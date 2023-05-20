package tictactoe.server;

import java.util.Optional;

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

                if (player1.isEmpty()) {
                    player1 = Optional.of(new Player(client, name));
                    client.sendEvent("entergame", name);
                } else if (player2.isEmpty()) {
                    player2 = Optional.of(new Player(client, name));
                    player1.get().getSocket().sendEvent("opponentEntered", name);
                } else {
                    client.sendEvent("roomFull");
                }
            }
        });

        server.start();

        

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
