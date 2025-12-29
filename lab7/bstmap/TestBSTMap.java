package bstmap;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.In;
import org.junit.Test;

import java.lang.reflect.Parameter;
import java.util.Iterator;

/**
 * Tests by Brendan Hu, Spring 2015, revised for 2016 by Josh Hug
 */
public class TestBSTMap {

    @Test
    public void sanityGenericsTest() {
        try {
            BSTMap<String, String> a = new BSTMap<String, String>();
            BSTMap<String, Integer> b = new BSTMap<String, Integer>();
            BSTMap<Integer, String> c = new BSTMap<Integer, String>();
            BSTMap<Boolean, Integer> e = new BSTMap<Boolean, Integer>();
        } catch (Exception e) {
            fail();
        }
    }

    //assumes put/size/containsKey/get work
    @Test
    public void sanityClearTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1 + i);
            //make sure put is working via containsKey and get
            assertTrue(null != b.get("hi" + i) && (b.get("hi" + i).equals(1 + i))
                    && b.containsKey("hi" + i));
        }
        assertEquals(455, b.size());
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(null, b.get("starChild"));
        assertEquals(0, b.size());
        b.put("starChild", 5);
        assertTrue(((Integer) b.get("starChild")).equals(5));
        b.put("KISS", 5);
        assertTrue(((Integer) b.get("KISS")).equals(5));
        assertNotEquals(null, b.get("starChild"));
        assertEquals(2, b.size());
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++)
            b.put("hi" + i, 1);
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi") && b.get("hi") != null);
    }

    //assumes put works
    @Test
    public void containsKeyNullTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", null);
        assertTrue(b.containsKey("hi"));
    }

    @Test
    public void InOrderTest() {
        BSTMap<Integer, Integer> b = new BSTMap<>();

        for (int i = 0; i < 455; i++) {
            b.put(i, i * 5);
        }

        Iterator<Integer> seer = b.keySet().iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(i, (int) seer.next());
            i++;
        }
    }

    @Test
    public void iteratorTest() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        String[] letters = {"a", "b", "d", "e", "f", "g", "k", "m", "p", "r", "v", "x", "y", "z"};

        Iterator<String> seer = b.iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void removeRootTest1() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("a", 1);

        assertEquals(1, (int) b.remove("a"));
        assertFalse(b.iterator().hasNext());
    }

    @Test
    public void removeRootTest2() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("v", 2);
        b.put("p", 3);
        b.put("m", 4);
        b.put("r", 5);
        b.put("y", 6);
        b.put("x", 7);
        b.put("z", 8);

        String[] letters = {"m", "p", "r", "v", "x", "y", "z"};

        assertEquals(1, (int) b.remove("k"));

        Iterator<String> seer = b.iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void removeRootTest3() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        assertEquals(1, (int) b.remove("k"));

        String[] letters = {"a", "b", "d", "e", "f", "g", "m", "p", "r", "v", "x", "y", "z"};

        Iterator<String> seer = b.iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void notRemoveRootTest1() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        assertEquals(13, (int) b.remove("x"));

        int i = 0;
        Iterator<String> seer = b.iterator();
        String[] letters = {"a", "b", "d", "e", "f", "g", "k", "m", "p", "r", "v", "y", "z"};

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void notRemoveRootTest2() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        assertEquals(5, (int) b.remove("g"));

        String[] letters = {"a", "b", "d", "e", "f", "k", "m", "p", "r", "v", "x", "y", "z"};
        Iterator<String> seer = b.iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void notRemoveRootTest3() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        assertEquals(2, (int)b.remove("e"));

        String[] letters = {"a", "b", "d", "f", "g", "k", "m", "p", "r", "v", "x", "y", "z"};
        Iterator<String> seer = b.iterator();
        int i = 0;

        while (seer.hasNext()) {
            assertEquals(letters[i], seer.next());
            i++;
        }
    }

    @Test
    public void removeKeyAndValueTest() {
        BSTMap<String, Integer> b = new BSTMap<>();

        b.put("k", 1);
        b.put("e", 2);
        b.put("v", 3);
        b.put("b", 4);
        b.put("g", 5);
        b.put("d", 6);
        b.put("a", 7);
        b.put("f", 8);
        b.put("p", 9);
        b.put("y", 10);
        b.put("m", 11);
        b.put("r", 12);
        b.put("x", 13);
        b.put("z", 14);

        assertEquals(null, b.remove("v", 8));
    }
}
