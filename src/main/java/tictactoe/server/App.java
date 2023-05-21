package tictactoe.server;

import java.util.Optional;

import org.json.JSONException;
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

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        Server server = new Server(config);
    }

}
