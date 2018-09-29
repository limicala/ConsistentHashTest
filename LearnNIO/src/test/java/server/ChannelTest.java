package server;


import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelTest {

    public static void fileChannelTest() throws Exception{
        RandomAccessFile aFile = new RandomAccessFile("/Users/limicala/Documents/b", "rw");
        FileChannel inChannel = aFile.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {
            buf.flip();

            while(buf.hasRemaining()){
                System.out.print((char) buf.get());
            }

            buf.clear();
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }

    public static void main(String[] args) {
        Map<String, List<Integer>> map = new HashMap<>();

        List<Integer> list = map.get("aa");

        System.out.println(list == null);

        list = new ArrayList<>();

        List<Integer> list1 = map.get("aa");
        System.out.println(list1 == null);
    }
}
