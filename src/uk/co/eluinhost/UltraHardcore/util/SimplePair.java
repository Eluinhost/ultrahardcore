package uk.co.eluinhost.UltraHardcore.util;

public class SimplePair<K, V> {

    private K key;

    public K getKey() {
        return key;
    }

    private V value;

    public V getValue() {
        return value;
    }

    public SimplePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}