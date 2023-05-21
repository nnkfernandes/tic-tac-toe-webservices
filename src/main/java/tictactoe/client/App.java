package tictactoe.client;

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class App {

  public static void main(String[] clArgs) {
    try {
      Socket socket = IO.socket("http://localhost:9092");
      Client client = new Client(socket);

      client.start();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

}
