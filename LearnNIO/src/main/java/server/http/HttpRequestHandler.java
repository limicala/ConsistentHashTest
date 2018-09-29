package server.http;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static server.http.HttpResponseBuilder.*;

public class HttpRequestHandler implements Runnable{

    private List<RequestItem> pendingRequests = new LinkedList<>();
    private NioHttpSimpleServer server;

    public HttpRequestHandler(NioHttpSimpleServer server){
        this.server = server;
    }

    public void newRequest(SocketChannel client, byte[] bytes, int numRead){
        byte[] copy = new byte[numRead];
        System.arraycopy(bytes, 0, copy, 0, numRead);

        synchronized (pendingRequests){
            pendingRequests.add(new RequestItem(client, copy));
            pendingRequests.notify();
        }
    }

    @Override
    public void run() {

        while (true) {
            synchronized (pendingRequests) {
                try {
                    while (pendingRequests.isEmpty()){
                        pendingRequests.wait();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                RequestItem request = pendingRequests.remove(0);

                HttpResponseBuilder builder = new HttpResponseBuilder();

                String body = "<html><head>hello!</br></head></html>";
                builder.addHeader(CONNECTION, KEEP_ALIVE);
                builder.addHeader(CONTENT_TYPE, "text/html");
                builder.addHeader(CONTENT_LENGTH, body.length());
                //server.send(request.client, request.data);
                server.send(request.client, builder.toString().getBytes());
                server.send(request.client, body.getBytes());
            }
        }
    }
}
