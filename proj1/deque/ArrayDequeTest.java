package deque;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    //测试add和isEmpty和size
    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        assertTrue(ad.isEmpty());

        ad.addFirst("front");
        assertEquals(1, ad.size());
        assertFalse(ad.isEmpty());

        ad.addLast("middle");
        assertEquals(2, ad.size());

        ad.addLast("back");
        assertEquals(3, ad.size());

        ad.printDeque();
    }

    //测试add和remove
    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        assertTrue(ad.isEmpty());

        ad.addFirst(10);
        assertFalse(ad.isEmpty());

        ad.removeFirst();
        assertTrue(ad.isEmpty());
    }

    @Test
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(3);

        ad.removeLast();
        ad.removeFirst();
        ad.removeLast();
        ad.removeFirst();

        int size = ad.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad.removeLast());
    }

    @Test
    public void getTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        assertEquals(null, ad.get(10));

        for (int i = 4; i >= 1; i--) {
            ad.addFirst(i);
        }

        for (int i = 5; i <= 8; i++) {
            ad.addLast(i);
        }

        assertEquals(6, (int)ad.get(5));
        assertEquals(3, (int)ad.get(2));
        assertEquals(null, ad.get(-1));
        assertEquals(null, ad.get(100));
    }

    @Test
    public void expandTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        for (int i = 4; i >= 0; i--) {
            ad.addFirst(i);
        }

        for (int i = 5; i <= 9; i++) {
            ad.addLast(i);
        }

        for (int i = 0; i < ad.size(); i++) {
            assertEquals(i, (int)ad.get(i));
        }
    }

    @Test
    public void shrinkTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        for (int i = 9; i >= 0; i--) {
            ad.addFirst(i);
        }

        for (int i = 10; i < 20; i++) {
            ad.addLast(i);
        }

        for (int i = 0; i < 16; i++) {
            ad.removeLast();
        }

        for (int i = 0; i < 4; i++) {
            assertEquals(i, (int)ad.get(i));
        }
    }

    @Test
    public void bigLLDequeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad.removeLast(), 0.0);
        }
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        for (int i = 4; i >= 0; i--) {
            ad.addFirst(i);
        }

        for (int i = 5; i < 10; i++) {
            ad.addLast(i);
        }

        Iterator<Integer> seer = ad.iterator();
        while (seer.hasNext()) {
            System.out.println(seer.next());
        }

        for (int x : ad) {
            System.out.println(x);
        }
    }

    @Test
    public void iteratorStringTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();

        ad.addFirst("apple");
        ad.addFirst("banana");
        ad.addLast("cat");
        ad.addLast("dog");

        Iterator<String> seer = ad.iterator();

        assertTrue(seer.hasNext());
        assertEquals("banana", seer.next());

        assertTrue(seer.hasNext());
        assertEquals("apple", seer.next());

        assertTrue(seer.hasNext());
        assertEquals("cat", seer.next());

        assertTrue(seer.hasNext());
        assertEquals("dog", seer.next());

        assertFalse(seer.hasNext());
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        ad1.addFirst(5);
        ad1.addFirst(10);
        ad1.addLast(15);

        ArrayDeque<Integer> ad2 = null;

        assertFalse(ad1.equals(ad2));
    }

    @Test
    public void equalsTest2() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        ad1.addFirst(5);
        ad1.addFirst(10);
        ad1.addLast(15);

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();

        ad2.addLast(10);

        assertFalse(ad1.equals(ad2));
        assertFalse(ad2.equals(ad1));
    }

    @Test
    public void equalsTest3() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        ad.addFirst(5);
        ad.addFirst(10);
        ad.addLast(15);

        List<Integer> l = new ArrayList<>();
        l.add(10);
        l.add(5);
        l.add(15);

        assertFalse(ad.equals(l));
    }

    @Test
    public void eaualsTest4() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        ad1.addFirst(5);
        ad1.addFirst(10);
        ad1.addLast(15);

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();

        ad2.addFirst(5);
        ad2.addFirst(10);
        ad2.addLast(15);

        assertTrue(ad1.equals(ad2));
    }

    @Test
    public void lldEqualsAdTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();

        ad.addFirst("apple");
        ad.addFirst("banana");
        ad.addLast("cat");
        ad.addLast("dog");

        LinkedListDeque<String> lld = new LinkedListDeque<>();

        lld.addFirst("apple");
        lld.addFirst("banana");
        lld.addLast("cat");
        lld.addLast("dog");

        assertTrue(ad.equals(lld));
    }

    @Test
    public void lldEqualsAdTest2() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        ad.addFirst(5);
        ad.addFirst(10);
        ad.addLast(15);
        ad.addLast(20);

        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        lld.addFirst(5);
        lld.addFirst(10);
        lld.addLast(15);
        lld.addLast(20);

        assertTrue(lld.equals(ad));
    }
}
