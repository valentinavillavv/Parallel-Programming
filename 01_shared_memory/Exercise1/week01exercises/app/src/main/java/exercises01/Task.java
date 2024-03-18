// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;
import java.util.concurrent.locks.ReentrantLock;

public class Task {

    Printer lc = new Printer();

	ReentrantLock lock = new ReentrantLock();

    public Task() {

	Thread t1 = new Thread(() -> {
		while(true) 
		    lc.print();
	});
	Thread t2 = new Thread(() -> {
		while(true)  
		    lc.print();
	});
	t1.start(); t2.start();
	try { t1.join(); t2.join(); }
	catch (InterruptedException exn) { 
	    System.out.println("Some thread was interrupted");
	}
    }
    
    public static void main(String[] args) {
		new Task();
    }

    class Printer {
		public void print() {
		lock.lock();
		System.out.print("-"); //(1)
		try { 
			Thread.sleep(50); //(2)
		 } catch (InterruptedException exn) { }
		System.out.print("|"); //(3)
		lock.unlock();
		}
		}
		
}


