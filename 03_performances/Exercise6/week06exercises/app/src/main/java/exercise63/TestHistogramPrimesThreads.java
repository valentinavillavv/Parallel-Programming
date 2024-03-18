package exercise63;

import benchmarking.Benchmark;

public class TestHistogramPrimesThreads {
    public static void main(String[] args) { new TestHistogramPrimesThreads(); }

    public TestHistogramPrimesThreads() {
        for(int index=1;index<8;index++){
            final int nrLocks = index;
            Benchmark.Mark7("HistogramPrimesThreads with 8 thread and " + nrLocks + " locks", 
                j -> {
                    HistogramPrimesThreads p = new HistogramPrimesThreads(8,nrLocks);
                    return p.hashCode();
                });
        }
	}
}
