package server;

import java.nio.ByteBuffer;

public class ByteBufferTest {

    /**
     * flip()
     * compact()
     * get()
     * put()
     * clear()
     * hasRemaining()
     */

    public static void main(String[] args) {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        readBuffer.put("00005aa".getBytes());

        readBuffer.flip();


        byte[] bytes = new byte[5];
        readBuffer.get(bytes, 0, 5);
        String a = new String(bytes);

        System.out.println(a);

        System.out.println(readBuffer.limit() + " " + readBuffer.position());

        readBuffer.clear();

        System.out.println(readBuffer.limit() + " " + readBuffer.position());

    }
}
