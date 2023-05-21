package tictactoe.server;

import com.corundumstudio.socketio.SocketIOClient;

import tictactoe.shared.TileState;

public class Player {

    private SocketIOClient socket;
    private String name;
    private TileState tile;
    private int score = 0;

    public Player(SocketIOClient socket, String name, TileState tile) {
        this.name = name;
        this.socket = socket;
        this.tile = tile;
    }

    public SocketIOClient getSocket() {
        return socket;
    }

    public void setSocket(SocketIOClient socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TileState getTile() {
        return tile;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
