package tictactoe.server;

import java.util.Optional;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import tictactoe.shared.Board;
import tictactoe.shared.TileState;

/**
 * Hello world!
 *
 */
public class App {

    private static SocketIOServer server;
    private static Optional<Player> player1 = Optional.empty();
    private static Optional<Player> player2 = Optional.empty();
    private static Board board = new Board();

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        server = new SocketIOServer(config);
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

    public static void startGame() {
        try {
            JSONObject players = new JSONObject();
            players.put("player1", player1.get().getName());
            players.put("player2", player2.get().getName());
            server.getBroadcastOperations().sendEvent("setPlayers", players.toString());
        } catch (Exception e) {
        }
        server.getBroadcastOperations().sendEvent("updateBoard", new Board().toString());
        handleMove(player1.get());
    }

    public static void handleMove(Player player) {
       player.getSocket().sendEvent("makeMove", new AckCallback<Integer>(Integer.class) {
            @Override
            public void onSuccess(Integer pos) {
                TileState tile;

                try {
                    tile = board.getTile(pos);
                } catch (IndexOutOfBoundsException e) {
                    player.getSocket().sendEvent("invalidMove");
                    handleMove(player);
                    return;
                }
                if (tile != TileState.EMPTY) {
                    player.getSocket().sendEvent("tileNotMT");
                    handleMove(player);
                    return;
                }

                System.out.println("player " + player.getName() + " made move at position " + pos);
                
                board.setTile(pos, player.getTile());

                if (checkVictory(pos)) {
                    player.getSocket().sendEvent("victory");
                    player.setScore(player.getScore() + 1); 
                } else {

                }
                    
            }

            @Override
            public void onTimeout() {
                // Handle acknowledgment timeout (optional)
            }
        });
    }

    public static boolean checkVictory(int pos) {
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
