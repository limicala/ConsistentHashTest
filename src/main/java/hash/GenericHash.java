package hash;

public interface GenericHash<K, N> {
    N get(K key);
    boolean remove(N node);

    String getLabel();
}
