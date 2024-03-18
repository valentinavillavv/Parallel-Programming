// For week 2
package exercises02;

public class ReadersWritersSync {
    private boolean writer = false;
    private int readsAcquires = 0;
    private int readsReleases = 0;

    public static void main(String[] args) {
        ReadersWritersSync m = new ReadersWritersSync();
        for (int i = 0; i < 10; i++) {
            // start a reader

            new Thread(() -> {
                m.readLock();
                System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
                // read
                System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
                m.readUnlock();
            }).start();
            // start a writer
            new Thread(() -> {
                m.writeLock();
                System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
                // write
                System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
                m.writeUnlock();
            }).start();
        }
    }

    public synchronized void readLock() {
        try {
            while (writer)
                wait();
            readsAcquires++;
        } catch (InterruptedException e) {
        }
    }

    public synchronized void readUnlock() {
        readsReleases++;
        notifyAll();
    }

    public synchronized void writeLock() {
        try {
            while (writer)
                wait();
            writer = true;
            while (readsAcquires != readsReleases)
                wait();
        } catch (InterruptedException e) {
        }
    }

    public synchronized void writeUnlock() {
        writer = false;
        notifyAll();
    }

}
