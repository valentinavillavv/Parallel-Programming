package exercises03;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OurSemaphore {

    private final int capacity;
    private int permits;
    private final Lock lock;
    private final Condition condition;

    public OurSemaphore(int capacity) {
        this.capacity = capacity;
        this.permits = 0;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void acquire() throws Exception {
        lock.lock();

        while (permits == capacity) {
            condition.await();
        }
        permits++;

        lock.unlock();
    }

    public void release() {
        lock.lock();
        try {
            if (permits > 0) {
                permits--;
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void release(int total) {
        lock.lock();
        if (permits > total - 1) {
            permits = permits - total;
            condition.signalAll();
        }
        lock.unlock();
    }

    public static void main(String[] args) {
        OurSemaphore semaphore = new OurSemaphore(2); // Capacity is 2

        // three threads that try to acquire permits
        Thread thread1 = new Thread(() -> {
            try {
                try {
                    semaphore.acquire();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("Thread 1 acquired .");
                Thread.sleep(1000);
                semaphore.release();
                System.out.println("Thread 1 released .");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                try {
                    semaphore.acquire();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("Thread 2 acquired .");
                Thread.sleep(500);
                semaphore.release();
                System.out.println("Thread 2 released.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread3 = new Thread(() -> {
            try {
                System.out.println("Thread is trying to acquire.");
                semaphore.acquire();
                System.out.println("Thread 3 acquired.");
                Thread.sleep(2000);
                semaphore.release();
                System.out.println("Thread 3 released.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
    }

}
