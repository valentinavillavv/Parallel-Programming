package exercise63;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Histogram3 implements Histogram {
    private final int[] counts;
    private final int span;
    private int total;
    private final List<Lock> locks;

    public Histogram3(int span, int nrLocks) {
        this.span = span;
        this.total = 0;
        this.counts = new int[span];
        this.locks = new ArrayList<>(nrLocks);
        for (int i = 0; i < nrLocks; i++) {
            locks.add(i, new ReentrantLock());
        }
    }

    public void increment(int bin) {
        if (bin > 0 && bin < span) {
        synchronized (locks.get(bin % locks.size())) {
            counts[bin] = counts[bin] + 1;
            total++;
        }
        }
    }

    public float getPercentage(int bin) {
        if (bin > span || bin < 0) {
            return 0;
        }
        synchronized (locks.get(bin % locks.size())) {
            return (float) counts[bin] / total * 100;

        }
    }

    public int getCount(int bin) {
        synchronized (locks.get(bin % locks.size())) {
            return counts[bin];
        }
    }

    public int getSpan() {
        return span;
    }
}