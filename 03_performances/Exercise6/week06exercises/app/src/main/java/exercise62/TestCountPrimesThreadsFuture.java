package exercise62;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import benchmarking.Benchmark;

public class TestCountPrimesThreadsFuture {
	public static void main(String[] args) {
		new TestCountPrimesThreads();
	}

	public TestCountPrimesThreadsFuture() {
		final int range = 100_000;
		Benchmark.Mark7("countSequential Future", i -> countSequential(range));
		for (int c = 1; c <= 16; c++) {
			final int threadCount = c;
			Benchmark.Mark7(String.format("countParallelN Future %2d", threadCount),
					i -> countParallelN(range, threadCount));
			Benchmark.Mark7(String.format("countParallelNLocal Future %2d", threadCount),
					i -> countParallelNLocal(range, threadCount));
		}
	}

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

	// General parallel solution, using multiple threads
	private static long countParallelN(int range, int threadCount) {
		final int perThread = range / threadCount;
		final AtomicLong lc = new AtomicLong(0);

		final List<Future<?>> list = new ArrayList<>();
		final ExecutorService pool = new ForkJoinPool(8);

		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			try {

				Future<?> f1 = pool.submit(() -> {
					for (int i = from; i < to; i++)
						if (isPrime(i))
							lc.incrementAndGet();
				});
				list.add(f1);
			} catch (Error ex) {
				System.out.println("At t = " + t + " I got error: " + ex);
				System.exit(0);
			}
		}

		try {
			for (Future<?> f : list) {
				f.get(); // Wait for each future to be executed and add partial result
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdown(); // We are sure to be done, so we shut down the pool
		return lc.get();
	}

	// General parallel solution, using multiple threads but adds results in the end
	private static long countParallelNLocal(int range, int threadCount) {

		final int perThread = range / threadCount;
		final long[] results = new long[threadCount];

		final List<Future<?>> list = new ArrayList<>();
		final ExecutorService pool = new ForkJoinPool(8);

		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			final int threadNo = t;
			try {
				Future<?> f1 = pool.submit(() -> {
					long count = 0;
					for (int i = from; i < to; i++)
						if (isPrime(i))
							count++;
					results[threadNo] = count;
				});
				list.add(f1);
			} catch (Error ex) {
				System.out.println("At t = " + t + " I got error: " + ex);
				System.exit(0);
			}
		}

		try {
			for (Future<?> f : list) {
				f.get(); // Wait for each future to be executed and add partial result
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdown(); // We are sure to be done, so we shut down the pool
		long result = 0;
		for (int t = 0; t < threadCount; t++)
			result += results[t];
		return result;
	}
}
