package tictactoe.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import tictactoe.shared.Board;

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

      socket.on("players", args -> {
        try {
          JSONObject response = new JSONObject((String) args[0]);
          System.out.println(response);
        } catch (JSONException e) {
        }
      });

      socket.on("updateBoard", args -> {
        System.out.println((String) args[0]);
        // Print the board to screen
      });
      
      socket.on("makeMove", args -> {
        System.out.println(args[0]);
        if (args.length > 1 && args[1] instanceof Ack) {
          // pedir movimento para o jogador
          // TODO
          // mandar movimento para o servidor
          ((Ack) args[1]).call("movimento do jogador");
        }
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
      String response = (String) args[0];
      if (response.equals("player1")) {
        System.out.println("You are player 1, waiting for other player...");
      } else if (response.equals("player2")) {
        System.out.println("You are player 2, the game is about to start.");
      } else if (response.equals("nameAlreadyUsed")) {
        System.out.println("This name is already used");
        enterGame();
      } else if (response.equals("roomFull")) {
        System.out.println("The room is full");
      }
    });

    scanner.close();
  }
}
