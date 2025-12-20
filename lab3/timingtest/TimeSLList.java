package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        SLList<Integer> test1 = new SLList<>();
        for (int i = 0; i < 1000; i++) {
            test1.addLast(i);
        }
        Stopwatch sw1 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test1.addLast(i);
        }
        Ns.addLast(1000);
        times.addLast(sw1.elapsedTime());
        opCounts.addLast(10000);

        SLList<Integer> test2 = new SLList<>();
        for (int i = 0; i < 2000; i++) {
            test2.addLast(i);
        }
        Stopwatch sw2 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test2.addLast(i);
        }
        Ns.addLast(2000);
        times.addLast(sw2.elapsedTime());
        opCounts.addLast(10000);

        SLList<Integer> test3 = new SLList<>();
        for (int i = 0; i < 4000; i++) {
            test3.addLast(i);
        }
        Stopwatch sw3 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test3.addLast(i);
        }
        Ns.addLast(4000);
        times.addLast(sw3.elapsedTime());
        opCounts.addLast(10000);

        SLList<Integer> test4 = new SLList<>();
        for (int i = 0; i < 8000; i++) {
            test4.addLast(i);
        }
        Stopwatch sw4 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test4.addLast(i);
        }
        Ns.addLast(8000);
        times.addLast(sw4.elapsedTime());
        opCounts.addLast(10000);

        SLList<Integer> test5 = new SLList<>();
        for (int i = 0; i < 16000; i++) {
            test5.addLast(i);
        }
        Stopwatch sw5 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test5.addLast(i);
        }
        Ns.addLast(16000);
        times.addLast(sw5.elapsedTime());
        opCounts.addLast(10000);

        SLList<Integer> test6 = new SLList<>();
        for (int i = 0; i < 32000; i++) {
            test6.addLast(i);
        }
        Stopwatch sw6 = new Stopwatch();
        for (int i = 0; i < 10000; i++) {
            test6.addLast(i);
        }
        Ns.addLast(32000);
        times.addLast(sw6.elapsedTime());
        opCounts.addLast(10000);

        printTimingTable(Ns, times, opCounts);
    }

}
