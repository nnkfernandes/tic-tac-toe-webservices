package tictactoe.server;

import java.util.Optional;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

/**
 * Hello world!
 *
 */
public class App {

    private static Optional<Player> player1 = Optional.empty();
    private static Optional<Player> player2 = Optional.empty();

    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("entergame", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String name, AckRequest ackRequest) {

                JSONObject response = new JSONObject();
                try {
                    if (player1.isEmpty()) {
                        player1 = Optional.of(new Player(client, name));
                        response.put("result", "player1");
                    } else if (player2.isEmpty()) {
                        if (name == player1.get().getName()) {
                            response.put("result", "nameAlreadryUsed");
                        } else {
                            player2 = Optional.of(new Player(client, name));
                            player1.get().getSocket().sendEvent("opponentEntered", name);
                            response.put("result", "player2");

                            JSONObject players = new JSONObject();
                            players.put("player1", player1.get().getName());
                            players.put("player2", name);
                            server.getBroadcastOperations().sendEvent("players", players);
                        }
                    } else {
                        response.put("result", "roomFull");
                    }
                } catch (Exception e) {
                }
                ackRequest.sendAckData(new Object[] { (Object) response });
            }
        });

        server.start();

        

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
