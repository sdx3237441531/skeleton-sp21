package deque;

import jh61b.junit.In;
import org.junit.Test;
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
}
