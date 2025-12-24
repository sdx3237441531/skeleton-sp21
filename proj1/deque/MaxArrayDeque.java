package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque {
    private Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int maxDex = 0;
        for (int i = 1, size = size(); i < size; i += 1) {
            if (cmp.compare((T)get(maxDex), (T)get(i)) < 0) {
                maxDex = i;
            }
        }
        return (T)get(maxDex);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int maxDex = 0;
        for (int i = 1, size = size(); i < size; i += 1) {
            if (c.compare((T)get(maxDex), (T)get(i)) < 0) {
                maxDex = i;
            }
        }
        return (T)get(maxDex);
    }
}
