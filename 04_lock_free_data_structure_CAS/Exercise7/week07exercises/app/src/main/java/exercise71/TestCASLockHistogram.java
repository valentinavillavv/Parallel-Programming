package exercise71;

import benchmarking.*;

class TestCASLockHistogram {
	private static CasHistogram casHistogram;
	private static Histogram2 histogram2;
	
	private static final int RANGE = 49999; // increase and do the screen
	private static final int nbin = 25;

	public static void main(String[] args) {

		// Create an object `histogramCAS` with your Histogram CAS implementation
		casHistogram = new CasHistogram(nbin);

		// Create an object `histogramLock` with your Histogram Lock from week 5
		histogram2 = new Histogram2(nbin);

		// Evaluate the performance of CAS vs Locks histograms
	
		System.out.println("Performance testing:");
		for (int i = 1; i <= 16; i *= 2) {
			System.out.println("Using " + i + " threads:");
			int a1 = i;
			Benchmark.Mark7("CAS Histogram ", count -> {
				countParallel(RANGE, a1, casHistogram);
				return 0.0;
			});
			Benchmark.Mark7("Histogram2 ", count -> {
				countParallel(RANGE, a1, histogram2);
				return 0.0;
			});
		}

	}

	// Function to count the prime factors of a number `p`
	private static int countFactors(int p) {
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

	// Parallel execution of counting the number of primes for numbers in `range`
	private static void countParallel(int range, int threadCount, Histogram h) {
		final int perThread = range / threadCount;
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t = t + 1) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					h.increment(countFactors(i));

			});
		}
		for (int t = 0; t < threadCount; t = t + 1)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t = t + 1)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
	}
}
