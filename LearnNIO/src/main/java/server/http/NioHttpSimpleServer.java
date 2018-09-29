package server.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioHttpSimpleServer implements Runnable{

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8912);
    private List<ChangeRequest> changeRequests = new LinkedList<ChangeRequest>();
    private Map<SocketChannel, List<ByteBuffer>> pendingSends = new HashMap<>();
    private List<HttpRequestHandler> requestHandlers = new ArrayList<>();

    public NioHttpSimpleServer(){
        try {
            serverChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverChannel.socket().bind(new InetSocketAddress(8080));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException oe){
            oe.printStackTrace();
        }
        System.out.println("server start");
    }

    @Override
    public void run() {

        while (true) {
            // update interestOps
            synchronized (changeRequests) {
                for (ChangeRequest change : changeRequests){
                    SelectionKey key = change.client.keyFor(selector);
                    if (key != null && key.isValid()){
                        key.interestOps(change.ops);
                    }

                }
                changeRequests.clear();
            }
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        accept(key);
                    }

                    if (key.isReadable()) {
                        read(key);
                    }
                    if (key.isValid() && key.isWritable()) {
                        write(key);
                    }
                }
            }catch (Exception oe){
                oe.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key){
        System.out.println("Accept");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            client.register(selector, SelectionKey.OP_READ);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void read(SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();

        readBuffer.clear();

        int numRead = -1;
        try {
            numRead = client.read(readBuffer);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("numRead:" + numRead);
        if (numRead == -1){
            // close
            key.cancel();
            client.close();
            return;
        }

        int workIndex = client.hashCode() % requestHandlers.size();

        System.out.println(workIndex + " | " + readBuffer.capacity() + " | " + numRead);

        //System.out.println(new String(readBuffer.array()));
        requestHandlers.get(workIndex).newRequest(client, readBuffer.array(), numRead);
    }

    private void write(SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();

        synchronized (pendingSends) {
            List<ByteBuffer> list = pendingSends.get(client);

            if (!list.isEmpty()){
                ByteBuffer send = list.get(0);

                try {
                    client.write(send);
                } catch (IOException e){

                }
                if (!send.hasRemaining()){
                    list.remove(0);
                }
            }

            if (list.isEmpty()){
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void send(SocketChannel client, byte[] data){
        synchronized (changeRequests){
            changeRequests.add(new ChangeRequest(client, SelectionKey.OP_WRITE));
            synchronized (pendingSends) {
                List<ByteBuffer> list = pendingSends.get(client);

                if (list == null) {
                    list = new LinkedList<>();
                    pendingSends.put(client, list);
                }

                list.add(ByteBuffer.wrap(data));

            }
        }
        selector.wakeup();
    }

    public void addHandler(HttpRequestHandler handler){
        requestHandlers.add(handler);
    }

    public static void main(String[] args) {

        NioHttpSimpleServer server = new NioHttpSimpleServer();
        HttpRequestHandler handler = new HttpRequestHandler(server);
        server.addHandler(handler);

        new Thread(handler, "work").start();
        new Thread(server, "selector").start();

    }
}
