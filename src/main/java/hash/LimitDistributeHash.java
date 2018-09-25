package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LimitDistributeHash<K, N> implements GenericHash<K, N> {

    private final int BOUND;
    private final HashFunction hasher;
    private List<N> nodes;
    private Funnel<N> nodeFunnel;
    private Funnel<K> keyFunnel;

    public LimitDistributeHash(HashFunction hashFunction, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes){
        this.hasher = hashFunction;
        this.nodeFunnel = nodeFunnel;
        this.keyFunnel = keyFunnel;
        this.nodes = new ArrayList<N>(nodes);
        this.BOUND = nodes.size() + 10;
    }

    public boolean add(N node){
        int i = 0;
        while (i < nodes.size() && nodes.get(i) != null) i++;
        return nodes.set(i, node) == node;
    }

    public boolean remove(N node){
        int i = nodes.indexOf(node);
        if (i < 0) return false;
        return nodes.set(i, null) == null;
    }

    public N get(K key){
        Random random = new Random(key.hashCode());
        //Random random = new Random(hasher.newHasher().putObject(key, keyFunnel).hash().asLong());
        int index = -1;
        do {
            index = random.nextInt(BOUND);
        } while (index >= nodes.size() || nodes.get(index) == null);
        return nodes.get(index);
    }

    @Override
    public String getLabel() {
        return "LimitDistributeHash";
    }
}
