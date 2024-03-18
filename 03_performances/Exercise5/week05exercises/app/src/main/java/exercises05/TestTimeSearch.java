package exercises05;
// jst@itu.dk * 2023-09-05

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import benchmarking.Benchmark;

public class TestTimeSearch {
  public static void main(String[] args) { new TestTimeSearch(); }

  public TestTimeSearch() {
    final String filename = "src/main/resources/long-text-file.txt";
    final String target= "ipsum";
    final int NumThreads = 4;

    final PrimeCounter lc= new PrimeCounter();  //name is abit misleading, it is just a counter
    String[] lineArray= readWords(filename);

    //System.out.println("Array Size: "+ lineArray.length);

    //System.out.println("# Occurences of "+target+ " :"+search(target, lineArray, 0, lineArray.length, lc));
    /*Benchmark.Mark7("Search string serial", 
          i -> {
            search(target, lineArray, 0, lineArray.length, lc);
            return 0.0;
          });*/
    System.out.println("# Occurences of "+target+ " :"+countParallelN(target, lineArray, NumThreads, lc));
    /*Benchmark.Mark7("Search string parallel", 
          i -> {
            countParallelN(target, lineArray, NumThreads, lc);
            return 0.0;
          });*/
  }

  static long search(String x, String[] lineArray, int from, int to, PrimeCounter lc){
    //Search each line of file
    int thread_result = 0;
    for (int i=from; i<to; i++ ) {
      thread_result += (linearSearch(x, lineArray[i]));
    }
    System.out.println("Thread has found " + thread_result + " occurencies from " + from + " to " + to);
    lc.add(thread_result);
    return lc.get();
  }

  static int linearSearch(String x, String line) {
    //Search for occurences of c in line
    String[] arr= line.split(" ");
    int count= 0;
    for (int i=0; i<arr.length; i++ ) if ( (arr[i].equals(x)) ) count++;                   
    return count;
  }

  public static String[] readWords(String filename) {
    try {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
          return reader.lines().toArray(String[]::new);   //will be explained in Week07;
        }
    } catch (IOException exn) { return null;}
  }

  private static long countParallelN(String target,String[] lineArray, int N, PrimeCounter lc) {
    // uses N threads to search lineArray
    final int perThread = lineArray.length / N;
    Thread[] threads= new Thread[N];
    for (int t= 0; t<N; t++) {
        final int from = perThread * t, 
        to = (t+1==N) ? lineArray.length : perThread * (t+1); 
        threads[t]= new Thread( () -> {
          search(target,lineArray,from,to,lc);
        });
    }

    for (int t= 0; t<N; t++) 
      threads[t].start();

    try {
      for (int t=0; t<N; t++) 
        threads[t].join();
    } catch (InterruptedException exn) { }
    return lc.get();
  }
  
}
