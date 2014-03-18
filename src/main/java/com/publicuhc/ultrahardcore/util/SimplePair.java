package com.publicuhc.ultrahardcore.util;

public class SimplePair<K, V> {

    private final K m_key;

    /**
     * @return the key for the pair
     */
    public K getKey() {
        return m_key;
    }

    private final V m_value;

    /**
     * @return the value for the pair
     */
    public V getValue() {
        return m_value;
    }

    /**
     * A Pair of values
     * @param key the key
     * @param value the value
     */
    public SimplePair(K key, V value) {
        m_key = key;
        m_value = value;
    }
}