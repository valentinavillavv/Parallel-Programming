// NOTE: In this file, you should only modify the class ConcurrentIntegerSetSync
package exercises04;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

//interface 
public interface ConcurrentIntegerSet {
  public boolean add(Integer element);

  public boolean remove(Integer element);

  public int size();
}

// withouth syncronization
class ConcurrentIntegerSetBuggy implements ConcurrentIntegerSet {
  final private Set<Integer> set;

  public ConcurrentIntegerSetBuggy() {
    this.set = new HashSet<Integer>();
  }

  public boolean add(Integer element) {
    return set.add(element);
  }

  public boolean remove(Integer element) {
    return set.remove(element);
  }

  public int size() {
    return set.size();
  }
}

class ConcurrentIntegerSetSync implements ConcurrentIntegerSet {
  final private Set<Integer> set;

  public ConcurrentIntegerSetSync() {
    this.set = new HashSet<Integer>();
  }

  public synchronized boolean add(Integer element) {
    return set.add(element);
  }

  public synchronized boolean remove(Integer element) {
    return set.remove(element);
  }

  public synchronized int size() {
    return set.size();
  }
}

// uses a thread safe class
class ConcurrentIntegerSetLibrary implements ConcurrentIntegerSet {
  final private Set<Integer> set;

  public ConcurrentIntegerSetLibrary() {
    this.set = new ConcurrentSkipListSet<Integer>();
  }

  public boolean add(Integer element) {
    return set.add(element);
  }

  public boolean remove(Integer element) {
    return set.remove(element);
  }

  public int size() {
    return set.size();
  }
}
