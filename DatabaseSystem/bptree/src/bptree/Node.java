package bptree_1;



public class Node {
	public static final int INF = 10000000;
	boolean isLeafNode;
	int level;
	int id;
	int m = 0;	// # of child(in NonLeafNode)/value(in LeafNode)
	int b;		// max # of child(in NonLeafNode)/value(in LeafNode)
	int[][] p;	// array of key-ptr(in NonLeafNode)/key-value(in LeafNode)
	int r;		// right most ptr

	/**
	  * When the node has key, return index of array
	  * @param key
	  * @return index    
	  */
	public int isContainKey(int key) {
		for(int i = 0 ; i < b ; i++) {
			if(this.p[i][0] == key) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	  * Delete a key and sort own array
	  * @param key   
	  * @return void    
	  */
	public void DeleteAndSort(int key) {
		int index = this.isContainKey(key);
		if(index == -1)
			return ;
		p[index][0] = INF;
		p[index][1] = -1;
		m--;
		for(int i = 0 ; i < b ; i++) {
			for (int j = i + 1; j < b ; j++) {
				if (p[i][0] > p[j][0]) {
					arraySwap(p, i, j);
				}
			}
		}
	}
	
	public void arraySwap(int[][] arr, int a, int b) {
		int[] temp = new int[2];
		for (int i = 0; i < 2 ; i++) {
			temp[i] = arr[a][i];
			arr[a][i] = arr[b][i];
			arr[b][i] = temp[i];
		}
	}
	
	public boolean isUnderflowed() {
		return m < (int)Math.floor((b+1)/2);
	}
	
	public void printNode() {
		System.out.println("================================================================");
		System.out.print("[Level:"+level+"]");
		System.out.print("[Leaf:"+isLeafNode+"]");
		System.out.print("[Id:"+id+"]");
		if(isLeafNode) {
			System.out.print("[# of value:"+m+"]");
			System.out.print("[max # of value:"+b+"]");	
			System.out.println();
			for(int i = 0 ; i < b ; i++) {
				if(p[i][0] != INF) 
					System.out.print("["+p[i][0]+"]      ");
			}
			System.out.println();
			for(int i = 0 ; i < b ; i++) {
				if(p[i][0] != INF)
				System.out.print("["+p[i][1]+"]");
			}
			System.out.print("["+r+"]");
			
		}else {
			System.out.print("[# of child:"+m+"]");
			System.out.print("[max # of child:"+b+"]");
			System.out.println();
			for(int i = 0 ; i < b ; i++) {
				if(p[i][0] != INF)
				System.out.print("["+p[i][0]+"]      ");
			}
			System.out.println();
			for(int i = 0 ; i < b ; i++) {
				if(p[i][0] != INF)
				System.out.print("["+p[i][1]+"]");
			}
			System.out.print("["+r+"]");
		}
		System.out.println("\n================================================================");
	}
}
