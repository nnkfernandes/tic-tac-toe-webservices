package tictactoe.server;

import java.util.Optional;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import tictactoe.shared.Board;

/**
 * Hello world!
 *
 */
public class App {

    private static SocketIOServer server;
    private static Optional<Player> player1 = Optional.empty();
    private static Optional<Player> player2 = Optional.empty();

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        server = new SocketIOServer(config);
        server.addEventListener("entergame", String.class, (client, name, ack) -> {
            if (player1.isEmpty()) {
                player1 = Optional.of(new Player(client, name));
                ack.sendAckData("player1");
            } else if (player2.isEmpty()) {
                if (name == player1.get().getName()) {
                    ack.sendAckData("nameAlreadryUsed");
                } else {
                    player2 = Optional.of(new Player(client, name));
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
        player1.get().getSocket().sendEvent("makeMove", new AckCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                // Handle acknowledgment from the client
            }

            @Override
            public void onTimeout() {
                // Handle acknowledgment timeout (optional)
            }
        });
    }

    public Board handleMove() {

    }

}
