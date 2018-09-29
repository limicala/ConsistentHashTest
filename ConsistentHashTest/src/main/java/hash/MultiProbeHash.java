package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class MultiProbeHash<K, N> implements GenericHash<K, N> {

    private static final int TIMES = 21;
    private final HashFunction hashFunction;
    private final SortedMap<Long, N> ring = new TreeMap<Long, N>();
    private Funnel<N> nodeFunnel;
    private Funnel<K> keyFunnel;

    public MultiProbeHash(HashFunction hashFunction, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes) {
        this.hashFunction = hashFunction;
        this.nodeFunnel = nodeFunnel;
        this.keyFunnel = keyFunnel;
        for (N node : nodes) {
            add(node);
        }
    }

    public boolean add(N node) {
        ring.put(hashFunction.newHasher().putObject(node, nodeFunnel).hash().asLong(), node);
        return true;
    }

    @Override
    public N get(K key) {
        long minDistance = Long.MAX_VALUE;
        long nodeHash = 0;
        for(int i = 0; i < TIMES; i++){
            long tmpHash = hashFunction.newHasher().putObject(key, keyFunnel).putInt(i).hash().asLong();
            if(ring.containsKey(tmpHash)) return ring.get(tmpHash);

            SortedMap<Long, N> tailMap = ring.tailMap(tmpHash);
            long tmpNodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();

            long result = Math.abs(tmpHash - tmpNodeHash);
            if(result < minDistance){
                minDistance = result;
                nodeHash = tmpNodeHash;
            }
        }
        return ring.get(nodeHash);
    }

    @Override
    public boolean remove(N node) {
        return node.equals(ring.remove(hashFunction.newHasher().putObject(node, nodeFunnel).hash().asLong()));
    }

    @Override
    public String getLabel() {
        return "MultiProbeHash";
    }
}
