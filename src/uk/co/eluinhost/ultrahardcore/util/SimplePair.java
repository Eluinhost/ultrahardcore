package uk.co.eluinhost.ultrahardcore.util;

public class SimplePair<K, V> {

    private final K m_key;

    public K getKey() {
        return m_key;
    }

    private final V m_value;

    public V getValue() {
        return m_value;
    }

    public SimplePair(K key, V value) {
        m_key = key;
        m_value = value;
    }
}