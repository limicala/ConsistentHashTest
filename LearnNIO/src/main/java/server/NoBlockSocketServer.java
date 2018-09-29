package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NoBlockSocketServer {
    private final int PORT = 9000;
    private Selector selector;

    public void init() {

        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            selector = Selector.open();

            channel.socket().bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false);

            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {

                try {
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                        while (iterator.hasNext()){

                            SelectionKey key = iterator.next();
                            iterator.remove();

                            if (key.isAcceptable()){
                                accept(key);
                            }

                            if (key.isReadable()){
                                read(key);
                            }

                            if (key.isWritable()){
                                write(key);
                            }

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void accept(SelectionKey key){
        //System.out.println("accept");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE)
                    .attach(new Message());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();

        Message message = (Message) key.attachment();

        ByteBuffer head = message.getHead();

        client.read(head);

        if (head.hasRemaining() || message.invalidHead()) return;

        //System.out.println("read");
        int size = 0;
        try {
            String s = new String(head.array());
            //System.out.println("sizeStr : [" + s + "]");
            size = Integer.parseInt(s);
        }catch (NumberFormatException ne){
            ne.printStackTrace();
        }
        if (size == 0) {
            head.clear();
            return;
        }

        ByteBuffer body = message.getBody();

        if (body == null){
            body = ByteBuffer.allocate(size);
            message.setBody(body);
        }

        if (body.hasRemaining()) {
            client.read(body);
        }
    }

    private void write(SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();

        Message message = (Message) key.attachment();

        ByteBuffer head = message.getHead();
        ByteBuffer body = message.getBody();

        if (head.hasRemaining() || body == null || body.hasRemaining()) return;

        //System.out.println("write");

        body.flip();

        try {
            while (body.hasRemaining()){
                client.write(body);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        head.clear();
        message.setBody(null);
    }

    public static void main(String[] args) {
        new NoBlockSocketServer().init();
    }

}