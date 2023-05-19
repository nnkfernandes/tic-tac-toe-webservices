package tictactoe.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class App {
  public static void main(String[] clArgs) {
    try {
      Socket socket = IO.socket("http://localhost:9092");
      socket.on(Socket.EVENT_CONNECT, (args) -> {
        // System.out.println("Connected to server");
        // Perform actions upon successful connection
      });

      socket.on(Socket.EVENT_DISCONNECT, (args) -> {
        System.out.println("Disconnected from server");
        // Perform actions upon disconnection
      });
      socket.on("entergame", (args) -> {
        System.out.println("Enter game");
        // Process the received message
      });

      // Connect to the server
      socket.connect();

      Scanner scanner = new Scanner(System.in);

      System.out.print("Enter yout name: ");
      String name = scanner.nextLine();

      socket.emit("entergame", name);

      // Send a message to the server
      socket.emit("chatevent", "Hello, server!");
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
