package bptree_1;

public class LeafNode extends Node {
	
	public LeafNode(int level, int id , int b, int key, int value, int ptr) {
		this.level = level;
		this.id = id;
		this.b = b;
		
		isLeafNode = true;
		m = 1;
		p = new int[b][2];
		for(int i = 0 ; i < b ; i++) {
			p[i][0] = INF;
			p[i][1] = 0;
		}
		p[0][0] = key;
		p[0][1] = value;
		r = ptr;
	}	
	
	/**
	  * Insert a key and sort own array
	  * @param key   
	  * @return void    
	  */
	public void InsertAndSort(int key, int value) {
		p[m][0] = key;
		p[m][1] = value;
		m++;
		for(int i = 0 ; i < b ; i++) {
			for (int j = i + 1; j < b ; j++) {
				if (p[i][0] > p[j][0]) {
					arraySwap(p, i, j);
				}
			}
		}
	}
	
	/**
	  * Delete half of own array especially when overflow occurred
	  * @param void
	  * @return void    
	  */
	public void deleteHalf() {
		for(int i = (int)b/2 ; i < b ; i++) {
			this.p[i][0] = INF;
			this.p[i][1] = -1;
		}
		this.m = b/2;
		this.r = this.id + 1;
	}
	
	public boolean isOverflowed() {
		return m == b ;
	}
}
