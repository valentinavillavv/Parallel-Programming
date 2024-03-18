package exercises04;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.List;
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class ConcurrentSetTest {

	// Variable with set under test
	private ConcurrentIntegerSet set;
	// increase the possibility of interleaving
	private CyclicBarrier barrier;

	@BeforeEach
	public void initialize() {
		// set = new ConcurrentIntegerSetBuggy();
		set = new ConcurrentIntegerSetSync();
		// set = new ConcurrentIntegerSetLibrary();
	}

	// test add
	@ParameterizedTest
	// @Disabled
	@DisplayName("Test Add Integer Set Parallel")
	@MethodSource("argsGeneration")
	public void testingAddIntegerSetParallel(int nrThreads, int N) {
		System.out.printf("Parallel integer set tests with %d threads and %d iterations", nrThreads, N);

		// init barrier
		barrier = new CyclicBarrier(nrThreads + 1);

		// start threads
		for (int i = 0; i < nrThreads; i++) {
			new AddNInteger(N).start();
		}

		try {
			barrier.await(); // wait until threads are ready for execution (maximize contention)
			barrier.await(); // wait for threads to finish
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		// add assert true
		assertTrue(N == set.size(), "set.size() == " + set.size() + ", but we expected " + N);
	}

	// test remove
	@ParameterizedTest
	// @Disabled
	@DisplayName("Test Remove Integer Set Parallel")
	@MethodSource("argsGeneration")
	public void testingRemoveIntegerSetParallel(int nrThreads, int N) {
		System.out.printf("Parallel integer set tests with %d threads and %d iterations", nrThreads, N);

		for (int i = 0; i < N; i++) {
			set.add(i);
		}

		// init barrier
		barrier = new CyclicBarrier(nrThreads + 1);

		// start threads
		for (int i = 0; i < nrThreads; i++) {
			new RemoveNInteger(N).start();
		}

		try {
			barrier.await(); // wait until threads are ready for execution (maximize contention)
			barrier.await(); // wait for threads to finish
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		// add assert true
		assertTrue(0 == set.size(), "set.size() == " + set.size() + ", but we expected " + 0);
	}

	private static List<Arguments> argsGeneration() {

		// Max number of increments
		final int I = 50_000;
		final int iInit = 10_000;
		final int iIncrement = 10_000;

		// Max exponent number of threads (2^J)
		final int J = 6;
		final int jInit = 1;
		final int jIncrement = 1;

		// List to add each parameters entry
		List<Arguments> list = new ArrayList<Arguments>();

		// Loop to generate each parameter entry
		// (2^j, i) for i in {10_000,20_000,...,I} and j in {1,..,J}
		for (int i = iInit; i <= I; i += iIncrement) {
			for (int j = jInit; j < J; j += jIncrement) {
				list.add(Arguments.of((int) Math.pow(2, j), i));
			}
		}

		// Return the list
		return list;
	}

	/*** Test threads ***/
	public class AddNInteger extends Thread {
		// add number from 0 to N
		private final int N;

		public AddNInteger(int N) {
			this.N = N;
		}

		public void run() {
			try {
				barrier.await(); // waits until all threads all ready
				for (int i = 0; i < N; i++) {
					set.add(i);
				}
				barrier.await(); // waits until all threads are done
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}

	public class RemoveNInteger extends Thread {
		// add number from 0 to N
		private final int N;

		public RemoveNInteger(int N) {
			this.N = N;
		}

		public void run() {
			try {
				barrier.await(); // waits until all threads all ready
				for (int i = 0; i < N; i++) {
					set.remove(i);
				}
				barrier.await(); // waits until all threads are done
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
}
