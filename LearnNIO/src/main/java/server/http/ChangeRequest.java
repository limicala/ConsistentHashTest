package server.http;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ChangeRequest {

    public SocketChannel client;
    public int ops;

    public ChangeRequest(SocketChannel client, int ops){
        this.client = client;
        this.ops = ops;
    }
}
