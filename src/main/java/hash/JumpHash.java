package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JumpHash<K, N> implements GenericHash<K, N> {

    private static final long UNSIGNED_MASK = 0x7fffffffffffffffL;

    private static final long JUMP = 1L << 31;

    private static final long CONSTANT = Long.parseUnsignedLong("2862933555777941757");
    private final HashFunction hasher;
    private List<N> nodes;
    private Funnel<N> nodeFunnel;
    private Funnel<K> keyFunnel;

    public JumpHash(HashFunction hashFunction, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes){
        this.hasher = hashFunction;
        this.keyFunnel = keyFunnel;
        this.nodeFunnel = nodeFunnel;
        this.nodes = new ArrayList<N>(nodes);
    }
    @Override
    public N get(K key) {
        long k = key.hashCode();
        long b = -1;
        long j = 0;

        while (j < nodes.size()) {
            b = j;
            k = k * CONSTANT + 1L;

            j = (long) ((b + 1L) * (JUMP / toDouble((k >>> 33) + 1L)));
        }
        return nodes.get((int) b);
    }

    @Override
    public boolean remove(N node) {
        int index = nodes.indexOf(node);
        return index < 0 ? false : nodes.set(index, null) == null;
    }


    private static double toDouble(final long n) {
        double d = n & UNSIGNED_MASK;
        if (n < 0) {
            d += 0x1.0p63;
        }
        return d;
    }

    @Override
    public String getLabel() {
        return "JumpHash";
    }
}
