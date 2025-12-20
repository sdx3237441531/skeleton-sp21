package timingtest;
import edu.neu.ccs.gui.StringObjectRadioPanel;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int cnt = 0;
        Stopwatch sw = new Stopwatch();
        AList<Integer> test = new AList<>();
        for (int i = 0; i < 1000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 1000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 2000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 4000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 8000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 16000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 32000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 64000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 128000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        for (int i = 0; i < 256000; i++) {
            test.addLast(i);
            cnt += 1;
        }
        Ns.addLast(cnt);
        times.addLast(sw.elapsedTime());
        opCounts.addLast(cnt);

        printTimingTable(Ns, times, opCounts);
    }
}
