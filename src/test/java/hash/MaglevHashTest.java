package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import io.whitfin.siphash.SipHasher;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MaglevHashTest {
    private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel(Charset.defaultCharset());
    @Test
    public void printPermutationAndLookUp(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            list.add("node" + i);
        }

        MaglevHash<String, String> hash = new MaglevHash(11,strFunnel, strFunnel,list);
        hash.get("c");
        hash.print();

//        hash.remove("node2");
//        hash.get("c");
//        hash.print();
    }

    @Test
    public void printOffset(){
        String node = "node2";
        int mSizeLookup = 17;
        long h = SipHasher.hash("0123456789ABCDEF".getBytes(), node.getBytes());
        int offset = (int) ((h >>> 32) % mSizeLookup);
        int skip = Math.abs((int) (h & 0xffffffff) % (mSizeLookup - 1) + 1);

        System.out.println(offset + " " + skip);
    }
}
