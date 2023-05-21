package tictactoe.server;

import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import tictactoe.shared.Board;
import tictactoe.shared.TileState;

public class Server {
  private SocketIOServer server;
  private Optional<Player> player1 = Optional.empty();
  private Optional<Player> player2 = Optional.empty();
  private Board board = new Board();

  public Server(Configuration configuration) {
    this.server = new SocketIOServer(configuration);

    server.addEventListener("entergame", String.class, (client, name, ack) -> {
      if (player1.isEmpty()) {
        player1 = Optional.of(new Player(client, name, TileState.CROSS));
        ack.sendAckData("player1");
      } else if (player2.isEmpty()) {
        if (name == player1.get().getName()) {
          ack.sendAckData("nameAlreadryUsed");
        } else {
          player2 = Optional.of(new Player(client, name, TileState.NOUGHT));
          player1.get().getSocket().sendEvent("opponentEntered", name);
          ack.sendAckData("player2");

          startGame();
        }
      } else {
        ack.sendAckData("roomFull");
      }

    });

    server.start();
  }

  public void startGame() {
    try {
      JSONObject players = new JSONObject();
      players.put("player1", player1.get().getName());
      players.put("player2", player2.get().getName());
      server.getBroadcastOperations().sendEvent("setPlayers", players.toString());
    } catch (Exception e) {
    }
    server.getBroadcastOperations().sendEvent("updateBoard", new Board().toString());

    handleMove(player1.get(), player2.get());
  }

  public void handleMove(Player player, Player opponent) {
    player.getSocket().sendEvent("makeMove", new AckCallback<Integer>(Integer.class) {
      @Override
      public void onSuccess(Integer pos) {
        TileState tile;

        try {
          tile = board.getTile(pos);
        } catch (IndexOutOfBoundsException e) {
          player.getSocket().sendEvent("invalidMove");
          handleMove(player, opponent);
          return;
        }
        if (tile != TileState.EMPTY) {
          player.getSocket().sendEvent("tileNotEmpty");
          handleMove(player, opponent);
          return;
        }

        System.out.println("player " + player.getName() + " made move at position " + pos);

        board.setTile(pos, player.getTile());
        server.getBroadcastOperations().sendEvent("updateBoard", board.toString());

        if (checkVictory(pos)) {
          System.out.println("player " + player.getName() + " won this round");
          player.getSocket().sendEvent("victory");
          opponent.getSocket().sendEvent("defeat");
          player.setScore(player.getScore() + 1);
          try {
            JSONObject scores = new JSONObject();
            scores.put("player1", player1.get().getScore());
            scores.put("player2", player2.get().getScore());
            server.getBroadcastOperations().sendEvent("updateScore", scores.toString());
          } catch (JSONException e) {
          }
          board.clean();
          server.getBroadcastOperations().sendEvent("updateBoard", board.toString());
        } else if (checkTie()) {
          System.out.println("Old lady!");
          player.getSocket().sendEvent("tie");
          opponent.getSocket().sendEvent("tie");
          board.clean();
          server.getBroadcastOperations().sendEvent("updateBoard", board.toString());
        }

        player.getSocket().sendEvent("waiting");
        handleMove(opponent, player);
      }

      @Override
      public void onTimeout() {
        // Handle acknowledgment timeout (optional)
      }
    });
  }

  public boolean checkVictory(int pos) {
    TileState tile = board.getTile(pos);
    switch (pos) {
      case 0:
        return ((board.getTile(1) == tile && board.getTile(2) == tile)
            || (board.getTile(4) == tile && board.getTile(8) == tile)
            || (board.getTile(3) == tile && board.getTile(6) == tile));
      case 1:
        return ((board.getTile(0) == tile && board.getTile(2) == tile)
            || (board.getTile(4) == tile && board.getTile(7) == tile));
      case 2:
        return ((board.getTile(0) == tile && board.getTile(1) == tile)
            || (board.getTile(4) == tile && board.getTile(6) == tile)
            || (board.getTile(5) == tile && board.getTile(8) == tile));
      case 3:
        return ((board.getTile(4) == tile && board.getTile(5) == tile)
            || (board.getTile(0) == tile && board.getTile(6) == tile));
      case 4:
        return ((board.getTile(3) == tile && board.getTile(5) == tile)
            || (board.getTile(1) == tile && board.getTile(7) == tile)
            || (board.getTile(0) == tile && board.getTile(8) == tile)
            || (board.getTile(3) == tile && board.getTile(6) == tile));
      case 5:
        return ((board.getTile(3) == tile && board.getTile(4) == tile)
            || (board.getTile(2) == tile && board.getTile(8) == tile));
      case 6:
        return ((board.getTile(7) == tile && board.getTile(8) == tile)
            || (board.getTile(0) == tile && board.getTile(3) == tile)
            || (board.getTile(2) == tile && board.getTile(4) == tile));
      case 7:
        return ((board.getTile(6) == tile && board.getTile(8) == tile)
            || (board.getTile(1) == tile && board.getTile(4) == tile));
      case 8:
        return ((board.getTile(6) == tile && board.getTile(7) == tile)
            || (board.getTile(2) == tile && board.getTile(5) == tile)
            || (board.getTile(0) == tile && board.getTile(4) == tile));
      default:
        return false;
    }
  }

  private boolean checkTie() {
    return !board.stream().anyMatch(TileState.EMPTY::equals);
  }
}
