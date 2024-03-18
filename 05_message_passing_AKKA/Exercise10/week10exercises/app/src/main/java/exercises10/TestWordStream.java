//Exercise 10.
//JSt vers Oct 23, 2023

//install  src/main/resources/english-words.txt
package exercises10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.DoubleSummaryStatistics;

import java.net.*;

import benchmarking.Benchmark;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";
    // 10.2.1 count number of words
    System.out.println("Count: " + readWords(filename).count());

    // 10.2.2 first 100 words from the file
    System.out.println("First 100 words: " +
        readWords(filename).limit(100).collect(Collectors.toList()));

    // 10.2.3 all words that have at least 22 letters
    System.out.println("All words at least 22 letters: " +
        readWords(filename).filter(x -> x.length() > 21).collect(Collectors.toList()));

    // 10.2.4 some words that have at least 22 letters (10 p.e.)
    System.out.println("Some words at least 22 letters: " +
        readWords(filename).filter(x -> x.length() > 21).limit(10).collect(Collectors.toList()));

    // 10.2.5 stream pipeline to find all palindromes
    System.out.println("Find all palindrome words: " +
        readWords(filename).filter(x -> isPalindrome(x)).collect(Collectors.toList()));

    // 10.2.6 stream parallel pipeline to find all palindromes
    System.out.println("Find all palindrome words parallel: " +
        readWords(filename).parallel().filter(x -> isPalindrome(x)).collect(Collectors.toList()));

    // Benchmark
    Benchmark.Mark7("Stream", i -> {
      List<String> list = readWords(filename).filter(x -> isPalindrome(x)).collect(Collectors.toList());
      return list.size();
    });

    Benchmark.Mark7("ParallelStream", i -> {
      List<String> list = readWords(filename).parallel().filter(x -> isPalindrome(x)).collect(Collectors.toList());
      return list.size();
    });

    // 10.2.7 count number of words online
    System.out.println("Count online: " + readWordStream("https://staunstrups.dk/jst/english-words.txt").count());

    // 10.2.8 minimal, maximal and average word lengths
    DoubleStream ds = readWords(filename).mapToDouble(x -> x.length());
    DoubleSummaryStatistics stats = ds.summaryStatistics();
    System.out.printf("min=%g  max=%g  avg=%g%n", stats.getMin(), stats.getMax(), stats.getAverage());

  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }

  // 10.2.7
  public static Stream<String> readWordStream(String urlname) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(urlname).openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      return reader.lines();
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }

  public static boolean isPalindrome(String s) {
    final String str = s.toLowerCase();
    return IntStream.range(0, s.length() / 2)
        .allMatch(i -> str.charAt(i) == str.charAt(s.length() - 1 - i));
    // return true if all characters of the words respect the palindrome clause
  }

  public static Map<Character, Integer> letters(String s) {
    Map<Character, Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}
