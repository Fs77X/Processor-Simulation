//CMPS12B/M
//QueueTest.java
//QueueTest, a test file which we use to test our queue to see if its fully functioning
public class QueueTest{
	public static void main(String[] args){
		System.out.println("Break 1");
		Queue Test = new Queue();
		System.out.println("Break 2");
		Job A = new Job(2,2);
		System.out.println("Break 3");
		Job B = new Job(5,1);
		System.out.println("Break 4");
		Job C = new Job(7,2);
		System.out.println("Break 5");
		Test.enqueue(A);
		System.out.println("Break 6");
		Test.enqueue(B);
		System.out.println("Break 7");
		Test.enqueue(C);
		System.out.println("Break 8");
//		for(Queue c = Test; c!=null; c.next) {
//			
//		}
		int a = Test.length();
		System.out.println(a);
		System.out.println("test before: " + Test);
		System.out.println("Break 9");
//		Queue temp = Test;
//		Job JobTest = (Job) temp.dequeue(); //should make JobTest a
		System.out.println("Break 10");							//job with (2,2)
//		System.out.println("JobTest " + JobTest);
		System.out.println("Test again: " + Test);
		System.out.println("Break 11");	
		int k = Test.length();
		System.out.println("Break 12");	
		System.out.println(k);
		System.out.println("Break 13");	
//		Test.dequeueAll();
		System.out.println("Break 14");	
//		Test.enqueue(A);
		System.out.println(Test);
		Job JobTest2 = (Job) Test.getNext();
		System.out.println("get next " + JobTest2);
		System.out.println("Break 15");	
		Job testing = (Job) Test.peek();
		System.out.println("testing: " + testing);
	}
}
