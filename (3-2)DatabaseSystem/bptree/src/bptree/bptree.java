package bptree_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

public class bptree {
	
	/**
	 * This class is for implementation of B+ tree.
	 * There are four main functions
	 * Insertion , Deletion , Search single key , Search in range 
	 * These functions are implemented using hash maps and array lists.
	 * About manual, take a look at README.md
	 *
	 * @version     1.00 September 2017
	 * @author      KOOJANGHOI
	 * @see         https://github.com/KOOJANGHOI
	 * @email       kjanghoi@gmail.com
	 */

	public static final int INF = 10000000;
	static int b;		// max # of child(in NonLeafNode)/value(in LeafNode)
	public static int maxlevel = 0;	// level of root node
	public static Node root;			// root node
	public static NonLeafNode newroot;	// new root node when new root node is created
	public static Map<Integer, ArrayList<Node>> nmap = new HashMap<Integer, ArrayList<Node>>();	// HashMap store level as key, ArrayList<Node> in the level as value
	public static ArrayList<Node> narr = null;	// ArrayList<Node> store nodes in specific level
	public static PrintWriter pw;	

	
	/**
	  *	Write meta-data into .dat file using PrintWriter
	  * @param path		path of .dat file    
	  * @param filename	name of .dat file    
	  * @return void
	  */
	public static void writeMetaData(String path , String filename) throws FileNotFoundException {
		pw = new PrintWriter(new FileOutputStream(path+filename, true));
		Node temp;
		pw.println("----------------------------------------MetaData------------------------------------------");
		for(int i = maxlevel ; i >= 0 ; i--) {
			pw.println("----------------------------------------[Level:"+i+"]-----------------------------------------");
			narr = nmap.get(i);
			for(int j = 0 ; j < narr.size() ; j++) {
				temp = narr.get(j);
				if(temp.isLeafNode) {
					pw.println("[Level:"+temp.level+"][Id:"+temp.id+"][# of value:"+temp.m+"][Ptr:"+temp.r+"]");
				} else {
					pw.println("[Level:"+temp.level+"][Id:"+temp.id+"][# of child:"+temp.m+"][Ptr:"+temp.r+"]");
				}
				for(int k = 0 ; k < b ; k++) {
					if(temp.p[k][0] != INF)
					pw.print("["+temp.p[k][0]+"]   ");
				}
				pw.println();
				for(int k = 0 ; k < b ; k++) {
					if(temp.p[k][0] != INF)
					pw.print("["+temp.p[k][1]+"]   ");
				}
				pw.println();
				pw.println("------------------------------------------------------------------------------------------");
			}	
		}
		pw.close();
	}
	
	/**
	  * When new node is created, add to HashMap.
	  * @param level		level of new node   
	  * @param node		new node
	  * @return void    
	  */
	public static void addNodeToMap(int level, Node node) {
		ArrayList<Node> narr = null;
		if (nmap.containsKey(level)) {
			narr = nmap.get(level);
			if (narr == null)
				narr = new ArrayList<Node>();
			narr.add(node);
		} else {
			narr = new ArrayList<Node>();
			narr.add(node);
		}
		nmap.put(level, narr);
	}
	
	/**
	  * Find Node by its level and id
	  * @param level		level of node    
	  * @param id		id of node  
	  * @return Node		that node
	  */
	public static Node findNodeByLevAndId(int level, int id) {
		ArrayList<Node> narr = null;
		Node temp = null;
		if (nmap.containsKey(level)) {
			narr = nmap.get(level);
			for (int i = 0; i < narr.size(); i++) {
				if (id == narr.get(i).id)
					temp = narr.get(i);
			}
			return temp;
		}
		return null;
	}
	
	/**
	  * Find LeafNode by its key
	  * @param key		key of a LeafNode    
	  * @return Node		that LeafNode  
	  */
	public static Node findLeafNodeByKey(int key) {
		ArrayList<Node> narr = null;
		LeafNode temp = null;
		if (!nmap.containsKey(0))
			return null;
		narr = nmap.get(0);
		for (int i = 0; i < narr.size(); i++) {
			for (int j = 0; j < b; j++) {
				if (key == narr.get(i).p[j][0]) {
					temp = (LeafNode) narr.get(i);
					return temp;
				}
			}
		}
		return null;
	}
	
	/**
	  * Find appropriate node when insertion is invoked
	  * @param entry		start point. It can be root or not
	  * @param key		the key to be inserted
	  * @return Node		appropriate LeafNode to be inserted
	  */
	public static Node whereToInsert(Node entry, int key) {
		Node temp = null;
		if (entry.isLeafNode)
			return entry;
		if (key > entry.p[entry.m - 2][0]) {
			temp = findNodeByLevAndId(entry.level - 1, entry.r);
		} else {
			for (int i = 0; i < entry.m - 1; i++) {
				if (key <= entry.p[i][0]) {
					temp = findNodeByLevAndId(entry.level - 1, entry.p[i][1]);
					break;
				}
			}
		}
		if (temp.isLeafNode) {
			return temp;
		} else {
			return whereToInsert(temp, key);
		}
	}
	
	/**
	  * Print all node by level(ascending order)
	  * @param void   
	  * @return void
	  */
	public static void printCurrentStatus() {
		Iterator<Integer> it = nmap.keySet().iterator();
		while (it.hasNext()) {
			int ikey = Integer.parseInt(it.next().toString());
			narr = nmap.get(ikey);
			if (narr != null) {
				for (Node node : narr) {
					node.printNode();
				}
			}
		}
	}
	
	/**
	  * Find parent node
	  * @param Node		child node   
	  * @return Node		parent of the child node  
	  */
	public static Node findParentNode(Node node) {
		ArrayList<Node> narr = null;
		NonLeafNode temp = null;
		if (nmap.containsKey(node.level + 1))
			narr = nmap.get(node.level + 1);
		for (int i = 0; i < narr.size(); i++) {
			temp = (NonLeafNode) narr.get(i);
			for (int j = 0; j < b; j++) {
				if (node.id == temp.p[j][1])
					return temp;
			}
			if (node.id == temp.r)
				return temp;
		}
		return null;
	}
	
	/**
	  * Sort all node by its first key using HashMap,ArrayList
	  * As a result, all own id and pointer to child node is re-distributed
	  * @param void
	  * @return void  
	  */
	public static void sortAllNode() {
		NonLeafNode temp = null;
		int tmp = 0;
		for (int lev = 0; lev <= maxlevel; lev++) {
			Collections.sort(nmap.get(lev), new Comparator<Node>() {
				@Override
				public int compare(Node n1, Node n2) {
					if (n1.p[0][0] < n2.p[0][0]) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			for (int i = 0; i < nmap.get(lev).size(); i++) {
				nmap.get(lev).get(i).id = i;
			}
		}
		for (int i = 0; i < nmap.get(0).size(); i++) {
			nmap.get(0).get(i).r = nmap.get(0).get(i).id + 1;
		}
		for (int lev = 1; lev <= maxlevel; lev++) {
			for (int i = 0; i < nmap.get(lev).size(); i++) {
				temp = (NonLeafNode) nmap.get(lev).get(i);
				for (int j = 0; j < temp.m - 1; j++) {
					temp.p[j][1] = tmp;
					tmp++;
				}
				for (int j = temp.m - 1; j < b - 1; j++) {
					temp.p[j][0] = INF;
					temp.p[j][1] = -1;
				}
				temp.r = tmp;
				tmp++;
			}
		}
		temp = (NonLeafNode) nmap.get(maxlevel).get(0);
		tmp = temp.m;
		for (int i = 0; i < tmp - 1; i++) {
			temp.p[i][1] = i;
		}
		temp.r = tmp - 1;
	}
	
	/**
	  * Transfer to left sibling(in LeafNode)
	  * @param Leafnode	where underflow occurred    
	  * @return void  
	  */
	public static void transferToLeftLeafSibling(LeafNode node) {
		LeafNode leftSibling = (LeafNode) findNodeByLevAndId(0, node.id - 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(leftSibling);
		int index = parent.isContainKey(node.p[0][0]);
		node.InsertAndSort(leftSibling.p[leftSibling.m - 1][0], leftSibling.p[leftSibling.m - 1][1]);
		leftSibling.DeleteAndSort(node.p[0][0]);
		if (parent.id == parent2.id) {
			parent.p[index][0] = node.p[0][0];
		}

	}
	
	/**
	  * Transfer to left sibling(in NonLeafNode)
	  * @param NonLeafnode	where underflow occurred    
	  * @return void  
	  */
	public static void transferToLeftNonLeafSibling(NonLeafNode node) {
		NonLeafNode leftSibling = (NonLeafNode) findNodeByLevAndId(node.level, node.id - 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(leftSibling);
		if (parent.id == parent2.id) {
			int index = 0;
			for (int i = 0; i < parent.m - 1; i++) {
				if (parent.p[i][1] == leftSibling.id)
					index = i;
			}
			node.InsertAndSort(parent.p[index][0]);
			parent.p[index][0] = leftSibling.p[leftSibling.m - 2][0];
		} else {
			node.InsertAndSort(leftSibling.p[leftSibling.m - 2][0]);
		}
		leftSibling.DeleteAndSort(leftSibling.p[leftSibling.m - 2][0]);
		sortAllNode();
	}
	
	/**
	  * Merge to left sibling(in LeafNode)
	  * @param Leafnode	where underflow occurred    
	  * @return void  
	  */
	public static void mergeToLeftLeafSibling(LeafNode node) {
		LeafNode leftSibling = (LeafNode) findNodeByLevAndId(0, node.id - 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(leftSibling);
		for (int i = 0; i < node.m; i++) {
			leftSibling.InsertAndSort(node.p[i][0], node.p[i][1]);
		}
		int key = node.p[0][0];
		nmap.get(0).remove(node.id);
		if (parent.id != parent2.id) {
			parent2.DeleteAndSort(parent2.p[0][0]);
		} else {
			parent.DeleteAndSort(key);
		}
		sortAllNode();
		if (parent.isUnderflowed()) {
			underflowInNonLeafNode(parent);
		} else if (parent2.isUnderflowed()) {
			underflowInNonLeafNode(parent2);
		}
	}
	
	/**
	  * Merge to left sibling(in NonLeafNode)
	  * @param NonLeafnode	where underflow occurred    
	  * @return void  
	  */
	public static void mergeToLeftNonLeafSibling(NonLeafNode node) {

		NonLeafNode leftSibling = (NonLeafNode) findNodeByLevAndId(0, node.id - 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(leftSibling);
		int index = 0;
		if (parent.id == parent2.id) {
			for (int i = 0; i < parent.m - 1; i++) {
				if (parent.p[i][1] == leftSibling.id)
					index = i;
			}
		} else {
			index = 0;
		}
		leftSibling.InsertAndSort(parent.p[index][0]);
		parent.DeleteAndSort(parent.p[index][0]);
		for (int i = 0; i < node.m - 1; i++) {
			leftSibling.InsertAndSort(node.p[i][0]);
		}
		nmap.get(node.level).remove(node.id);
		sortAllNode();
		if (parent.isUnderflowed()) {
			underflowInNonLeafNode(parent);
		}
	}
	
	/**
	  * Transfer to right sibling(in LeafNode)
	  * @param Leafnode	where underflow occurred    
	  * @return void  
	  */
	public static void transferToRightLeafSibling(LeafNode node) {
		LeafNode rightSibling = (LeafNode) findNodeByLevAndId(0, node.id + 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(rightSibling);
		int index = parent.isContainKey(rightSibling.p[0][0]);
		node.InsertAndSort(rightSibling.p[0][0], rightSibling.p[0][1]);
		rightSibling.DeleteAndSort(rightSibling.p[0][0]);
		if (parent.id == parent2.id) {
			parent.p[index][0] = rightSibling.p[0][0];
		}
	}
	
	/**
	  * Transfer to right sibling(in NonLeafNode)
	  * @param NonLeafnode	where underflow occurred    
	  * @return void  
	  */
	public static void transferToRightNonLeafSibling(NonLeafNode node) {
		NonLeafNode rightSibling = (NonLeafNode) findNodeByLevAndId(node.level, node.id + 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		NonLeafNode parent2 = (NonLeafNode) findParentNode(rightSibling);
		if (parent.id == parent2.id) {
			int index = 0;
			for (int i = 0; i < parent.m - 1; i++) {
				if (parent.p[i][1] == node.id)
					index = i;
			}
			node.InsertAndSort(parent.p[index][0]);
			parent.p[index][0] = rightSibling.p[0][0];
		} else {
			node.InsertAndSort(rightSibling.p[0][0]);
		}
		rightSibling.DeleteAndSort(rightSibling.p[0][0]);
		sortAllNode();
	}
	
	/**
	  * Merge to right sibling(in LeafNode)
	  * @param Leafnode	where underflow occurred    
	  * @return void  
	  */
	public static void mergeToRightLeafSibling(LeafNode node) {
		LeafNode rightSibling = (LeafNode) findNodeByLevAndId(0, node.id + 1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		for (int i = 0; i < rightSibling.m; i++) {
			node.InsertAndSort(rightSibling.p[i][0], rightSibling.p[i][1]);
		}
		int key = rightSibling.p[0][0];
		nmap.get(0).remove(rightSibling.id);
		parent.DeleteAndSort(key);
		sortAllNode();
		if (parent.isUnderflowed()) {
			underflowInNonLeafNode(parent);
		}
	}
	
	/**
	  * Merge to right sibling(in NonLeafNode)
	  * @param NonLeafnode	where underflow occurred    
	  * @return void  
	  */
	public static void mergeToRightNonLeafSibling(NonLeafNode node) {
		NonLeafNode rigntSibling = (NonLeafNode) findNodeByLevAndId(0,node.id+1);
		NonLeafNode parent = (NonLeafNode) findParentNode(node);
		node.InsertAndSort(parent.p[0][0]);
		parent.DeleteAndSort(parent.p[0][0]);
		for(int i = 0 ; i < rigntSibling.m-1; i++) {
			node.InsertAndSort(rigntSibling.p[i][0]);
		}
		nmap.get(node.level).remove(rigntSibling.id);
		sortAllNode();
		if(parent.isUnderflowed()) {
			underflowInNonLeafNode(parent);
		}
	}
	
	/**
	  * Handle overflow in LeafNode
	  * @param Leafnode	where overflow occurred    
	  * @return void  
	  */
	public static void overflowInLeafNode(LeafNode node) {
		LeafNode temp_leafnode = null;
		NonLeafNode temp_nonleafnode = null;
		if (!node.isLeafNode) {
			return;
		}
		if (node.level == maxlevel) {
			temp_leafnode = new LeafNode(maxlevel, node.id + 1, b, node.p[b / 2][0], node.p[b / 2][1], -1);
			for (int i = (int) b / 2 + 1; i < b; i++) {
				temp_leafnode.InsertAndSort(node.p[i][0], node.p[i][1]);
			}
			addNodeToMap(maxlevel, temp_leafnode);
			maxlevel++;
			newroot = new NonLeafNode(maxlevel, 0, b, node.p[b / 2][0], 0, 1);
			addNodeToMap(maxlevel, newroot);
			node.deleteHalf();
			root = newroot;
		} else {
			temp_nonleafnode = (NonLeafNode) findParentNode(node);
			temp_leafnode = new LeafNode(0, node.id + 1, b, node.p[b / 2][0], node.p[b / 2][1], node.id + 2);
			for (int i = (int) b / 2 + 1; i < b; i++) {
				temp_leafnode.InsertAndSort(node.p[i][0], node.p[i][1]);
			}
			temp_nonleafnode.InsertAndSort(node.p[b / 2][0]);
			addNodeToMap(0, temp_leafnode);
			node.deleteHalf();
			if (temp_nonleafnode.isOverflowed()) {
				overflowInNonLeafNode(temp_nonleafnode);
			} else {
				sortAllNode();
			}
		}
	}

	
	/**
	  * Handle overflow in NonLeafNode
	  * @param NonLeafnode	where overflow occurred    
	  * @return void  
	  */
	public static void overflowInNonLeafNode(NonLeafNode node) {
		NonLeafNode parent = null;
		NonLeafNode sibling = null;
		if (node.level == maxlevel) {
			sibling = new NonLeafNode(node.level, node.id + 1, b, node.p[b / 2 + 1][0], node.p[b / 2 + 1][1], node.r);
			if (b / 2 + 2 < b) {
				for (int i = b / 2 + 2; i < b; i++) {
					sibling.InsertAndSort(node.p[i][0]);
				}
			}
			addNodeToMap(node.level, sibling);
			maxlevel++;
			newroot = new NonLeafNode(maxlevel, 0, b, node.p[b / 2][0], 0, 1);
			addNodeToMap(maxlevel, newroot);
			node.deleteHalf();
			root = newroot;
			sortAllNode();
		} else {
			parent = (NonLeafNode) findParentNode(node);
			sibling = new NonLeafNode(node.level, node.id + 1, b, node.p[b / 2 + 1][0], node.p[b / 2 + 1][1], node.r);
			if (b / 2 + 2 < b) {
				for (int i = b / 2 + 2; i < b; i++) {
					sibling.InsertAndSort(node.p[i][0]);
				}
			}
			parent.InsertAndSort(node.p[b / 2][0]);
			addNodeToMap(node.level, sibling);
			node.deleteHalf();
			sortAllNode();
			if (parent.isOverflowed()) {
				overflowInNonLeafNode(parent);
			}
		}
		return;
	}

	
	/**
	  * Handle underflow in LeafNode
	  * @param Leafnode	where underflow occurred    
	  * @return void  
	  */
	public static void underflowInLeafNode(LeafNode node) {
		LeafNode leftSibling = (LeafNode) findNodeByLevAndId(0, node.id - 1);
		LeafNode rightSibling = (LeafNode) findNodeByLevAndId(0, node.id + 1);
		if (node.id == 0) {
			if (rightSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToRightLeafSibling(node);
			} else {
				mergeToRightLeafSibling(node);
			}
		} else if (node.id == nmap.get(0).size() - 1) {
			if (leftSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToLeftLeafSibling(node);
			} else {
				mergeToLeftLeafSibling(node);
			}
		} else {
			if (leftSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToLeftLeafSibling(node);
			} else if (rightSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToRightLeafSibling(node);
			} else {
				mergeToLeftLeafSibling(node);
			}
		}
	}
	
	/**
	  * Handle underflow in NonLeafNode
	  * @param NonLeafnode	where underflow occurred    
	  * @return void  
	  */
	public static void underflowInNonLeafNode(NonLeafNode node) {
		NonLeafNode leftSibling = (NonLeafNode) findNodeByLevAndId(node.level, node.id - 1);
		NonLeafNode rightSibling = (NonLeafNode) findNodeByLevAndId(node.level, node.id + 1);
		if (node.id == 0) {
			if (rightSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToRightNonLeafSibling(node);
			} else {
				mergeToRightNonLeafSibling(node);
			}
		} else if (node.id == nmap.get(node.level).size() - 1) {
			if (leftSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToLeftNonLeafSibling(node);
			} else {
				mergeToLeftNonLeafSibling(node);
			}
		} else {
			if (leftSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToLeftNonLeafSibling(node);
			} else if (rightSibling.m >= (int) Math.floor((b + 1) / 2) + 1) {
				transferToRightNonLeafSibling(node);
			} else {
				mergeToLeftNonLeafSibling(node);
			}
		}
	}
	
	/**
	  * Insert key,value to B+ tree
	  * @param key	key to be inserted
	  * @param value value to be inserted    
	  * @return void 
	  */
	public static void Insertion(int key, int value) {
		Node temp = null;
		LeafNode newleafnode = null;
		if (root == null) {
			root = new LeafNode(0, 0, b, key, value, -1);
			addNodeToMap(0, root);
			maxlevel = 0;
			return;
		} else if (root.isLeafNode) {
			((LeafNode) root).InsertAndSort(key, value);
			if (((LeafNode) root).isOverflowed()) {
				overflowInLeafNode((LeafNode) root);
			}
		} else if (!root.isLeafNode) {
			temp = whereToInsert(root, key);
			if (!temp.isLeafNode) {
				System.out.println("Fail!![0]");
			}
			newleafnode = (LeafNode) temp;
			newleafnode.InsertAndSort(key, value);
			if (newleafnode.isOverflowed()) {
				overflowInLeafNode((LeafNode) newleafnode);
			}
		}
	}
	
	/**
	  * Delete key to B+ tree
	  * @param key	key to be deleted   
	  * @return void  
	  */
	public static void Deletion(int key) {
		LeafNode temp;
		NonLeafNode parent;
		temp = (LeafNode) findLeafNodeByKey(key);
		parent = (NonLeafNode) findParentNode(temp);
		if (temp == null)
			return;
		temp.DeleteAndSort(key);
		if (parent.isContainKey(key) != -1) {
			int tmp = parent.isContainKey(key);
			parent.p[tmp][0] = temp.p[0][0];
		}
		if (temp.isUnderflowed()) {
			underflowInLeafNode(temp);
		}
	}

	
	/**
	  * Find index(where the key is stored in the array of node)
	  * @param node	node to be searched
	  * @param key 	key in that node    
	  * @return index	index of key
	  */
	public static int SearchKeyInNode(Node node, int key) {
		for (int i = 0; i < node.m; i++) {
			if (key == node.p[i][0]) {
				return i;
			}
		}
		return -1;
	}

	
	/**
	  * Search single key
	  * @param node	entry node as starting point of searching
	  * @param key 	key to be searched
	  * @return void
	  */
	public static void SearchSingleKey(Node entry, int key) {
		Node temp = null;
		int index = 0;
		if (entry.isLeafNode) {
			index = SearchKeyInNode(entry, key);
			if (index != -1) {
				System.out.println(entry.p[index][1]);
				return;
			} else {
				System.out.println("NOT FOUND");
				return;
			}
		} else {
			for (int i = 0; i < entry.m - 2; i++) {
				System.out.print(entry.p[i][0] + ",");
			}
			System.out.print(entry.p[entry.m - 2][0]);
			System.out.println();
		}

		if (key >= entry.p[entry.m - 2][0]) {
			temp = findNodeByLevAndId(entry.level - 1, entry.r);
		} else {
			for (int i = 0; i < entry.m - 1; i++) {
				if (key < entry.p[i][0]) {
					temp = findNodeByLevAndId(entry.level - 1, entry.p[i][1]);
					break;
				}
			}
		}
		SearchSingleKey(temp, key);
	}

	
	/**
	  * Search key in specific range(print all keys between key1 and key2)
	  * @param key1		
	  * @param key2
	  * @return void		
	  */
	public static void RangedSearch(int key1, int key2) {
		LeafNode temp = null;
		for (int i = 0; i < nmap.get(0).size(); i++) {
			temp = (LeafNode) nmap.get(0).get(i);
			for (int j = 0; j < temp.m; j++) {
				if ((key1 <= temp.p[j][0]) && (temp.p[j][0] <= key2)) {
					System.out.println(temp.p[j][0] + "," + temp.p[j][1]);
				}

			}
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "/Users/KJH/eclipse-workspace/bptree_1/src/";
		String indexfile, datafile;
		String line;
		int startkey, endkey, searchkey;
		PrintWriter pw;

		if (args.length == 3) {
			switch (args[0]) {
			case "-c":
				indexfile = args[1];
				b = Integer.parseInt(args[2]);
				pw = new PrintWriter(new FileOutputStream(path+indexfile, false));
				try {
					pw.println("c " + b);
				} finally {
					pw.close();
				}
				break;
			case "-i":
				line = " ";
				indexfile = args[1];
				datafile = args[2];
				try (BufferedReader br1 = new BufferedReader(new FileReader(indexfile))) {
					while ((line = br1.readLine()) != null) {
						String[] data1 = line.split(" ");
						if (data1[0].equals("c")) {
							b = Integer.parseInt(data1[1]);
							break;
						}
					}
					br1.close();
					pw = new PrintWriter(new FileOutputStream(path+indexfile, true));
					BufferedReader br2 = new BufferedReader(
							new InputStreamReader(new FileInputStream(path + datafile), "euc-kr"));
					while ((line = br2.readLine()) != null) {
						String[] data2 = line.split(",");
						pw.println("i " + data2[0] + " " + data2[1]);
						Insertion(Integer.parseInt(data2[0]), Integer.parseInt(data2[1]));
					}
					pw.close();
					br2.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					writeMetaData(path,indexfile);
				}
				break;
			case "-d":
				indexfile = args[1];
				datafile = args[2];
				try (BufferedReader br1 = new BufferedReader(new FileReader(indexfile))) {
					while ((line = br1.readLine()) != null) {
						String[] data1 = line.split(" ");
						if (data1[0].equals("c")) {
							b = Integer.parseInt(data1[1]);
						} else if (data1[0].equals("i")) {
							Insertion(Integer.parseInt(data1[1]), Integer.parseInt(data1[2]));
						}
					}
					br1.close();
					BufferedReader br2 = new BufferedReader(
							new InputStreamReader(new FileInputStream(path + datafile), "euc-kr"));
					while ((line = br2.readLine()) != null) {
						String[] data2 = line.split(" ");
						Deletion(Integer.parseInt(data2[0]));
					}
					br2.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					writeMetaData(path,indexfile);
				}
				break;
			case "-s":
				indexfile = args[1];
				searchkey = Integer.parseInt(args[2]);
				try (BufferedReader br1 = new BufferedReader(new FileReader(indexfile))) {
					while ((line = br1.readLine()) != null) {
						String[] data1 = line.split(" ");
						if (data1[0].equals("c")) {
							b = Integer.parseInt(data1[1]);
						} else if (data1[0].equals("i")) {
							Insertion(Integer.parseInt(data1[1]), Integer.parseInt(data1[2]));
						}
					}
					br1.close();
					SearchSingleKey(root, searchkey);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("Syntax error(args.length is 3)!");
			}
		} else if (args.length == 4) {
			if (args[0].equals("-r")) {
				line = " ";
				indexfile = args[1];
				startkey = Integer.parseInt(args[2]);
				endkey = Integer.parseInt(args[3]);
				try (BufferedReader br1 = new BufferedReader(new FileReader(indexfile))) {
					while ((line = br1.readLine()) != null) {
						String[] data1 = line.split(" ");
						if (data1[0].equals("c")) {
							b = Integer.parseInt(data1[1]);
						} else if (data1[0].equals("i")) {
							Insertion(Integer.parseInt(data1[1]), Integer.parseInt(data1[2]));
						}
					}
					br1.close();
					RangedSearch(startkey, endkey);
				} catch (IOException e) {
					e.printStackTrace();
				} 
			} else {
				System.out.println("Syntax error(args.length is 4)!");
			}
		} else {
			System.out.println("Syntax error(args.length is " + args.length + ")!");
		}	
	}
}
