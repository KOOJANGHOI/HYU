package LF;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/* 1 million random number insert into BST. according to # of thread */
/* BST : Fine Grained Lock using ReentrantLock */
public class TEST1 extends Thread {
	static BST_LF bst; // Fine Grained Lock BST using ReentrantLock
	static int N; // # of thread
	int seq; // sequence of thread
	public TEST1(int seq) {
		this.seq = seq;
	}

	public void run() {
		try {
			Random random = new Random();
			/* sum of insert = 1 million */
			/* Number of times allocated to each thread(cnt) = 100000/(# of thread) */ 
			int cnt = 1000000 / N;
			for (int i = 0; i < cnt; i++) {
				int num = random.nextInt();
				bst.insert(num);			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("[1 MILLION RANDOM NUMBER INSERT TEST]");
		System.out.print("[INPUT(# of thread)]:");
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		scan.close();
		double avgTime = 0;
		/* 10 times test */
		for (int j = 0; j < 10; j++) {
			bst = new BST_LF(); // create BST object
			long startTime = System.currentTimeMillis(); // start time
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (int i = 0; i < N; i++) {
				Thread t = new TEST1(i);
				t.start();
				threads.add(t);
			}
			for (int i = 0; i < threads.size(); i++) {
				Thread t = threads.get(i);
				try {
					t.join();
				} catch (Exception e) {
				}
			}
			long endTime = System.currentTimeMillis(); // end time
			double elapsedTime = (endTime-startTime)/1000.0;
			avgTime += elapsedTime;
			System.out.println("["+(j+1)+"][" + elapsedTime + "]"); // elapsed time
		}
		System.out.println("[avg]["+avgTime/10+"]");
	}
}