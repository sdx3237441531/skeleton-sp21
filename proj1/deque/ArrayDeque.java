package deque;

import java.lang.reflect.Parameter;
import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T>{
    private int size;
    private int senFront; //指向前端元素所在的位置
    private int senBack; //指向后端元素所在的位置
    private T[] items;

    public ArrayDeque() {
        size = 0;
        senFront = -1;
        senBack = -1;
        items = (T[]) new Object[8];
    }

    public void addTheFirstItem(T item) {
        items[0] = item;
        size += 1;
        senFront = senBack = 0;
    }

    public void arrayCopy(T[] a, T[] items) {
        for (int i = 0; i < size; i++) {
            a[i] = items[senFront];
            senFront = (senFront + 1) % items.length;
        }
        senFront = 0;
        senBack = size - 1;
    }

    public void expand() {
        T[] a = (T[]) new Object[size * 2];
        arrayCopy(a, items);
        items = a;
    }

    public void addFirst(T item) {
        if (size == 0) {
            addTheFirstItem(item);
        } else {
            if (size == items.length) {
                expand();
            }
            if (senFront == 0) {
                senFront = items.length - 1;
            } else {
                senFront -= 1;
            }
            items[senFront] = item;
            size += 1;
        }
    }

    public void addLast(T item) {
        if (size == 0) {
            addTheFirstItem(item);
        } else {
            if (size == items.length) {
                expand();
            }
            senBack = (senBack + 1) % items.length;
            items[senBack] = item;
            size += 1;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.println(items[senFront]);
            senFront = (senFront + 1) % items.length;
        }
    }

    public void shrink() {
        T[] a = (T[]) new Object[items.length / 2];
        arrayCopy(a, items);
        items = a;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T x = items[senFront];
        size -= 1;
        senFront = (senFront + 1) % items.length;
        if (items.length >= 16 && size < items.length * 0.25) {
            shrink();
        }
        return x;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T x = items[senBack];
        size -= 1;
        if (senBack == 0) {
            senBack = items.length - 1;
        } else {
            senBack -= 1;
        }
        if (items.length >= 16 && size < items.length * 0.25) {
            shrink();
        }
        return x;
    }

    public T get(int index) {
        if (index < 0 || index > size - 1 || isEmpty()) {
            return null;
        }
        return items[(senFront + index) % items.length];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator {
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            if (wizPos < size) {
                return true;
            }
            return false;
        }

        public T next() {
            T x = items[wizPos];
            wizPos += 1;
            return x;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> o = (ArrayDeque<T>) object;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0, size = this.size(); i < size; i += 1) {
            if (this.get(i) != o.get(i)) {
                return false;
            }
        }
        return true;
    }
}
