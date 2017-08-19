package com.sermak.plugin.db.cdatatypes;

import java.io.Serializable;
import java.util.HashMap;

public class HashPair<K, V> implements Serializable {
    private HashMap<K, V> ktov;
    private HashMap<V, K> vtok;

    public HashPair() {
        ktov = new HashMap<>();
        vtok = new HashMap<>();
    }

    public void clear() {
        ktov.clear();
        vtok.clear();
    }

    public void put(K one, V two) {
        ktov.put(one, two);
        vtok.put(two, one);
    }

    public boolean containsFirst(K first) {
        return ktov.containsKey(first) && vtok.containsValue(first);
    }

    public boolean containsSecond(V second) {
        return ktov.containsValue(second) && vtok.containsKey(second);
    }

    public K getFirst(V second) {
        return vtok.get(second);
    }

    public V getSecond(K first) {
        return ktov.get(first);
    }
}
