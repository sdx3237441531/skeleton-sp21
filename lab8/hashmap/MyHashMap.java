package hashmap;

import net.sf.saxon.expr.Component;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize = 16;
    private double maxLoad = 0.75;
    private int size = 0;

    /**
     * Constructors
     */
    public MyHashMap() {
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets = new Collection[tableSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        for (int i = 0; i < initialSize; i++) {
            buckets[i].clear();
        }
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        Collection<Node> bucket = buckets[Math.abs(key.hashCode()) % initialSize];
        Iterator<Node> seer = bucket.iterator();
        while (seer.hasNext()) {
            if (seer.next().key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        Collection<Node> bucket = buckets[Math.abs(key.hashCode()) % initialSize];
        Iterator<Node> seer = bucket.iterator();
        while (seer.hasNext()) {
            Node p = seer.next();
            if (p.key.equals(key)) {
                return p.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void expand() {
        Collection<Node> nodes = createBucket();
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> bucket = buckets[i];
            Iterator<Node> seer = bucket.iterator();
            while (seer.hasNext()) {
                nodes.add(seer.next());
            }
            buckets[i].clear();
        }
        initialSize = initialSize * 2;
        size = 0;
        buckets = createTable(initialSize);
        Iterator<Node> seer = nodes.iterator();
        while (seer.hasNext()) {
            Node p = seer.next();
            put(p.key, p.value);
        }
    }

    @Override
    public void put(K key, V value) {
        Collection<Node> bucket = buckets[Math.abs(key.hashCode()) % initialSize];
        Iterator<Node> seer = bucket.iterator();
        while (seer.hasNext()) {
            Node p = seer.next();
            if (p.key.equals(key)) {
                p.value = value;
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;
        double loadFactor = (double) size / initialSize;
        if (loadFactor >= maxLoad) {
            expand();
        }
    }

    @Override
    public Set<K> keySet() {
        //return keySet;
        Set<K> setKey = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> bucket = buckets[i];
            Iterator<Node> seer = bucket.iterator();
            while (seer.hasNext()) {
                Node p = seer.next();
                setKey.add(p.key);
            }
        }
        return setKey;
    }

    @Override
    public V remove(K key) {
        V removeValue = null;
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> bucket = buckets[i];
            Iterator<Node> seer = bucket.iterator();
            while (seer.hasNext()) {
                Node p = seer.next();
                if (p.key.equals(key)) {
                    bucket.remove(p);
                    removeValue = p.value;
                }
            }
        }
        return removeValue;
    }

    @Override
    public V remove(K key, V value) {
        V removeValue = null;
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> bucket = buckets[i];
            Iterator<Node> seer = bucket.iterator();
            while (seer.hasNext()) {
                Node p = seer.next();
                if (p.key.equals(key) && p.value.equals(value)) {
                    bucket.remove(p);
                    removeValue = p.value;
                }
            }
        }
        return removeValue;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
