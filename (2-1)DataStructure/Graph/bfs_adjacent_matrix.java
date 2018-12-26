package SocialNetwork;
public class FriendGraph {
	
	private static final int max = 50; // max # of Person
	private int[][] arr; // for BFS graph. if two person are connected, it store 1. if not , it store 0
	private int[] queue; // a queue
	private int[] visited; // if 0, is not visited. if 1 , is visited
	private int[] distance; // array of distance
	private int rear; // rear for queue
	private int front; // front for queue
	
	/**
	  *	Push integer(representing specific person) into queue
	  * @param num		a number representing a person    
	  * @return void
	  */
	public void push(int num) {
		queue[rear++] = num;
	}
	/**
	  *	Pop integer(representing specific person) into queue
	  * @param void	    
	  * @return void
	  */
	public int pop() {
		return queue[front++];
	}
	
	/**
	  * Typical BFS algorithm
	  * @param s			source   
	  * @return void
	  */
	public void BFS(int s) {
		for (int i = 0; i < max; i++) {
			if (i != s) {
				visited[i] = 0;
				distance[i] = -1;
			}
		}
		visited[s] = 1;
		distance[s] = 0;
		push(s);
		while (!(rear == front)) {
			int temp = pop();
			for (int i = 0; i < max; i++) {
				if (arr[temp][i] == 1 && visited[i] == 0) {
					visited[i] = 1;
					distance[i] = distance[temp] + 1;
					push(i);
				}
			}
			visited[temp] = 2;
		}
	}
	
}