package exercise71;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] count;
    private final int span;

    public CasHistogram(int span) {
        count = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            count[i] = new AtomicInteger(0);
        }
        this.span=span;
    }

    @Override
    public void increment(int bin) {
        int currentValue;
        do {
            currentValue = count[bin].get();
        } while (!count[bin].compareAndSet(currentValue, currentValue + 1));
    }

    @Override
    public int getCount(int bin) {
        return count[bin].get();
    }

    @Override
    public int getSpan() {
        return span;
    }

    @Override
    public int getAndClear(int bin) {
        int currentValue;
        do {
            currentValue = count[bin].get();
        } while (!count[bin].compareAndSet(currentValue, 0));
        return currentValue;
    }

}