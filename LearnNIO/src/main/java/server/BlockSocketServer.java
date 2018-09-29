package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockSocketServer{

    private final int PORT = 9000;

    public void init() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true){
                Socket socket = server.accept();

                new Thread(new Handler(socket)).start();
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    class Handler implements Runnable {
        private final Socket client;
        private InputStream in;
        private OutputStream out;

        public Handler(Socket socket){
            client = socket;
        }

        public void run() {
            try{
                Thread t = Thread.currentThread();
                in = client.getInputStream();
                out = client.getOutputStream();
                System.out.println("[" + t.getId() + "]:" + "accept new client");
                try {
                    while (true){
                        String buffer = safeRead(in, 5);
                        if (null == buffer){
                            System.out.println("[" + t.getId() + "]:" + "cant read buffer size");
                            break;
                        }
                        System.out.println("[" + t.getId() + "]:" + "size: " + buffer);

                        int size = Integer.parseInt(buffer);

                        buffer = safeRead(in, size);

                        if (null == buffer){
                            System.out.println("[" + t.getId() + "]:" + "cant read buffer content");
                        }
                        System.out.println("[" + t.getId() + "]:" + "content: " + buffer);
                        out.write(buffer.getBytes());
                    }

                }catch (Exception e){
                    System.out.println("[" + t.getId() + "]:" + "catch Exception, StackTrace[0]:" + e.getStackTrace()[0]);
                } finally {
                    in.close();
                    out.close();
                    client.close();
                }

            }catch (IOException e){
                e.printStackTrace();
            }

        }


    }


    public static String safeRead(InputStream in, int length) {
        byte[] msg = new byte[length];
        int numRead;
        int t;
        try {
            numRead = in.read(msg);
            if (numRead<=0) return null;

            while (numRead != length) {
                t = in.read(msg, numRead, length - numRead);
                if (t<=0) return null;
                numRead += t;
            }
        } catch (Exception e) {
            return null;
        }
        return new String(msg);
    }

    public static void main(String[] args) {
        BlockSocketServer server = new BlockSocketServer();

        server.init();
    }

}
