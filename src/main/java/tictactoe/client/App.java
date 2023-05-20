package tictactoe.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class App {
  private static Socket socket;

  public static void main(String[] clArgs) {
    try {
      socket = IO.socket("http://localhost:9092");
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

      enterGame();

    } catch (

    URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private static void enterGame() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter yout name: ");
    String name = scanner.nextLine();

    socket.emit("entergame", name, (Ack) args -> {
      JSONObject response = (JSONObject) args[0];
      System.out.println(response);
      try {
        String result = response.getString("result");
        if (result.equals("player1")) {
          System.out.println("You are player 1, waiting for other player...");
        } else if (result.equals("player2")) {
          System.out.println("You are player 2, the game is about to start.");
        } else if (result.equals("nameAlreadyUsed")) {
          System.out.println("This name is already used");
          enterGame();
        } else if (result.equals("roomFull")) {
          System.out.println("The room is full");
        }
      } catch (JSONException e) {
      }
    });

    scanner.close();
  }
}
