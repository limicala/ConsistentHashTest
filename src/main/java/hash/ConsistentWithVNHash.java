package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.*;

public class ConsistentWithVNHash<K, N> implements GenericHash<K, N> {

    private final HashFunction hashFunction;
    private final SortedMap<Long, N> ring = new TreeMap<Long, N>();
    private final Map<N, HashSet<Long>> recordMap = new HashMap();
    private int virtualNodeCount;
    private Funnel<N> nodeFunnel;
    private Funnel<K> keyFunnel;

    public ConsistentWithVNHash(int vCount, HashFunction hashFunction, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes) {
        this.hashFunction = hashFunction;
        this.nodeFunnel = nodeFunnel;
        this.keyFunnel = keyFunnel;
        this.virtualNodeCount = vCount;
        for (N node : nodes) {
            add(node);
        }
    }

    public boolean add(N node) {
        HashSet<Long> set = new HashSet<>();
        for (int i = 0; i < virtualNodeCount; i++){
            Long l = hashFunction.newHasher().putObject(node, nodeFunnel).putInt(i).hash().asLong();
            set.add(l);
            ring.put(l, node);
        }
        recordMap.put(node, set);
        return true;
    }

    public boolean remove(N node) {
        Set<Long> set = recordMap.get(node);
        if (null == set) return false;
        for (Long l : set){
            if(!node.equals(ring.remove(l))){
                return false;
            }
        }
        return true;
    }

    public N get(K key) {
        Long hash = hashFunction.newHasher().putObject(key, keyFunnel).hash().asLong();
        if (!ring.containsKey(hash)) {
            SortedMap<Long, N> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    @Override
    public String getLabel() {
        return "ConsistentWith" + virtualNodeCount + "nodes";
    }
}
