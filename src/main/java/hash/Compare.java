package hash;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * For comparing the load differences between consistent hash and HRW
 */
public class Compare {

	private static final int keySize  = 10000;
	private static final int nodeSize = 5;
	private static final int removeCount = 2;

	private static final HashFunction hfunc = Hashing.murmur3_128();
	private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel(Charset.defaultCharset());


	public static void main(String[] args) {

        Map<String, AtomicInteger> distribution = Maps.newHashMap();
		testHash(distribution, new ConsistentHash(hfunc, strFunnel, strFunnel, getNodes(distribution)));

        distribution = Maps.newHashMap();
        testHash(distribution, new ConsistentWithVNHash(100,hfunc, strFunnel, strFunnel, getNodes(distribution)));

		//distribution = Maps.newHashMap();
		//testHash(distribution, new RendezvousHash(hfunc, strFunnel, strFunnel, getNodes(distribution)));

		distribution = Maps.newHashMap();
		testHash(distribution, new LimitDistributeHash(hfunc, strFunnel, strFunnel, getNodes(distribution)));

		distribution = Maps.newHashMap();
		testHash(distribution, new MaglevHash(65537, strFunnel, strFunnel, getNodes(distribution)));

		//distribution = Maps.newHashMap();
		//testHash(distribution, new MaglevHash(23, strFunnel, strFunnel, getNodes(distribution)));

		distribution = Maps.newHashMap();
		testHash(distribution, new MultiProbeHash(hfunc, strFunnel, strFunnel, getNodes(distribution)));
//        System.out.println("======: JumpHash :========");
//        distribution = Maps.newHashMap();
//        JumpHash<String, String> j = new JumpHash(hfunc, strFunnel, strFunnel, getNodes(distribution));
//        putKeys(distribution, j);
//
//        System.out.println("====== remove 2 ========");
//        removeNodes(distribution, j, removeCount);
//        putKeys(distribution, j);

	}

	private static void testHash(Map<String, AtomicInteger> distribution, GenericHash hash){
        System.out.println("======: " + hash.getLabel() + " :========");
        putKeys(distribution, hash);

        HashMap<String, Object> map1 = statistic(hash);

        System.out.println("====== remove " + removeCount + " ========");
        removeNodes(distribution, hash, removeCount);
        putKeys(distribution, hash);

		HashMap<String, Object> map2 = statistic(hash);

		System.out.println("changed count : " + diff(map1, map2));
    }
	
	private static List<String> getNodes(Map<String, AtomicInteger> distribution) {
		List<String> nodes = Lists.newArrayList();
		for(int i = 0 ; i < nodeSize; i ++) {
			nodes.add("Node"+i);
			distribution.put("Node"+i, new AtomicInteger());
		}
		return nodes;
	}

	private static void putKeys(Map<String, AtomicInteger> distribution, GenericHash hash){
		for(int i = 0 ; i < keySize; i++) {
			distribution.get(hash.get(""+i)).incrementAndGet();
		}
		int max = 0, size = 0;
		for(Entry<String, AtomicInteger> e : distribution.entrySet()) {
			System.out.println(e.getKey() + "," + e.getValue().get());
			if (e.getValue().get() > max){
				max = e.getValue().get();
			}
			size += e.getValue().get();
			e.getValue().set(0);
		}

		double mean = (size * 1.0) / distribution.size();
		System.out.printf("max=%d, mean=%f, peak-to-mean=%f\n", max, mean, max / mean);
	}

	private static void removeNodes(Map<String, AtomicInteger> distribution, GenericHash hash, int count){
		for(int i = 0 ; i < count; i ++) {
            hash.remove("Node"+i);
			distribution.remove("Node"+i);
		}
	}

	private static HashMap<String, Object> statistic(GenericHash hash){
		HashMap<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < keySize; i++){
			String t = ""+i;
			map.put(t, hash.get(t));
		}
		return map;
	}

	private static int diff(HashMap<String, Object> map1, HashMap<String, Object> map2){
		int count = 0;

		assert map1.size() == map2.size();

		for (Entry<String, Object> entry : map1.entrySet()){
			if(!entry.getValue().equals(map2.get(entry.getKey()))){
				count += 1;
			}
		}

		return count;
	}
}
