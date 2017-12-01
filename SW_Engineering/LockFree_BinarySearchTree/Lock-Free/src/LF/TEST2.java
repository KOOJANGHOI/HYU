package LF;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/* 1 million random number insert into BST */
/* according to # of thread , ratio of insert/search */
/* BST : Fine Grained Lock using ReentrantLock */
public class TEST2 extends Thread {
	static Random random;
	static BST_LF bst;// Fine Grained Lock BST using ReentrantLock
	static int N;// # of thread
	int seq;// sequence of thread

	public TEST2(int seq) {
		this.seq = seq;
	}

	public void run() {
		try {
			random = new Random();
			/* sum of insert/search = 1 million */
			/* Number of times allocated to each thread(cnt) = 100000/(# of thread) */ 
			/* Ratio of insert/search(R) = 5(1:1) . 2(1:4) . 0(1:9) */
			int cnt = 1000000 / N;
			int R = 0;
			for (int i = 0; i < cnt; i++) {
				int tmp = random.nextInt(9);
				int num = random.nextInt();
				if(R < 3) {
					bst.insert(num);
				} else if(R < 6) {
					int res = bst.delete(num);
				} else {
					boolean res = bst.search(num);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("[ADDITIONAL 1 MILLION RANDOM NUMBER INSERT/SEARCH TEST]");
		System.out.print("[INPUT(# of thread)]:");
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		scan.close();
		double avgTime = 0;
		/* 10 times test */
		for (int j = 0; j < 10; j++) {
			bst = new BST_LF(); // create BST object
			random = new Random();
			//long startTime = System.currentTimeMillis(); // start time
			/* insert 1 million random number into BST */
			ArrayList<Thread> threads = new ArrayList<Thread>();
			long startTime = System.currentTimeMillis(); // start time
			for (int i = 0; i < N; i++) {
				Thread t = new TEST2(i);
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