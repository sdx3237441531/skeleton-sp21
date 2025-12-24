package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void IntegerComparatorTest() {
        Comparator<Integer> c = new IntegerComparator();

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(c);

        mad.addFirst(5);
        mad.addFirst(10);
        mad.addLast(15);
        mad.addLast(20);

        assertEquals(20, (int)mad.max());
        assertEquals(20, (int)mad.max(c));
    }

    @Test
    public void StringComparatorTest() {
        Comparator<String> c = new StringComparator();

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(c);

        mad.addFirst("apple");
        mad.addFirst("banana");
        mad.addLast("cat");
        mad.addLast("dog");

        assertEquals("dog", mad.max());
        assertEquals("dog", mad.max(c));
    }
}
