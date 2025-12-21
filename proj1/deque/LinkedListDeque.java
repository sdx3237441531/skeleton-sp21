package deque;

public class LinkedListDeque<T> {
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

    LinkedListDeque() {
        sentinel = new DequeNode<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        DequeNode<T> p = new DequeNode<>(item);
        p.prev = sentinel;
        p.next = sentinel.next;
        sentinel.next.prev = p;
        sentinel.next = p;
        size += 1;
    }

    public void addLast(T item) {
        DequeNode<T> p = new DequeNode<>(item);
        sentinel.prev.next = p;
        p.prev = sentinel.prev;
        p.next = sentinel;
        sentinel.prev = p;
        size += 1;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        DequeNode<T> p = sentinel.next;
        while (p != sentinel) {
            System.out.println(p.item);
            p = p.next;
        }
    }

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
}
