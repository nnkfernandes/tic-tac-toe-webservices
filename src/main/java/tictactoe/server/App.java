package tictactoe.server;

import com.corundumstudio.socketio.Configuration;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        Server server = new Server(config);
    }

}
