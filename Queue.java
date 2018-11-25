//CMPS12B/M
//Queue.java
//Queue, a circular linked list which we use to store job objects
public class Queue implements QueueInterface {
	private class Node { // JobNode contains a Job object and a pointer to the next JobNode
		Object lastJob;
		Node next;

		Node(Object lastJob) {
			this.lastJob = lastJob;
			this.next = null;
			// here?
		}

	}

	private Node curr;
	private int numItems;

	// isEmpty()
	// pre: none
	// post: returns true if this Queue is empty, false otherwise
	public boolean isEmpty() {
		if (curr == null) {
			return true;
		} else {
			return false;
		}
	}

	// length()
	// pre: none
	// post: returns the length of this Queue.
	public int length() {
		return numItems;
	}

	// enqueue()
	// adds newItem to back of this Queue
	// pre: none
	// post: !isEmpty()
	public void enqueue(Object newItem) {
		Node newJob = new Node(newItem);
		// newJob.item = newItem;
		if (isEmpty() == true) { // special case when queue is empty
			newJob.next = newJob; // CHANGED from newItem

		} else {
			newJob.next = curr.next;
			curr.next = newJob;
		}
		curr = newJob;
		numItems++;
	}

	// dequeue()
	// deletes and returns item from front of this Queue
	// pre: !isEmpty()
	// post: this Queue will have one fewer element
	public Object dequeue() throws QueueEmptyException {
		Node firstJob = curr.next;
		if (isEmpty() == true) {
			throw new QueueEmptyException("Cannot dequeue an object from an empty queue");
		} else {

			if (firstJob == curr) {// chec if the is only one job in the queue 
				curr = null; // set only job to null, left with an empty queue
			} else {
				curr.next = firstJob.next; // in memory, let nothing point to last job, instead,
			} // make lastJob point to firstJob. circular list.
			numItems--;
		}
		return firstJob.lastJob;
	}
		

	// peek()
	// pre: !isEmpty()
	// post: returns item at front of Queue
	public Object peek() throws QueueEmptyException {
		if (isEmpty() == true) {
			throw new QueueEmptyException("Cannot peek an object from an empty queue");
		} else {
			Node firstJob = curr.next;
			return firstJob.lastJob;
		}
	}

	// dequeueAll()
	// sets this Queue to the empty state
	// pre: !isEmpty()
	// post: isEmpty()
	public void dequeueAll() throws QueueEmptyException { 
		if (isEmpty() == true) {
			throw new QueueEmptyException("The queue is already empty");
		} else {
			curr = null;
			numItems = 0;
		}
	}

	public Object getNext() throws QueueEmptyException { //gets next job
		if (isEmpty() == true) {
			throw new QueueEmptyException("This queue does not exist");
		} else {
			Node next_item = curr.next.next;
			return next_item.lastJob;
		}

	}

	public int Total() { //calculates total wait time
		int total = 0;
		int len = this.length();
		Node b = curr.next;
		for (int i = 0; i < len; i++) {
			total = total + (((Job) b.lastJob).getWaitTime());
			b = b.next;
		}
		return total;
	}

	public int max() { // finds our max wait time
		Node b = curr.next;
		int len = this.length();
		int max = ((Job) b.lastJob).getWaitTime();
		for (int i = 0; i < len; i++) {
			if (max < (((Job) b.next.lastJob).getWaitTime())) {
				max = (((Job) b.next.lastJob).getWaitTime());

			}
			b = b.next;
		}
		return max;

	}

	public boolean finished(int m) { //used for our while loop and checks if all jobs that have returned have a finish time
		if (curr == null) { //we return false because we know that there's jobs outside of the jobs processor, therefore it keeps the while loop running
			return false;
		}
		Node b = curr.next;
		int len = this.length();
		int counter = 0;

		for (int i = 0; i < len; i++) {
			if (((Job) b.lastJob).getFinish() != -1) {
				counter++;
			}
		}
		if (counter == len && counter == m) {
			return true;
		} else {
			return false;
		}
	}

	// toSting()
	// overrides Object's toString() method
	public String toString() {
		String blob = "";
		int len = this.length();
		Node b = curr.next;
		for (int i = 0; i < len; i++) {

			// System.out.println(b);
			blob = blob + b.lastJob.toString() + " ";
			b = b.next;
		}
		// for(JobNode b = curr; b != null; b = b.next){
		// System.out.println(b);
		// blob+= b.toString();

		// }
		return blob;
	}
}
