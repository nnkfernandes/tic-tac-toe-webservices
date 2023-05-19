package tictactoe.server;
import java.lang.reflect.Constructor;
import java.net.Socket;

import com.corundumstudio.socketio.SocketIOClient;

public class Player {

    private SocketIOCliente socket;
    private String name;
    public Player(SocketIOClient socket, String name) {
        this.name = name;
        this.socket = socket;
    }

    public SocketIOCliente getSocket() {
        return socket;
    }
    public void setSocket(SocketIOCliente socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}