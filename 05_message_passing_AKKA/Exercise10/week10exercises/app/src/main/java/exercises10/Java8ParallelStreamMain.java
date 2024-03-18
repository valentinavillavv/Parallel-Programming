package exercises10;

import java.util.List;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Java8ParallelStreamMain {
	/*
	 * public static void main(String[] args) {
	 * System.out.println("=================================");
	 * System.out.println("Using Sequential Stream");
	 * System.out.println("=================================");
	 * int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
	 * 18, 19, 20, 21, 22, 23, 24, 25, 26,
	 * 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
	 * 46, 47, 48, 49, 50, 51, 52,
	 * 53 };
	 * IntStream intArrStream = Arrays.stream(array);
	 * intArrStream.forEach(s -> {
	 * System.out.println(s + " " + Thread.currentThread().getName());
	 * });
	 * System.out.println("=================================");
	 * System.out.println("Using Parallel Stream");
	 * System.out.println("=================================");
	 * IntStream intParallelStream = Arrays.stream(array).parallel();
	 * intParallelStream.forEach(s -> {
	 * System.out.println(s + " " + Thread.currentThread().getName());
	 * });
	 * }
	 */

	public static void main(String[] args) {
		List<Integer> data = new ArrayList<Integer>();
		int dummy = 0;
		for (int i = 0; i < 100000; i++) {
			data.add(i);
		}

		System.out.println("=================================");
		System.out.println("Using Sequential Stream");
		System.out.println("=================================");
		long currentTime = System.currentTimeMillis();
		dummy = (int) data.stream()
				.map(number -> performComputation(1000))
				.reduce(0, Integer::sum);
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to complete: " + (endTime - currentTime)
				+ " milliseconds");

		System.out.println("=================================");
		System.out.println("Using Parallel Stream");
		System.out.println("=================================");
		currentTime = System.currentTimeMillis();
		dummy = (int) data.stream()
				.parallel()
				.map(number -> performComputation(1000))
				.reduce(0, Integer::sum);
		endTime = System.currentTimeMillis();
		System.out.println("Time taken to complete: " + (endTime - currentTime)
				+ " milliseconds");
	}

	public static int performComputation(int range) {
		int count = 0;
		for (int i = 0; i < range; i++)
			if (isPrime(i))
				count++;
		return count;
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}
}
