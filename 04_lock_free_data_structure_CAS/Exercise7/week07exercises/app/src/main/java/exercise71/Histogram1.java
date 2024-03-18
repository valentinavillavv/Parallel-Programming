package exercise71;

public class Histogram1 implements Histogram {
  private int[] counts;

  public Histogram1(int span) {
    this.counts = new int[span];
  }

  public void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }

  public int getCount(int bin) {
    return counts[bin];
  }

  public int getSpan() {
    return counts.length;
  }

  public int getAndClear(int bin) {
    int count = counts[bin];
    counts[bin] = 0;
    return count;
  }

}
