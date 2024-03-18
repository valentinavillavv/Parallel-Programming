package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import exercise72.ReadWriteCASLock;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    // 7.2.5
    private ReadWriteCASLock casLock;
    private CyclicBarrier barrier;
    private AtomicInteger numWritersHolding;

    @BeforeEach
    public void setUp() {
        casLock = new ReadWriteCASLock();
        numWritersHolding = new AtomicInteger(0);
    }

    @Test
    @DisplayName("Test Read Write CAS Lock Serial")
    public void testReadWriteCASSerial() throws InterruptedException {
        // It is not possible to take a read lock while holding a write lock
        assertTrue(casLock.writerTryLock(), " Impossible to acquire the write lock ");
        assertFalse(casLock.readerTryLock(), "Took a read lock while holding a write lock");

        casLock.writerUnlock();

        // It is not possible to take a write lock while holding a read lock.
        assertTrue(casLock.readerTryLock(), " Impossible to acquire the reader lock ");
        assertFalse(casLock.writerTryLock(), "Took a write lock while holding a read lock");

        casLock.readerUnlock();

        // It is not possible to unlock a lock that you do not hold (both for read and
        // write unlock).
        assertTrue(casLock.readerTryLock(), " Impossible to acquire the reader lock ");
        Thread t = new Thread(() -> {
            casLock.readerUnlock();
        });
        t.start();
        t.join();
        assertFalse(casLock.writerTryLock(), "Another thread unlock the lock");

        casLock.readerUnlock();

        assertTrue(casLock.writerTryLock(), " Impossible to acquire the writer lock ");
        t = new Thread(() -> {
            casLock.writerUnlock();
        });
        t.start();
        t.join();
        assertFalse(casLock.writerTryLock(), "Another thread unlock the lock");
    }

    // 7.2.6
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16}) // Vary the number of threads
    @DisplayName("parallel functional correctness test that checks that two writers cannot acquire the lock at the same time")
    public void testCASWriteLockParallel(int nrThreads) throws InterruptedException {
        
        // init barrier
		barrier = new CyclicBarrier(nrThreads + 1);

		// start threads
		for (int i = 0; i < nrThreads; i++) {
			new Thread(() -> {
                try {
                    barrier.await(); // waits until all threads all ready
                    for(int j=0;j<100;j++){
                        if(casLock.writerTryLock()){
                            int holdingCount = numWritersHolding.incrementAndGet();
                            assertTrue(holdingCount <= 1, "More than one writer holding the lock.");
                            numWritersHolding.decrementAndGet();
                            casLock.writerUnlock();
                        }
                    }
                    barrier.await(); // waits until all threads are done
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
		}

		try {
			barrier.await(); // wait until threads are ready for execution (maximize contention)
			barrier.await(); // wait for threads to finish
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
    }

}
