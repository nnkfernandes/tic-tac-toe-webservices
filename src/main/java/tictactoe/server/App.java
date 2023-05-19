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

    private static Optional<Player> player1;
    private static Optional<Player> player2;
    


    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("entergame", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String name, AckRequest ackRequest) {

                if (player1.isEmpty()) {
                    
                    player1.get().setSocket(client);
                } else if (player2.isEmpty()) {
                    player2.get().setSocket(client);
                } else {
                    System.out.println("The room is full.");
                }

                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("entergame", name);
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
