package deque;

import jh61b.junit.In;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    public void getTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        assertEquals(null, lld.get(0));

        lld.addFirst(5);
        lld.addFirst(10);
        lld.addLast(15);

        assertEquals(5, (int)lld.get(1));
        assertEquals(10, (int)lld.get(0));
        assertEquals(15, (int)lld.get(2));
        assertEquals(null, lld.get(-1));
        assertEquals(null, lld.get(100));
    }

    @Test
    public void getRecursionTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        assertEquals(null, lld.get(0));

        lld.addFirst(5);
        lld.addFirst(10);
        lld.addLast(15);

        assertEquals(5, (int)lld.get(1));
        assertEquals(10, (int)lld.get(0));
        assertEquals(15, (int)lld.get(2));
        assertEquals(null, lld.get(-1));
        assertEquals(null, lld.get(100));
    }

    @Test
    public void addRemoveTest2() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        for (int i = 4; i >= 0; i--) {
            lld.addFirst(i);
        }

        for (int i = 5; i < 10; i++) {
            lld.addLast(i);
        }

        for (int i = 0; i < lld.size(); i++) {
            assertEquals(i, (int)lld.get(i));
        }

        for (int i = lld.size() - 1; i >= 0; i--) {
            assertEquals(i, (int)lld.removeLast());
        }
    }

    @Test
    public void LinkedListDequeTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        for (int i = 4; i >= 0; i--) {
            lld.addFirst(i);
        }

        for (int i = 5; i < 10; i++) {
            lld.addLast(i);
        }

        for (int x : lld) {
            System.out.println(x);
        }
    }

    @Test
    public void testRemoveSingleElement() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();

        // 测试1：添加一个元素后移除
        deque.addFirst(1);
        assertEquals(Integer.valueOf(1), deque.removeFirst());
        assertTrue("链表应为空", deque.isEmpty());
        assertEquals(0, deque.size());

        // 测试2：从另一边测试
        deque.addLast(2);
        assertEquals(Integer.valueOf(2), deque.removeLast());
        assertTrue("链表应为空", deque.isEmpty());
        assertEquals(0, deque.size());

        // 测试3：边界情况测试 - 连续操作
        deque.addFirst(10);
        deque.addLast(20);
        deque.addFirst(30);

        assertEquals(Integer.valueOf(30), deque.removeFirst()); // 移除30
        assertEquals(Integer.valueOf(20), deque.removeLast());  // 移除20
        assertEquals(Integer.valueOf(10), deque.removeFirst()); // 移除10

        assertTrue("所有元素移除后应为空", deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();

        lld1.addFirst(5);
        lld1.addFirst(10);
        lld1.addLast(15);

        LinkedListDeque<Integer> lld2 = null;

        assertFalse(lld1.equals(lld2));
    }

    @Test
    public void equalsTest2() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();

        lld1.addFirst(5);
        lld1.addFirst(10);
        lld1.addLast(15);

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        lld2.addLast(10);

        assertFalse(lld1.equals(lld2));
        assertFalse(lld2.equals(lld1));
    }

    @Test
    public void equalsTest3() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        lld.addFirst(5);
        lld.addFirst(10);
        lld.addLast(15);

        List<Integer> l = new ArrayList<>();
        l.add(10);
        l.add(5);
        l.add(15);

        assertFalse(lld.equals(l));
    }

    @Test
    public void eaualsTest4() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();

        lld1.addFirst(5);
        lld1.addFirst(10);
        lld1.addLast(15);

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        lld2.addFirst(5);
        lld2.addFirst(10);
        lld2.addLast(15);

        assertTrue(lld1.equals(lld2));
    }
}
