
/** **************************************************************************
 *  The sequential Binary Search Tree (for storing integer values)
 *
 *****************************************************************************/

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* BST with Fine Grained Lock especially ReentrantReadWriteLock */

/* all components are same with BST_FGL_REL */
/* except ,
 * lock in insert,delete is changed into WriteLock
 * lock in search is changed into ReadLock
 * 
 */
public class BST_FGL_RWL {
	private Node root;
	private ReentrantReadWriteLock TreeLock;

	public BST_FGL_RWL() {
		root = null;
		TreeLock = new ReentrantReadWriteLock();
	}

	/*****************************************************
	 *
	 * INSERT
	 *
	 ******************************************************/
	public void insert(int toInsert) {
		TreeLock.writeLock().lock();;
		if (root == null) {
			Node newNode = new Node(toInsert);
			root = newNode;
			TreeLock.writeLock().unlock();
			return;
		}
		TreeLock.writeLock().unlock();
		insertHelper(root, toInsert, null);
	}

	private void insertHelper(Node root, int toInsert, Node parent) {
		Node newNode = new Node(toInsert);
		if (root == null) {
			throw new RuntimeException("cannot insert.");
		}
		root.lock.writeLock().lock();
		if (parent != null)
			parent.lock.writeLock().unlock();
		if (root.data > toInsert) {
			if (root.left == null) {
				root.left = newNode;
				root.lock.writeLock().unlock();
			} else {
				insertHelper(root.left, toInsert, root);
			}
		} else if (root.data < toInsert) {
			if (root.right == null) {
				root.right = newNode;
				root.lock.writeLock().unlock();
			} else {
				insertHelper(root.right, toInsert, root);
			}
		} else {
			root.lock.writeLock().unlock();
			return;
		}
	}

	/*****************************************************
	 *
	 * SEARCH
	 *
	 ******************************************************/

	// you don't need to implement hand-over-hand lock for this function.
	public int findMin() {
		if (root == null) {
			throw new RuntimeException("cannot findMin.");
		}
		Node n = root;
		while (n.left != null) {
			n = n.left;
		}
		return n.data;
	}

	public Node findMin(Node root, Node parent) {
		if (root == null) {
			return null;
		} else {
			root.lock.readLock().lock();
			if (parent != null)
				parent.lock.readLock().unlock();
			if (root.left == null) {
				root.lock.readLock().unlock();
				return root;
			} else {
				return findMin(root.left, root);
			}
		}
	}

	public boolean search(int toSearch) {
		TreeLock.readLock().lock();;
		if(root==null) {
			TreeLock.readLock().unlock();
			return false;
		} 
		TreeLock.readLock().unlock();
		return searchHelper(root, toSearch,null);
	}

	private boolean searchHelper(Node root, int toSearch, Node parent) {
		
		if(root==null) {
			throw new RuntimeException("cannot search.");
		}
		root.lock.readLock().lock();
		if(parent != null)
			parent.lock.readLock().unlock();
		if(root.data > toSearch) {
			if(root.left == null) {
				root.lock.readLock().unlock();
				return false;
			} else {
				return searchHelper(root.left,toSearch,root);
			}
		} else if(root.data > toSearch) {
			if(root.right==null) {
				root.lock.readLock().unlock();
				return false;
			} else {
				return searchHelper(root.right,toSearch,root);
			}
		} else {
			root.lock.readLock().unlock();
			return true;	
		}
	}

	/*****************************************************
	 *
	 * DELETE
	 *
	 ******************************************************/

	public void delete(int toDelete) {
		TreeLock.writeLock().lock();
		if (root == null) {
			TreeLock.writeLock().unlock();
			return;
		}
		TreeLock.writeLock().unlock();
		root = deleteHelper(root, toDelete, null);
	}

	private Node deleteHelper(Node root, int toDelete, Node parent) {
		Node tmpNode = null;
		if(root != null) {
			root.lock.writeLock().lock();
			if(root.data == toDelete) {
				if (root.left == null && root.right == null) {
					tmpNode = root;
					root = null;
					if (parent != null)
						parent.lock.writeLock().unlock();
					tmpNode.lock.writeLock().unlock();
					tmpNode = null;
					return root;
				} else if (root.left == null && root.right != null) {
					tmpNode = root;
					root = root.right;
					if (parent != null)
						parent.lock.writeLock().unlock();
					tmpNode.lock.writeLock().unlock();
					tmpNode = null;
					return root;
				} else if (root.left != null && root.right == null) {
					tmpNode = root;
					root = root.left;
					if (parent != null)
						parent.lock.writeLock().unlock();
					tmpNode.lock.writeLock().unlock();
					tmpNode = null;
					return root;
				} else {
					Node rlNode = findMin(root.right, root);
					root.data = rlNode.data;
					root.right = deleteHelper(root.right, rlNode.data, null);
				}
			} else if(root.data > toDelete) {
				if (parent != null)
					parent.lock.writeLock().unlock();
				root.left = deleteHelper(root.left, toDelete, root);
			} else {
				if (parent != null)
					parent.lock.writeLock().unlock();
				root.right = deleteHelper(root.right, toDelete, root);	
			}
		} 
		return root;
	}

	private int retrieveData(Node p) {
		while (p.right != null) {
			p = p.right;
		}
		return p.data;
	}

	/*************************************************
	 *
	 * TRAVERSAL
	 *
	 **************************************************/

	public void preOrderTraversal() {
		preOrderHelper(root);
	}

	private void preOrderHelper(Node r) {
		if (r != null) {
			System.out.print(r + " ");
			preOrderHelper(r.left);
			preOrderHelper(r.right);
		}
	}

	public void inOrderTraversal() {
		inOrderHelper(root);
	}

	private void inOrderHelper(Node r) {
		if (r != null) {
			inOrderHelper(r.left);
			System.out.print(r + " ");
			inOrderHelper(r.right);
		}
	}

	private class Node {
		private int data;
		private Node left, right;
		private ReentrantReadWriteLock lock;

		public Node(int data, Node l, Node r) {
			left = l;
			right = r;
			this.data = data;
			lock = new ReentrantReadWriteLock();
		}

		public Node(int data) {
			this(data, null, null);
		}

		public String toString() {
			return "" + data;
		}
	}
}
