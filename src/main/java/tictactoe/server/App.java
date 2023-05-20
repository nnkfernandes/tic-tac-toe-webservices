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

    private static Optional<Player> player1 = Optional.empty();
    private static Optional<Player> player2 = Optional.empty();

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("entergame", String.class, (client, name, ack) -> {
            String response = "";
            if (player1.isEmpty()) {
                player1 = Optional.of(new Player(client, name));
                response = "player1";
            } else if (player2.isEmpty()) {
                if (name == player1.get().getName()) {
                    response = "nameAlreadryUsed";
                } else {
                    player2 = Optional.of(new Player(client, name));
                    player1.get().getSocket().sendEvent("opponentEntered", name);
                    response = "player2";

                    try {
                        JSONObject players = new JSONObject();
                        players.put("player1", player1.get().getName());
                        players.put("player2", name);
                        server.getBroadcastOperations().sendEvent("players", players.toString());
                        server.getBroadcastOperations().sendEvent("updateBoard", new Board().toString());
                    } catch (Exception e) {
                    }
                }
            } else {
                response = "roomFull";
            }
            ack.sendAckData(response);
        });

        server.start();

    }
}
