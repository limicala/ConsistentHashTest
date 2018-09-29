package server.http;

import java.nio.channels.SocketChannel;

public class RequestItem {
    public SocketChannel client;
    public byte[] data;

    public RequestItem(SocketChannel client, byte[] data) {
        this.client = client;
        this.data = data;
    }
}
