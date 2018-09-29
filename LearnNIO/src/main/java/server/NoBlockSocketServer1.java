package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NoBlockSocketServer1 extends NoBlockSocketServer {

    private void write(SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();

        Message message = (Message) key.attachment();

        ByteBuffer head = message.getHead();
        ByteBuffer body = message.getBody();

        if (head.hasRemaining() || body == null || body.hasRemaining()) return;

        //System.out.println("write");

        body.flip();
        int lastPos = message.getWritedPos();

        body.position(lastPos);

        try {
            if (body.hasRemaining()){
                int r = client.write(body);
                message.setWritedPos(lastPos + r);
            }else{
                message.clear();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NoBlockSocketServer1().init();
    }
}
