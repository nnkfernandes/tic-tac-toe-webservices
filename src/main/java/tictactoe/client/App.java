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
  private static String p1Name;
  private static String p2Name;
  private static int p1Score = 0;
  private static int p2Score = 0;

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

      socket.on("setPlayers", args -> {
        try {
          JSONObject names = new JSONObject((String) args[0]);
          p1Name = names.getString("player1");
          p2Name = names.getString("player2");
        } catch (JSONException e) {
        }
      });
      socket.on("updateScore", args -> {
        try {
          JSONObject scores = new JSONObject((String) args[0]);
          p1Score = scores.getInt("player1");
          p2Score = scores.getInt("player2");
        } catch (JSONException e) {
        }
      });
      socket.on("updateBoard", args -> {
        String board = (String) args[0];
        String scoreStr = "| SCORE | " + p1Name + ": " + p1Score + " VS " + p2Name + ": "
            + p2Score + " |";
        System.out.println("-".repeat(scoreStr.length()));
        System.out.println(scoreStr);
        System.out.println("-".repeat(scoreStr.length()));
        System.out.println("\n");
        System.out.println(board);
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
