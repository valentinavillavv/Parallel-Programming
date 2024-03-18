package exercises04;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ReaderWriter2Test {

    @Test
    public void testFiveReaders() throws InterruptedException {
        ReaderWriter2 rw = new ReaderWriter2();

        Thread[] readerThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            readerThreads[i] = new Thread(() -> {
                rw.readLock();
                try {
                    // Simulate reading
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rw.readUnlock();
            });
            readerThreads[i].start();
        }
        // Wait for all reader threads to complete
        for (Thread thread : readerThreads) {
            thread.join();
        }

        // Check that there were never more than 5 readers concurrently
        assertTrue(rw.getMaxConcurrentReaders() <= 5);
    }
}
