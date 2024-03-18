package exercises05;

import benchmarking.Benchmark;

public class TestVolatile {

  public static void main(String[] args) {
    new TestVolatile();
  }

  public TestVolatile() {
    Benchmark.SystemInfo();
    final Volatile myVolatile = new Volatile();
    Benchmark.Mark7("Normal add",
        i -> {
          myVolatile.inc();
          return 0.0;
        });
    Benchmark.Mark7("Volatile add",
        i -> {
          myVolatile.vInc();
          return 0.0;
        });
  }
}

/**
 * Immutable Point class used by DelegatingVehicleTracker
 * 
 * @author Brian Goetz and Tim Peierls
 */

class Volatile {
  private volatile int vCtr;
  private int ctr;

  public void vInc() {
    vCtr++;
  }

  public void inc() {
    ctr++;
  }
}
