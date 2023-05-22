package tictactoe.client;

import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class Client {
  private Socket socket;
  private String p1Name;
  private String p2Name;
  private int p1Score = 0;
  private int p2Score = 0;
  private static Scanner scanner = new Scanner(System.in);

  public Client(Socket socket) {
    this.socket = socket;
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
      // pedir movimento para o jogador
      int move = getMove();
      // mandar movimento para o servidor
      ((Ack) args[0]).call(move);
    });

    socket.on("victory", args -> {
      System.out.println("You won the game. :)");
      System.out.println("Let's start a new round!");
    });

    socket.on("defeat", args -> {
      System.out.println("You lost the game. :(");
      System.out.println("Let's start a new round!");
    });

    socket.on("tie", args -> {
      System.out.println("The old lady won");
      System.out.println("Let's start a new round!");
    });

    socket.on("waiting", args -> {
      System.out.println("Waiting for opponent move...");
    });

    socket.on("opponentDisconnect", args -> {
      System.out.println("Your opponent disconnected, waiting for other player to enter to restart game...");
    });

    socket.on("invalidMove", args -> {
      System.out.println("Invalid move! Please try again.");
    });
    socket.on("tileNotEmpty", args -> {
      System.out.println("Tile not empty, please choose another one.");
    });
  }

  public void start() {
    socket.connect();

    enterGame();
  }

  private static int getMove() {
    System.out.println("[Type your move (0-8) OR type 'q' to quit the game]:");

    while (true) {
      String input;
      input = scanner.nextLine();

      if (input.equals("q")) {
        scanner.close();
        System.exit(0);
      }
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.out.println("Please type your a valid number");
      }
    }
  }

  private void enterGame() {
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
  }
}
