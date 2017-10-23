package SocialNetwork;

public class FriendGraph {
	/**
	 * This class is for implementation of SocialNetwork. The main function is
	 * calculate distance between Friends. This function is implemented using BFS
	 * algorithm. About manual, take a look at README.md
	 *
	 * @version 1.00. September 2017
	 * @author KOOJANGHOI
	 * @see https://github.com/KOOJANGHOI
	 * @email kjanghoi@gmail.com
	 */

	private static final int max = 50; // max # of Person
	private int[][] arr; // for BFS graph. if two person are connected, it store 1. if not , it store 0
	private int[] queue; // a queue
	private int[] visited; // if 0, is not visited. if 1 , is visited
	private int[] distance; // array of distance
	private int rear; // rear for queue
	private int front; // front for queue
	private String[] name; // array of name
	private int num; // # of Person in FriendGraph

	// Constructor of FriendGraph
	public FriendGraph() {
		this.num = 0;
		this.rear = 0;
		this.front = 0;
		this.arr = new int[max][max];
		this.queue = new int[max];
		this.visited = new int[max];
		this.distance = new int[max];
		this.name = new String[max];
		for (int i = 0; i < max; i++) {
			queue[i] = 0;
			visited[i] = 0;
			name[i] = "";
			distance[i] = 0;
			for (int j = 0; j < max; j++) {
				arr[i][j] = 0;
			}
		}
	}

	/**
	  *	Write add Person into FriendGraph
	  * @param per		Person object       
	  * @return void
	  */
	public void addPerson(Person per) {
		name[this.num] = per.getName();
		num++;
	}

	/**
	  *	Find designated number(index of name array) for specific name
	  * @param name		name to be searched  
	  * @return index	if found, return index. if not, return -1
	  */
	public int findIndex(String name) {
		int index;
		for (index = 0; index < max; index++) {
			if (this.name[index].equals(name))
				break;
		}
		if (index == max) {
			System.out.println("[" + name + "]is not registered!!");
			return -1;
		} else {
			return index;
		}

	}

	/**
	  *	Return whether two Person are connected or not
	  * @param name1		name of first Person    
	  * @param name2		name of second Person
	  * @return boolean 	if connected ,return true. if not, return false
	  */
	public boolean connected(String name1, String name2) {
		int row = findIndex(name1);
		int col = findIndex(name2);
		if (arr[row][col] == 1)
			return true;
		return false;
	}

	/**
	  *	Make Friendship between two Person by changing value of arr[][] to 1
	  * @param name1		name of first Person    
	  * @param name2		name of second Person
	  * @return void
	  */
	public void addFriendship(String name1, String name2) {
		int row = findIndex(name1);
		int col = findIndex(name2);
		arr[row][col] = 1;
		arr[col][row] = 1;
	}

	/**
	  *	Return small value between two argument(integer)
	  * @param x			first integer    
	  * @param x			second integer
	  * @return int		if x <= y , return x. else return y
	  */
	public int min(int x, int y) {
		if (x <= y) {
			return x;
		} else {
			return y;
		}
	}

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

	/**
	  *	Calculate distance between two person
	  * @param name1		name of first person(like source)  
	  * @param name2		name of second person(like destination)
	  * @return int		return distance.(if two person are not connected,return -1. if two person are same person,return 0
	  */
	public int getDistance(String name1, String name2) {
		// if two person are same person
		if (name1.equals(name2))
			return 0;
		int row = findIndex(name1);
		int col = findIndex(name2);
		BFS(row);
		// if two person are not connected
		if (distance[col] == 0) {
			return -1;
		}
		return distance[col];
	}
}
