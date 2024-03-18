// For week 8
// raup@itu.dk * 2023-10-20
package exercises08;

import java.util.concurrent.atomic.AtomicReference;

// Treiber's LockFree Stack (Goetz 15.4 & Herlihy 11.2)
class LockFreeStack<T> {
	AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(); // Initializes to null

	public void push(T value) {
		Node<T> newHead = new Node<T>(value);
		Node<T> oldHead;
		do {
			oldHead = top.get();
			newHead.next = oldHead;
		} while (!top.compareAndSet(oldHead, newHead)); // PUSH1 linearization point if no other push/pop

	}

	public T pop() {
		Node<T> newHead;
		Node<T> oldHead;
		do {
			oldHead = top.get();
			if (oldHead == null) { // POP1 linearization point if queue empty
				return null;
			}
			newHead = oldHead.next;
		} while (!top.compareAndSet(oldHead, newHead)); // POP2 lin point if no other push/pop during creation of new
														// head

		return oldHead.value;
	}

	// class for nodes
	private static class Node<T> {
		public final T value;
		public Node<T> next;

		public Node(T value) {
			this.value = value;
			this.next = null;
		}
	}
}
