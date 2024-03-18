// raup@itu.dk * 2023-10-20 
package exercises08;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// Concurrency imports
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Random;

class TestLockFreeStack {

    private AtomicInteger sum = new AtomicInteger(0);

    @BeforeEach
    public void setUp() {
        sum.set(0);
    }

    // 8.2.2 - Test push
    @ParameterizedTest
    @ValueSource(ints = {32, 64, 128, 256}) // Vary the number of threads
    @DisplayName("Push integer and verify the sum")
    public void pushInteger(int threadCount) {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random rand = new Random();

        Thread[] threads = new Thread[threadCount];
          for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
              int num = rand.nextInt(100);
              sum.addAndGet(num);
              stack.push(num);
            });
          }
      
          for (int t = 0; t < threadCount; t++)
            threads[t].start();
          try {
            for (int t = 0; t < threadCount; t++)
              threads[t].join();
          } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
		    }
    
        // assert
        int stack_sum = 0;
        for (int i = 0; i < threadCount; i++) {
            stack_sum += stack.pop();
        }
        assertEquals(sum.get(), stack_sum, "Sum expected was " + sum + " does not match with sum of the stack that is " + stack_sum);
    }

    // 8.2.3 - Test pop 
    @ParameterizedTest
    @ValueSource(ints = {32, 64, 128, 256}) // Vary the number of threads
    @DisplayName("Pop integer and verify the sum")
    public void popInteger(int threadCount) {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random rand = new Random();

        int num;
        int local_sum = 0;
        for (int i = 0; i < threadCount; i++) {
            num = rand.nextInt(100);
            stack.push(num);
            local_sum+=num;
        }

        Thread[] threads = new Thread[threadCount];
          for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
              sum.addAndGet(stack.pop());
            });
          }
      
          for (int t = 0; t < threadCount; t++)
            threads[t].start();
          try {
            for (int t = 0; t < threadCount; t++)
              threads[t].join();
          } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
		    }
            
        assertEquals(sum.get(), local_sum, "Sum expected was " + local_sum + " does not match with sum of the stack that is " + sum);
    }
}
