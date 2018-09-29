package server;

import java.nio.ByteBuffer;

public class Message {
    private final int headSize = 5;
    private ByteBuffer head;
    private ByteBuffer body;
    private int writedPos;

    public Message(){
        head = ByteBuffer.allocate(headSize);
        body = null;
        writedPos = 0;
    }

    public ByteBuffer getHead() { return head; }
    public ByteBuffer getBody() { return body; }

    public void setBody(ByteBuffer body) {
        this.body = body;
    }

    public boolean invalidHead(){
        int i = headSize;
        for (; i > 0; i--){
            if (head.array()[i - 1] < 31) break;
        }
        if (i == 0) return false;

        //System.out.println("i:" + i);
        head.position(i);
        head.compact();
        return true;
    }

    public int getWritedPos() {
        return writedPos;
    }

    public void setWritedPos(int writedPos) {
        this.writedPos = writedPos;
    }

    public void clear(){
        head.clear();
        body = null;
        writedPos = 0;
    }
}
