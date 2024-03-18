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

public class SemaphoreImpTest {

	// Variable with set under test
	private SemaphoreImp sem;
	// increase the possibility of interleaving
	private CyclicBarrier barrier;

	@BeforeEach
	public void initialize() {
		// capacity equals 5
		sem = new SemaphoreImp(1);
	}

	@Test
	@DisplayName("Test")
	public void testingSemaphoreImpTestParallel() {
		int nrThreads = 2;

		// init barrier
		barrier = new CyclicBarrier(nrThreads + 1);

		sem.release();
		// start threads
		for (int i = 0; i < nrThreads; i++) {
			new SemaphoreImplementation().start();
		}

		try {
			barrier.await(); // wait until threads are ready for execution (maximize contention)
			barrier.await(); // wait for threads to finish
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	/*** Test threads ***/
	public class SemaphoreImplementation extends Thread {

		public void run() {
			try {
				barrier.await(); // waits until all threads all ready
				sem.acquire();
				System.out.println("hi");
				// sem.release();
				barrier.await(); // waits until all threads are done
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			} finally {
				sem.release();
			}
		}
	}
}
