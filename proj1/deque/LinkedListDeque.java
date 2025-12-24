package deque;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.junit.Test;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class DequeNode<T> {
        DequeNode<T> prev;
        DequeNode<T> next;
        T item;

        DequeNode(T item) {
            this.item = item;
            prev = null;
            next = null;
        }
    }

    private DequeNode<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new DequeNode<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        DequeNode<T> p = new DequeNode<>(item);
        p.prev = sentinel;
        p.next = sentinel.next;
        sentinel.next.prev = p;
        sentinel.next = p;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        DequeNode<T> p = new DequeNode<>(item);
        sentinel.prev.next = p;
        p.prev = sentinel.prev;
        p.next = sentinel;
        sentinel.prev = p;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        DequeNode<T> p = sentinel.next;
        while (p != sentinel) {
            System.out.println(p.item);
            p = p.next;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T x = sentinel.next.item;
        sentinel.next.prev = null;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev.next = null;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T x = sentinel.prev.item;
        sentinel.prev.next = null;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next.prev = null;
        sentinel.prev.next = sentinel;
        size -= 1;
        return x;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size - 1 || isEmpty()) {
            return null;
        }
        DequeNode<T> p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index--;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index > size - 1 || isEmpty()) {
            return null;
        }
        return getRecursive(sentinel.next, index);
    }

    public T getRecursive(DequeNode<T> p, int index) {
        if (index == 0) {
            return p.item;
        } else {
            return getRecursive(p.next, index - 1);
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;
        private DequeNode<T> p;

        public LinkedListDequeIterator() {
            wizPos = 0;
            p = sentinel.next;
        }

        public boolean hasNext() {
            if (wizPos < size) {
                return true;
            }
            return false;
        }

        public T next() {
            T x = p.item;
            wizPos += 1;
            p = p.next;
            return x;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<T> o = (LinkedListDeque<T>) object;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0, size = this.size(); i < size; i++) {
            if (!o.get(i).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }
}
