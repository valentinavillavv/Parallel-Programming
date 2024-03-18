package exercise63;

public class Histogram2 implements Histogram {
    private final int[] counts;
    private final int span;
    private int total;

    public Histogram2(int span) {
        this.counts = new int[span];
        this.span = span;
        total = 0;
    }

    public synchronized void increment(int bin) {
        if (bin >= 0 && bin < span) {
            counts[bin] = counts[bin] + 1;
            total++;
        }
    }

    public synchronized int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return span;
    }

    public synchronized float getPercentage(int bin) {
        if (bin >= 0 && bin < span) {
            return (float) counts[bin] / total * 100;
        }
        return 0;
    }
}
