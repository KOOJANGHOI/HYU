package LF;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

/* Lock-Free BST */
public class BST_LF {
	/*
	 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/
	 * AtomicReference.html
	 * 
	 * For An object reference that may be updated atomically
	 */
	AtomicReference<Node> root;

	/* constructor */
	public BST_LF() {
		root = new AtomicReference<Node>(null);
	}

	/*************************************************
	 *
	 * INSERT
	 *
	 **************************************************/

	/**
	 * insert integer into BST
	 * 
	 * load root Node to curNode.
	 * 
	 * [find location] If tree is null , root.CAS(null,newNode). if CAS fail , start
	 * from scratch. else , find leafNode which already have toInsert or which is
	 * relevant location to insert.
	 * 
	 * check edge case 1) root = parentNode if parentNode is marked,
	 * root.CAS(parentNode,null) and start from scratch. else , create new subtree
	 * has newNode and parentNode
	 * 
	 * 2) root != parentNode If parentNode is marked , newParentNode is left of
	 * right child node of gparentNode
	 * 
	 * [Attempt Insertion] create new subtree has newNode and parentNode. and insert
	 * parentNode and new subtree under gparentNode
	 * 
	 * 
	 * @param toInsert
	 *            Integer to be inserted into BST
	 * @return boolean If success, return true
	 */
	public boolean insert(int toInsert) {
		Node newNode = new Node(toInsert);
		Node newParent = null;
		Node curNode = null;
		Node parentNode = null;
		Node gparentNode = null;

		int compare = 0, oldCompare = 0;
		boolean[] marked = { false };

		retry: while (true) {
			curNode = root.get();
			if (curNode == null) {
				if (root.compareAndSet(null, newNode))
					return true;
				else
					continue retry;
			} else {
				while (curNode != null) {
					gparentNode = parentNode;
					parentNode = curNode;
					oldCompare = compare;

					if (curNode.data > toInsert) {
						compare = 1;
					} else if (curNode.data < toInsert) {
						compare = -1;
					} else {
						compare = 0;
					}

					if (compare != 0) {
						curNode = curNode.getChildNode(compare, marked);
					} else {
						if (curNode.isLeaf()) {
							return false;
						} else {
							curNode = curNode.getChildNode(-1, marked);
						}
					}
				}

				if (gparentNode == null) {
					if (parentNode.isMarked()) {
						root.compareAndSet(parentNode, null);
						continue retry;
					}
					newParent = createSubtree(parentNode, newNode, compare);
					if (root.compareAndSet(parentNode, newParent))
						return true;
					else
						continue retry;
				} else if (parentNode.isMarked()) {
					newParent = gparentNode.getChildNode(-oldCompare, marked);
					parentNode = newParent;
					compare = oldCompare;
				}
				newParent = createSubtree(parentNode, newNode, compare);
				if (gparentNode.insertChildNode(oldCompare, parentNode, newParent))
					return true;
				else
					continue retry;
			}
		}
	}

	/*
	 * create subtree
	 * 
	 * 
	 * @param dir direction to original child node
	 * 
	 * @param parent original parentNode
	 * 
	 * @param newNode new childNode
	 * 
	 * @return Node new subtree(new parentNode)
	 */
	private Node createSubtree(Node parentNode, Node newNode, int compare) {
		Node newParent;
		if (compare > 0) {
			newParent = new Node(parentNode.data, newNode, parentNode);
		} else {
			newParent = new Node(newNode.data, parentNode, newNode);
		}
		return newParent;
	}

	/*****************************************************
	 *
	 * DELETE
	 *
	 ******************************************************/

	/**
	 * delete integer into BST
	 * 
	 * load root Node to curNode.
	 * 
	 * [find location] If tree is null , return -1 (deletion is impossible) else ,
	 * find leafNode which already have toDelete
	 * 
	 * If node is founded , If the node is not leafNode , curNode is rightChildNode
	 * of curNode else ,
	 * 
	 * [check edge case] 1) parentNode == null , delete root by
	 * root.CAS(curNode,null) 2) gparentNode == null , delete at parentNode's level
	 * 3) normal case , delete Node and swing pointer and return deleted data. and
	 * gparentNode = parentNode , parentNode = curNode.
	 * 
	 * 
	 * @param toInsert
	 *            Integer to be inserted into BST
	 * @return boolean If success, return true
	 */
	public int delete(int toDelete) {
		Node newParent = null;
		Node curNode = null;
		Node parentNode = null;
		Node gparentNode = null;
		int compare = 0, oldCompare = 0;
		boolean[] marked = { false };
		while (true) {
			curNode = root.get();
			if (curNode == null)
				return -1;
			else {
				parentNode = curNode;
				while (curNode != null) {
					if (curNode.data > toDelete) {
						compare = 1;
					} else if (curNode.data < toDelete) {
						compare = -1;
					} else {
						compare = 0;
					}

					if (compare != 0) {
						curNode = curNode.getChildNode(compare, marked);
					} else {
						if (curNode.isLeaf()) {
							if (parentNode == null) {
								root.compareAndSet(curNode, null);
							} else if (gparentNode == null) {
								newParent = parentNode.getChildNode(-compare, marked);
								root.compareAndSet(parentNode, newParent);
							} else {
								newParent = parentNode.getChildNode(-compare, marked);
								gparentNode.insertChildNode(oldCompare, parentNode, newParent);
							}
							return curNode.data;
						} else
							curNode = curNode.getChildNode(-1, marked);
					}
					gparentNode = parentNode;
					parentNode = curNode;
					oldCompare = compare;
				}
				return -1;
			}
		}
	}

	/*************************************************
	 *
	 * SEARCH
	 *
	 **************************************************/
	/**
	 * search integer in BST similar to insert method.
	 * 
	 * load root Node to curNode.
	 * 
	 * [find location] If tree is null , return false. else , find leafNode which
	 * already have toSearch
	 * 
	 * If founded node is leaf , return true if and only if it is not marked. else ,
	 * curNode is rightChild Node of curNode.
	 * 
	 * @param toSearch
	 *            Integer to be searched in BST
	 * @return boolean If success, return true
	 */

	public boolean search(int toSearch) {
		Node curNode = root.get();
		int compare = 0;
		boolean[] marked = { false };
		while (curNode != null) {
			if (curNode.data > toSearch) {
				compare = 1;
			} else if (curNode.data < toSearch) {
				compare = -1;
			} else {
				compare = 0;
			}
			if (compare != 0) {
				curNode = curNode.getChildNode(compare, marked);
			} else {
				if (curNode.isLeaf())
					return !curNode.isMarked();
				else
					curNode = curNode.getChildNode(-1, marked);
			}
		}
		return false;
	}

	/* Node Class */
	private class Node {
		private int data;
		private Node left, right;

		/*
		 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/
		 * AtomicMarkableReference.html
		 * 
		 * For
		 * 
		 * This implementation maintains markable references by creating internal
		 * objects representing "boxed" [reference, boolean] pairs.
		 * 
		 */
		private AtomicMarkableReference<Node> amr;

		public Node() {
			left = null;
			right = null;
		}

		public Node(Node left, Node right) {
			this.left = left;
			this.right = right;
		}

		public Node(int data) {
			this.data = data;
			amr = new AtomicMarkableReference<Node>(new Node(), false);
		}

		public Node(int data, Node left, Node right) {
			this.data = data;
			amr = new AtomicMarkableReference<Node>(new Node(left, right), false);
		}

		/* insert child node to specific direction(for original childNode) */
		public boolean insertChildNode(int dir, Node oldNode, Node newNode) {

			Node curCN = amr.getReference();
			Node newCN;
			switch (dir) {
			case -1:
				if (curCN.right != oldNode)
					return false;
				newCN = new Node(curCN.left, newNode);
				break;
			case 1:
				if (curCN.left != oldNode)
					return false;
				newCN = new Node(newNode, curCN.right);
				break;
			case 0:
				newCN = null;
			default:
				return false;
			}
			return amr.compareAndSet(curCN, newCN, false, false);
		}

		/* return child node to specific direction */
		public Node getChildNode(int dir, boolean[] marked) {
			Node newChildNode;
			switch (dir) {
			case 0:
				newChildNode = null;
				break;
			case -1:
				newChildNode = this.amr.get(marked).right;
				break;
			case 1:
				newChildNode = this.amr.get(marked).left;
				break;
			default:
				newChildNode = null;
				break;
			}
			return newChildNode;
		}

		/* marking */
		public boolean mark() {
			return amr.attemptMark(amr.getReference(), true);
		}

		public boolean isMarked() {
			return amr.isMarked();
		}

		public boolean isLeaf() {
			return (amr.getReference().left == null && amr.getReference().right == null);
		}
	}
}