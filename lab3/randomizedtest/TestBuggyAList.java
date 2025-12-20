package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> test1 = new AListNoResizing<>();
        BuggyAList<Integer> test2 = new BuggyAList<>();

        test1.addLast(4);
        test2.addLast(4);
        test1.addLast(5);
        test2.addLast(5);
        test1.addLast(6);
        test2.addLast(6);

        assertEquals(test1.removeLast(), test2.removeLast());
        assertEquals(test1.removeLast(), test2.removeLast());
        assertEquals(test1.removeLast(), test2.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), L2.size());
            } else if (operationNumber == 2) {
                // getLast
                int size = L.size();
                if (size <= 0) {
                    continue;
                }
                assertEquals(L.getLast(), L2.getLast());
            } else if (operationNumber == 3) {
                // removeLast
                int size = L.size();
                if (size <= 0) {
                    continue;
                }
                assertEquals(L.removeLast(), L2.removeLast());
            }
        }
    }
}
