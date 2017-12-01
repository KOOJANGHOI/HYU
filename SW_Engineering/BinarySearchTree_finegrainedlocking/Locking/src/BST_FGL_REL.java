import java.util.concurrent.locks.ReentrantLock;

/* BST with Fine Grained Lock especially ReentrantLock */
public class BST_FGL_REL {
	private Node root; // root node
	private ReentrantLock TreeLock; // ReentrantLock for BST
	
	/* constructor */
	/* initialize root node */
	/* create ReentrantLock instance */
	public BST_FGL_REL() {
		root = null;
		TreeLock = new ReentrantLock();
	}

	/*************************************************
	 *
	 * INSERT
	 *
	 **************************************************/

	/**
	 * insert integer into BST
	 * 
	 * lock BST if root is null, create new root node with toInsert and unlock BST
	 * and return
	 * 
	 * else not null, unlock BST and call insertHelper method with root, toInsert ,
	 * null
	 * 
	 * 
	 * @param toInsert
	 *            Integer to be inserted into BST
	 * @return void
	 */
	public void insert(int toInsert) {
		TreeLock.lock();
		if (root == null) {
			Node newNode = new Node(toInsert);
			root = newNode;
			TreeLock.unlock();
			return;
		}
		TreeLock.unlock();
		insertHelper(root, toInsert, null);
	}

	/**
	 * actual insert method
	 * 
	 * if root is null, insertion is not possible
	 * 
	 * else, not null, lock node if parent is not null, unlock parent node
	 * 
	 * after compare root.data and toInsert, check two child node, if there are no
	 * child node at chosen direction, create new child node with toInsert and
	 * unlock node else , call insertHelper at child node (recursively)
	 * 
	 * if there is same value in node, there are nothing to do. so, unlock node and
	 * return
	 * 
	 * @param root
	 *            The node which date to be inserted
	 * @param toInsert
	 *            Integer to be inserted into node
	 * @param parent
	 *            Parent node
	 * @return void
	 */
	private void insertHelper(Node root, int toInsert, Node parent) {
		Node newNode = new Node(toInsert);
		if (root == null) {
			throw new RuntimeException("cannot insert.");
		}
		root.lock();
		if (parent != null)
			parent.unlock();
		if (root.data > toInsert) {
			if (root.left == null) {
				root.left = newNode;
				root.unlock();
			} else {
				insertHelper(root.left, toInsert, root);
			}
		} else if (root.data < toInsert) {
			if (root.right == null) {
				root.right = newNode;
				root.unlock();
			} else {
				insertHelper(root.right, toInsert, root);
			}
		} else {
			root.unlock();
			return;
		}
	}

	/*************************************************
	 *
	 * SEARCH
	 *
	 **************************************************/

	/*
	 * you don't need to implement hand-over-hand lock for this function. so , I
	 * didn't touch this code either use
	 */
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

	/**
	 * find leftmost node at right child node
	 * 
	 * if root is null, search is not possible so return null
	 * 
	 * else , lock node if parent node is not null , unlock parent node
	 * 
	 * if left child node is null , unlock node and return node else call findMin at
	 * left child node(recursively)
	 * 
	 * @param root
	 *            The node which date to be inserted
	 * @param parent
	 *            Parent node
	 * @return Node left most node
	 */
	public Node findMin(Node root, Node parent) {
		if (root == null) {
			return null;
		} else {
			root.lock();
			if (parent != null)
				parent.unlock();
			if (root.left == null) {
				root.unlock();
				return root;
			} else {
				return findMin(root.left, root);
			}
		}
	}

	/**
	 * search data in BST
	 * 
	 * lock BST
	 * 
	 * if root is null , unlock BST and return false else , unlock BST and return
	 * searchHelper method with root , toSearch , null
	 * 
	 * 
	 * @param toSearch
	 *            value to be searched
	 * @return boolean true if the value is founded
	 */
	public boolean search(int toSearch) {
		TreeLock.lock();
		if (root == null) {
			TreeLock.unlock();
			return false;
		}
		TreeLock.unlock();
		return searchHelper(root, toSearch, null);
	}

	/**
	 * actual search method
	 * 
	 * if root is null, search is not possible
	 * 
	 * else, not null, lock node if parent is not null, unlock parent node
	 * 
	 * after compare root.data and toSearch, check two child node, if there are no
	 * child node at chosen direction, unlock node and return false else , call
	 * searchHelper at child node (recursively)
	 * 
	 * if there is same value in node, it is success. so, unlock node and return
	 * true
	 * 
	 * @param root
	 *            The node which date to be searched
	 * @param toSearch
	 *            Integer to be searched into node
	 * @param parent
	 *            Parent node
	 * @return boolean True if search success
	 */
	private boolean searchHelper(Node root, int toSearch, Node parent) {
		if (root == null) {
			throw new RuntimeException("cannot search.");
		}
		root.lock();
		if (parent != null)
			parent.unlock();
		if (root.data > toSearch) {
			if (root.left == null) {
				root.unlock();
				return false;
			} else {
				return searchHelper(root.left, toSearch, root);
			}
		} else if (root.data > toSearch) {
			if (root.right == null) {
				root.unlock();
				return false;
			} else {
				return searchHelper(root.right, toSearch, root);
			}
		} else {
			root.unlock();
			return true;
		}
	}

	/*****************************************************
	 *
	 * DELETE
	 *
	 ******************************************************/

	/**
	 * delete integer from BST
	 * 
	 * lock BST if root is null, unlock BST and return
	 * 
	 * else not null, unlock BST and call deleteHelper method with root, toDelete ,
	 * null
	 * 
	 * 
	 * @param toDelete
	 *            Integer to be deleted from BST
	 * @return void
	 */
	public void delete(int toDelete) {
		TreeLock.lock();
		if (root == null) {
			TreeLock.unlock();
			return;
		}
		TreeLock.unlock();
		root = deleteHelper(root, toDelete, null);
	}

	/**
	 * actual delete method
	 * 
	 * if root is null, deletion is not possible so return null
	 * 
	 * else, not null, lock node
	 * 
	 * after compare root.data and toDelete,
	 * 
	 * if root.data is same as toDelete
	 * 
	 * if two child node are null, unlock parent node and unlock node and return
	 * null
	 * 
	 * else if only one child node is null, node is changed into child node(not
	 * null) and unlock node and return node
	 * 
	 * else (two child node are not null), find left most node from right child node
	 * root data is changed to left most node's data and delete the left most node
	 * 
	 * else , root.data is different from toDelete if parent node is not null ,
	 * unlock parent node, and call deleteHelper method(recursively) to left or
	 * right child node according to result of compare.
	 * 
	 * 
	 * @param root
	 *            The node which date to be deleted
	 * @param toDelete
	 *            Integer to be deleted from node
	 * @param parent
	 *            Parent node
	 * @return void
	 */
	private Node deleteHelper(Node root, int toDelete, Node parent) {
		Node tmpNode = null;
		if (root != null) {
			root.lock();
			if (root.data > toDelete) {
				if (parent != null)
					parent.unlock();
				root.left = deleteHelper(root.left, toDelete, root);
				return root;
			} else if (root.data < toDelete) {
				if (parent != null)
					parent.unlock();
				root.right = deleteHelper(root.right, toDelete, root);
				return root;
			} else {
				if (root.left == null && root.right == null) {
					tmpNode = root;
					root = null;
					if (parent != null)
						parent.unlock();
					tmpNode.unlock();
					tmpNode = null;
					return root;
				} else if (root.left == null && root.right != null) {
					tmpNode = root;
					root = root.right;
					if (parent != null)
						parent.unlock();
					tmpNode.unlock();
					tmpNode = null;
					return root;
				} else if (root.left != null && root.right == null) {
					tmpNode = root;
					root = root.left;
					if (parent != null)
						parent.unlock();
					tmpNode.unlock();
					tmpNode = null;
					return root;
				} else {
					if (parent != null)
						parent.unlock();
					Node rlNode = findMin(root.right, root);
					root.data = rlNode.data;
					root.right = deleteHelper(root.right, rlNode.data, null);
					return root;
				}
			}
		} else {
			if (parent != null)
				parent.unlock();
			return null;
		}
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

	/* Node Class */
	private class Node {
		private int data;
		private Node left, right;
		private ReentrantLock lock; // ReentrantLock

		public Node(int data, Node l, Node r) {
			left = l;
			right = r;
			this.data = data;
			lock = new ReentrantLock();
		}

		public Node(int data) {
			this(data, null, null);
		}

		public String toString() {
			return "" + data;
		}

		public void lock() {
			// System.out.println("[O]["+data+"]");
			lock.lock();
		}

		public void unlock() {
			// System.out.println("[X]["+data+"]");
			lock.unlock();
		}
	}
}