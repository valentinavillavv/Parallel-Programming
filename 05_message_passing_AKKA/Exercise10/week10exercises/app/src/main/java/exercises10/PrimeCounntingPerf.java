package exercises10;
//Exercise 10.1

//JSt vers Oct 23, 2023

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.atomic.AtomicLong;
import benchmarking.Benchmark;

class PrimeCountingPerf {
  public static void main(String[] args) {
    new PrimeCountingPerf();
  }

  static final int range = 100000;

  // Test whether n is a prime number
  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }

  // Sequential solution
  private static long countSequential(int range) {
    long count = 0;
    final int from = 0, to = range;
    for (int i = from; i < to; i++)
      if (isPrime(i))
        count++;
    return count;
  }

  // IntStream solution
  private static long countIntStream(int range) {
    long count = 0;
    count = IntStream.range(0, range)
        .filter(x -> isPrime(x))
        .count();
    return count;
  }

  // Parallel solution
  private static long countParallel(int range) {
    AtomicLong count = new AtomicLong(0);
    int threadCount = 8;
    int perThread = range / threadCount;
    Thread[] threads = new Thread[threadCount];
    for (int t = 0; t < threadCount; t = t + 1) {
      final int from = perThread * t,
          to = (t + 1 == threadCount) ? range : perThread * (t + 1);
      threads[t] = new Thread(() -> {
        for (int i = from; i < to; i++)
          if (isPrime(i))
            count.incrementAndGet();
      });
    }
    for (int t = 0; t < threadCount; t = t + 1)
      threads[t].start();
    try {
      for (int t = 0; t < threadCount; t = t + 1)
        threads[t].join();
    } catch (InterruptedException exn) {
    }
    return count.get();
  }

  // parallel Stream solution
  private static long countparallelStream(List<Integer> list) {
    long count = 0;
    count = list.stream()
        .parallel()
        .filter(i -> isPrime(i))
        .count();
    return count;
  }

  public PrimeCountingPerf() {
    Benchmark.Mark7("Sequential", i -> countSequential(range));

    Benchmark.Mark7("Stream", i -> countIntStream(range));

    Benchmark.Mark7("Atomic Parallel", i -> countParallel(range));

    List<Integer> list = new ArrayList<Integer>();
    for (int i = 2; i < range; i++) {
      list.add(i);
    }
    Benchmark.Mark7("Parallel Stream", i -> countparallelStream(list));
  }
}
