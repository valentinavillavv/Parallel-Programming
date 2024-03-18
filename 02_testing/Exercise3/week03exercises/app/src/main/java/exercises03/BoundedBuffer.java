package exercises03;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInterface<T> {

    private final LinkedList<T> list;
    private final Semaphore slots;
    private final Semaphore item;
    private final Semaphore mutex;

    // we want to keep the list FIFO, so we use addFirst and removeLast

    // we assume then in the beginning the list is empty
    public BoundedBuffer(int bufferSize) {
        this.list = new LinkedList<T>();
        // we need 3 semaphores:
        // one for mutual exclusion(mutex),
        this.mutex = new Semaphore(1);
        // one to keep track of the available slots (slots you can write), called slots
        this.slots = new Semaphore(bufferSize);
        // one to keep track of the item that are ready to read
        this.item = new Semaphore(0);
    }

    @Override
    public T take() throws Exception {
        item.acquire();
        mutex.acquire();
        T taken = list.removeLast();
        //System.out.println("Consumed" + taken);
        mutex.release();
        slots.release();
        return taken;
    }

    @Override
    public void insert(T elem) throws Exception {
        slots.acquire();
        mutex.acquire();
        /// maybe we need a check on the size of the list because list is dynamic
        list.addFirst(elem);
        //System.out.println("Produced" + elem);
        mutex.release();
        item.release();
    }
}
