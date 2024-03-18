package exercise63;
// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022
// jst@itu.dk 22-09-2023

public class HistogramPrimesThreads {
  private final int range = 4999999;
  public static void main(String[] args) {
    new HistogramPrimesThreads(8,4);
  }

  public HistogramPrimesThreads(int threadCount,int nrLocks) {
    final Histogram histogram = new Histogram3(25,nrLocks); // 25 bins sufficient for a range of 0..4_999_999

    int perThread = range / threadCount;
    Thread[] threads = new Thread[threadCount];
    for (int t = 0; t < threadCount; t++) {
      final int from = perThread * t,
          to = (t + 1 == threadCount) ? range : perThread * (t + 1);
      threads[t] = new Thread(() -> {
        for (int i = from; i < to; i++)
          histogram.increment(countFactors(i));
      });
    }

    for (int t = 0; t < threadCount; t++)
      threads[t].start();
    try {
      for (int t = 0; t < threadCount; t++)
        threads[t].join();
    } catch (InterruptedException exn) {
    }

    // Finally we plot the result
    dump(histogram);
  }

  // Returns the number of prime factors of `p`
  public static int countFactors(int p) {
    if (p < 2)
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
        factorCount++;
        p = p / k;
      } else
        k = k + 1;
    }
    return factorCount;
  }

  public static void dump(Histogram histogram) {
    for (int bin = 0; bin < histogram.getSpan(); bin = bin + 1) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
    }
  }
}
