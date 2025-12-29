package bstmap;

import edu.princeton.cs.algs4.BST;

import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode(K k, V v) {
            key = k;
            value = v;
            left = null;
            right = null;
        }
    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode p = root;
        return containsKey(p, key);
    }

    private boolean containsKey(BSTNode p, K key) {
        if (p == null) {
            return false;
        }

        int cmp = key.compareTo(p.key);
        if (cmp == 0) {
            return true;
        } else if (cmp > 0) {
            return containsKey(p.right, key);
        } else {
            return containsKey(p.left, key);
        }
    }

    @Override
    public V get(K key) {
        BSTNode p = root;
        return get(p, key);
    }

    private V get(BSTNode p, K key) {
        if (p == null) {
            return null;
        }

        int cmp = key.compareTo(p.key);
        if (cmp == 0) {
            return p.value;
        } else if (cmp > 0) {
            return get(p.right, key);
        } else {
            return get(p.left, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
        } else {
            BSTNode p = root;
            insert(p, key, value);
        }
        size += 1;
    }

    private BSTNode insert(BSTNode p, K key, V value) {
        if (p == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(p.key);
        if (cmp > 0) {
            p.right = insert(p.right, key, value);
        } else if (cmp < 0) {
            p.left = insert(p.left, key, value);
        }
        return p;
    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new TreeSet<>();
        BSTNode p = root;
        keySet(p, s);
        return s;
    }

    private void keySet(BSTNode p, Set<K> s) {
        if (p == null) {
            return;
        }

        keySet(p.left, s);
        s.add(p.key);
        keySet(p.right, s);
    }

    @Override
    public V remove(K key) {
        BSTNode parent = null;
        BSTNode child = root;
        return remove(parent, child, key);
    }

    private BSTNode findPrevParent(BSTNode p) {
        BSTNode parent = null;
        while (p.right != null) {
            parent = p;
            p = p.right;
        }
        return parent;
    }

    private V removeNode(BSTNode parent, BSTNode child) {
        V removeValue = null;
        if (parent == null) {
            // 说明要删除的是根节点
            removeValue = root.value;
            if (child.left == null && child.right == null) {
                root = null;
            } else if (child.left != null && child.right != null) {
                BSTNode prevParent = findPrevParent(child.left);
                BSTNode prev = prevParent.right;
                prevParent.right = prev.left;
                prev.left = root.left;
                prev.right = root.right;
                root.left = null;
                root.right = null;
                root = prev;
            } else {
                if (child.left == null) {
                    root = child.right;
                } else {
                    root = child.left;
                }
            }
        } else {
            removeValue = child.value;
            int cmp = child.key.compareTo(parent.key);
            if (child.left == null && child.right == null) {
                if (cmp < 0) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            } else if (child.left != null && child.right != null) {
                BSTNode prevParent = findPrevParent(child.left);
                BSTNode prev = prevParent.right;
                prevParent.right = prev.left;
                prev.left = child.left;
                prev.right = child.right;
                child.left = null;
                child.right = null;
                if (cmp < 0) {
                    parent.left = prev;
                } else {
                    parent.right = prev;
                }
            } else {
                if (cmp < 0) {
                    if (child.left == null) {
                        parent.left = child.right;
                    } else {
                        parent.left = child.left;
                    }
                } else {
                    if (child.left == null) {
                        parent.right = child.right;
                    } else {
                        parent.right = child.left;
                    }
                }
            }
        }
        return removeValue;
    }

    private V remove(BSTNode parent, BSTNode child, K key) {
        if (child == null) {
            return null;
        }

        int cmp = key.compareTo(child.key);
        V removeValue = null;
        if (cmp > 0) {
            removeValue = remove(child, child.right, key);
        } else if (cmp < 0) {
            removeValue = remove(child, child.left, key);
        } else {
            removeValue = removeNode(parent, child);
        }
        return removeValue;
    }

    @Override
    public V remove(K key, V value) {
        BSTNode parent = null;
        BSTNode child = root;
        return remove(parent, child, key, value);
    }

    private V remove(BSTNode parent, BSTNode child, K key, V value) {
        if (child == null) {
            return null;
        }

        int cmp = key.compareTo(child.key);
        V removeValue = null;
        if (cmp > 0) {
            removeValue = remove(child, child.right, key, value);
        } else if (cmp < 0) {
            removeValue = remove(child, child.left, key, value);
        } else {
            if (!child.value.equals(value)) {
                return null;
            }
            removeValue = removeNode(parent, child);
        }
        return removeValue;
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> s = keySet();
        return s.iterator();
    }

    public void printInOrder() {
        BSTNode p = root;
        printInOrder(p);
    }

    private void printInOrder(BSTNode p) {
        if (p == null) {
            return;
        }

        printInOrder(p.left);
        System.out.println(p.key);
        printInOrder(p.right);
    }
}