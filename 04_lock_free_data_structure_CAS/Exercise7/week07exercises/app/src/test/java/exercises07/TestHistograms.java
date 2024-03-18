package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import exercise71.CasHistogram;
import exercise71.Histogram;
import exercise71.Histogram1;

import static org.junit.jupiter.api.Assertions.*;

public class TestHistograms {

    private Histogram casHistogram;
    private Histogram histogram1;

    private final int span = 25; // 25 should be enough

    @BeforeEach
    public void setUp() {
        casHistogram = new CasHistogram(span);
        histogram1 = new Histogram1(span);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16}) // Vary the number of threads
    @DisplayName("Compare CasHistogram parallel correctness with Histogram1 serial")
    public void testCasHistogramParallelCorrectness(int numThreads) throws InterruptedException {
    
      // start prime factor searching
		  new HistogramPrimesThreads(numThreads,casHistogram);
		  new HistogramPrimesThreads(1, histogram1); // serial one

      // Check correctness for each bin
      for (int bin = 0; bin < span; bin++) {
            int casCount = casHistogram.getCount(bin);
            int seqCount = histogram1.getCount(bin);
            assertEquals(seqCount, casCount, "Bin " + bin + " does not match between CasHistogram and Histogram1");
      }
    }

    public class HistogramPrimesThreads {
        private final int range = 499999;
      
        public HistogramPrimesThreads(int threadCount,Histogram hist) {

          final Histogram histogram = hist;
      
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
        }
      
        // Returns the number of prime factors of `p`
        public int countFactors(int p) {
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
      }
      
}
