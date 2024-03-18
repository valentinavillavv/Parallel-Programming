package exercise72;

import java.util.concurrent.atomic.AtomicReference;

public class ReadWriteCASLock implements SimpleRWTryLockInterface {

	private AtomicReference<Holders> holders = new AtomicReference<Holders>();

	// 7.2.1 check the lock is unheld and set holders
	public boolean writerTryLock() {
		Writer writer = new Writer(Thread.currentThread());
		// Lock is currently unheld if holders is null
		if (holders.compareAndSet(null, writer)) {
			return true; // Success
		} else {
			return false; // fail
		}
	}

	// 7.2.2 lock is held by our thread and release lock by setting holders to null
	public void writerUnlock() {
		Writer current = (Writer) holders.get();

		if (current != null && current.thread == Thread.currentThread()) {
			holders.compareAndSet(current, null); // Release
		} else {
			throw new IllegalStateException("Not the current write lock holder or lock is not held.");
		}
	}

	// 7.2.3 repetedly read holders until null or a readerlist then update adding
	// new reader
	public boolean readerTryLock() {
		Thread currentThread = Thread.currentThread();
		ReaderList newReader;

		while (true) {
			Holders currentHolders = holders.get();

			if (currentHolders == null || currentHolders instanceof ReaderList) { // no one or a Reader holds the lock
				newReader = currentHolders == null ? new ReaderList(currentThread, null)
						: new ReaderList(currentThread, (ReaderList) currentHolders);
				if (holders.compareAndSet(currentHolders, newReader))
					return true;
			} else {
				return false; // A writer has the lock
			}
		}
	}

	// 7.2.4 read until the calling thread is in the reader list and then remove it
	public void readerUnlock() {
		Thread currentThread = Thread.currentThread();

		while (true) {
			Holders currentHolders = holders.get();

			if (currentHolders == null) {
				throw new IllegalStateException("Not holding a read lock");
			} else if (currentHolders instanceof ReaderList) {
				ReaderList currentReaderList = (ReaderList) currentHolders;

				// Check if the current thread is in the reader list
				if (currentReaderList.contains(currentThread)) {
					ReaderList updatedReaderList = currentReaderList.remove(currentThread);

					// Attempt to atomically update the holders field with the new reader list
					if (holders.compareAndSet(currentHolders, updatedReaderList)) {
						return; // Success
					}
				} else {
					throw new IllegalStateException("Not the current read lock holder");
				}
			} else {
				throw new IllegalStateException("thread does not have read lock");
			}
		}
	}

	// Challenging 7.2.7: You may add new methods
	private static abstract class Holders {
	}

	private static class ReaderList extends Holders {
		private final Thread thread;
		private final ReaderList next;

		public ReaderList(Thread thread, ReaderList next) {
			this.thread = thread;
			this.next = next;
		}

		public boolean contains(Thread targetThread) {
			if (this.thread == targetThread) {
				return true;
			} else if (next != null) {
				return next.contains(targetThread);
			}
			return false;
		}

		public ReaderList remove(Thread targetThread) {
			if (this.thread == targetThread) {
				return next; // remove the element
			} else if (next != null) {
				return new ReaderList(this.thread, next.remove(targetThread)); // iterate the searching
			}
			return null; // empty list of readers
		}
	}

	private static class Writer extends Holders {
		public final Thread thread;

		public Writer(Thread currentThread) {
			this.thread = currentThread;
		}
	}
}
