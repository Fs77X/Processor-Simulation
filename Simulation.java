//CMPS12B/M
//Simulation.java
//Simulation, a program in which uses Queue.java and Job.java to create a simulation of processors that "process" Jobs

import java.io.*;
import java.util.Scanner;

public class Simulation {

	// -----------------------------------------------------------------------------
	//
	// The following function may be of use in assembling the initial backup and/or
	// storage queues. You may use it as is, alter it as you see fit, or delete it
	// altogether.
	//
	// -----------------------------------------------------------------------------

	public static Job getJob(Scanner in) {
		String[] s = in.nextLine().split(" ");
		int a = Integer.parseInt(s[0]);
		int d = Integer.parseInt(s[1]);
		return new Job(a, d);
	}

	// -----------------------------------------------------------------------------
	//
	// The following stub for function main contains a possible algorithm for this
	// project. Follow it if you like. Note that there are no instructions below
	// which mention writing to either of the output files. You must intersperse
	// those commands as necessary.
	//
	// -----------------------------------------------------------------------------

	public static void main(String[] args) throws IOException {

		// 1. check command line arguments
		if (args.length != 1) {
			throw new IOException("Need file name");

		}
		//
		// 2. open files for reading and writing
		File file = new File(args[0]);
		Scanner in = new Scanner(file);
		Scanner in2 = new Scanner(file); //two scanners because it looks like you can't reset where a scanner is looking
		in2.nextLine();
		// 3. read in m jobs from input file
		int m = Integer.parseInt(in.nextLine());
		Queue jobs = new Queue();
		Queue storage = new Queue(); //keeping storage to restore our jobs queue when running the simulation again with more processors
		while (in.hasNext()) {
			jobs.enqueue(getJob(in));
		}
		while (in2.hasNext()) { 
			storage.enqueue(getJob(in2));
		}
		// int jobsLen = jobs.length();
		PrintWriter report = new PrintWriter(new FileWriter(args[0] + ".rpt"));
		PrintWriter trace = new PrintWriter(new FileWriter(args[0] + ".trc"));
		String rptName = args[0] + ".rpt";
		String trcName = args[0] + ".trc";
		beginRpt(m, report, jobs, rptName); //prints the begining of report and trace
		beginTrc(m, trace, jobs, trcName);

		// 4. run simulation with n processors for n=1 to n=m-1 {
		for (int n = 1; n < m; n++) {
			printProc(n, trace);
			boolean changed = false; //used to print in the trace file if anything has changed

			// 5. declare and initialize an array of n processor Queues and any
			// necessary storage Queues

			Queue[] processors = create_P(n);
			int time = 0;
			// 6. while unprocessed jobs remain {
			while (!jobs.finished(m)) {
				if (time == 0) { //Here to print out time at 0 only
					printTrc(processors, n, trace, jobs, 0);
				}

				// 7. determine the time of the next arrival or finish event and
				// update time
				computeTimes(processors, time, n, changed); //computes times for any new jobs waiting in line
				time++;

				
				// 8. complete all processes finishing now
				for (int i = 0; i < n; i++) { //goes through all processors to find any jobs done
					if (!processors[i].isEmpty()) {
						if (time == ((Job) processors[i].peek()).getFinish()) {
							jobs.enqueue(processors[i].peek());
							processors[i].dequeue();
							changed = true;

						}
					}
				}

				// 9. if there are any jobs arriving now, assign them to a processor
				// Queue of minimum length and with lowest index in the queue array.
				if (!jobs.finished(m)) {
					if (checkIfArrive(jobs, time) > 0) {
						int counter = checkIfArrive(jobs, time); //finds how many jobs there are arriving
						arrive(processors, jobs, counter, time);//queues up all the jobs arriving at time
						changed = true; 
					}

				}
				computeTimes(processors, time, n, changed); //computesTimes for new jobs if they have been in line

				if (changed) { //if anything has been changed, print out
					printTrc(processors, n, trace, jobs, time);
				}
				changed = false; //reset change to false for the next loop

				// 10. } end loop
			}

			int total_wait = 0;
			int max = 0;
			double avg = 0.00;
			double noJobs = ((double) m);

			// 11. compute the total wait, maximum wait, and average wait for
			// all Jobs, then reset finish times
			total_wait = jobs.Total();
			avg = avg((double) total_wait, noJobs);
			max = jobs.max();
			rptWait(total_wait, max, avg, n, report); //method for printing in the rpt file
			total_wait = 0;
			avg = 0.00;
			max = 0;
			jobs.dequeueAll();

			reset(jobs, storage); //method circles through the Queue to reset jobs 

			// 12. } end loop
		}
		report.close();
		trace.close();

		// 13. close input and output files

	}

	public static Queue[] create_P(int n) { // creates queue arrays
		Queue[] a = new Queue[n];
		for (int i = 0; i < n; i++) {
			a[i] = new Queue();
		}
		return a;
	}

	public static int getLeast(Queue[] a) { // returns which processor has the least values
		int x = a[0].length();

		int counter = 0;

		for (int y = 0; y < a.length; y++) {
			if (x > a[y].length()) {
				x = a[y].length();
				counter = y;

			}
		}

		return counter;

	}

	public static double avg(double total, double m) {
		if(total == 0.00){
			return 0.00;
		} //calculates average time
		return total / m;
	}

	public static void print(Queue[] a, int n) { // used for debugging purposes
		for (int i = 0; i < n; i++) {
			if (!a[i].isEmpty()) {
				System.out.println("in " + i + " queue: " + a[i]);
			}
		}

	}

	public static int checkEmpty(Queue[] a) { //finds empty processor, returns -1 if there aren't any
		int x = -1;
		for (int i = 0; i < a.length; i++) {
			if (a[i].isEmpty()) {
				x = i;
				return x;
			}

		}
		return x;
	}

	public static void reset(Queue a, Queue b) { //resets job to the state it was before the simulation ran
		for (int i = 0; i < b.length(); i++) {
			Job current = (Job) b.dequeue();
			current.resetFinishTime();
			a.enqueue(current);
			b.enqueue(current);

		}
	}

	public static void computeTimes(Queue[] p, int time, int n, boolean changed) { //used to compute times when new job was waiting in line
		for(int i = 0; i < n; i++) {
			if(!p[i].isEmpty() && ((Job) p[i].peek()).getFinish() == -1) {
				((Job) p[i].peek()).computeFinishTime(time);
				changed = true;
				
				
			}
			
		}
		
	}


	public static int checkIfArrive(Queue jobs, int time) { //checking if there are multiple jobs with the same arrival
		int counter = 0;
		for (int i = 0; i < jobs.length(); i++) {
			Job current = (Job) jobs.dequeue();
			if (time == current.getArrival()) {
				counter++;
			}
			jobs.enqueue(current);
		}
		return counter;
	}

	public static void arrive(Queue[] p, Queue jobs, int counter, int time) { //will take counter from the method above and run till there's no jobs left with the appropriate arrival time
		for (int i = 0; i < counter; i++) {
			if (checkEmpty(p) != -1) {
				int emptyIndex = checkEmpty(p);
				// System.out.println("time of queing with finish: " + time);
				p[emptyIndex].enqueue(jobs.peek());
				((Job) p[emptyIndex].peek()).computeFinishTime(time);
				jobs.dequeue();

			} else {
				int a = getLeast(p);
				// System.out.println(a);
				// System.out.println("time of queing: " + time);
				p[a].enqueue(jobs.peek());
				jobs.dequeue();

			}

		}
	}

	public static void beginRpt(int m, PrintWriter rpt, Queue jobs, String reportName) { //beginning of report file
		rpt.println("Report file: " + reportName);
		rpt.println(m + " Jobs:");
		rpt.println(jobs);
		rpt.println("");
		rpt.println("***********************************************************");
	}

	public static void rptWait(int total, int max, double avg, int n, PrintWriter rpt) {  //prints out our wait times
		if (n == 1) {
			rpt.print(n + " processor: ");
		} else {
			rpt.print(n + " processors: ");
		}
		rpt.print("totalWait=" + total + ", " + "maxWait=" + max + ", " + "averageWait=");
		rpt.printf("%.2f", avg);
		rpt.print('\n');
	}

	public static void beginTrc(int m, PrintWriter trc, Queue jobs, String reportName) { //beginning of trace file
		trc.println("Trace file: " + reportName);
		trc.println(m + " Jobs:");
		trc.println(jobs);
		trc.println("");

	}

	public static void printProc(int n, PrintWriter trc) { //prints how many processors is the program using 
		trc.println("*****************************");
		if (n == 1) {
			trc.println(n + " processor:");
		} else {
			trc.println(n + " processors:");
		}
		trc.println("*****************************");
	}

	public static void printTrc(Queue[] a, int n, PrintWriter trc, Queue jobs, int time) {//prints in the trace file to keep track of whats going on in the program
		trc.println("time=" + time);
		if (!jobs.isEmpty()) {
			trc.println("0: " + jobs);
		} else {
			trc.println("0: ");
		}
		for (int i = 0; i < n; i++) {
			if (!a[i].isEmpty()) {
				trc.println(i + 1 + ": " + a[i]);
			} else {
				trc.println(i + 1 + ": ");
			}
		}
		trc.println("");

	}

}
