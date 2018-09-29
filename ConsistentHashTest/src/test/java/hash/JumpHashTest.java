package hash;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JumpHashTest {

    @Test
    public void testListRemove(){
        List<Integer> list = new ArrayList<Integer>();

        for(int i = 0; i < 10; i++){
            list.add(i);
        }

        for(int i = 0; i < 10; i++){
            System.out.print(list.get(i) + " ");
        }
        System.out.println();
        Assert.assertEquals(new Integer(3), list.get(3));

        System.out.println(list.indexOf(new Integer(4)));
        list.set(1, null);
        list.set(3, null);
        for(int i = 0; i < 10; i++){
            System.out.print(list.get(i) + " ");
        }
        Assert.assertEquals(null, list.get(3));
    }

    @Test
    public void testBitMove(){
        int number = 2;
        int t = number >> 2;
        System.out.println(number + " " + t);
    }
}
