package exercises04;

public class ReaderWriter2 {
    private boolean writer = false;
    private int readsAcquires = 0;
    private int readingnow = 0;
    private int readsReleases = 0;
    private int maxConcurrentReaders = 0;

    public synchronized void readLock() {
        try {
            while (writer || readingnow == 5) {
                wait();
            }
            readsAcquires++;
            readingnow++;
            if (readingnow > maxConcurrentReaders) {
                maxConcurrentReaders = readingnow;
            }
            System.out.println(readingnow + " threads are reading");
        } catch (InterruptedException e) {
        }
    }

    public synchronized void readUnlock() {
        if (readingnow > 0){
            readsReleases++;
            readingnow--;
            notifyAll();
        }  
    }

    public synchronized void writeLock() {
        try {
            while (writer || readsAcquires != readsReleases)
                wait();
            writer = true;
        } catch (InterruptedException e) {
        }
    }

    public synchronized void writeUnlock() {
        writer = false;
        notifyAll();
    }

    public int getMaxConcurrentReaders() {
        return maxConcurrentReaders;
    }

}
