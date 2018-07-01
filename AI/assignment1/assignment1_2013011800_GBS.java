import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class assignment1_2013011800_GBS {
	int[][] maze, visited;
	int m, n, step = 0, max = 250000;
	Node root;
	boolean endcondition = false;
	String path = null;
	int[] dx = { 0, 0, 1, -1 };
	int[] dy = { 1, -1, 0, 0 };
	List<Node> destinations = new ArrayList<Node>();
	Node fNode;
	Comparator<Node> comparator = new NodeComparator();
	PriorityQueue<Node> queue = new PriorityQueue<Node>(max, comparator);

	void readFile() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(path + "/input.txt"));
			String str;
			int lcnt = -1;
			while ((str = bf.readLine()) != null) {
				if (lcnt == -1) {
					m = Integer.parseInt(str.split(" ")[0]);
					n = Integer.parseInt(str.split(" ")[1]);
					maze = new int[m][n];
					lcnt++;
				} else {
					for (int i = 0; i < n; i++) {
						maze[lcnt][i] = Integer.parseInt(str.split(" ")[i]);
						if (maze[lcnt][i] == 3) {
							root = new Node(null, lcnt, i);
						} else if (maze[lcnt][i] == 4) {
							destinations.add(new Node(lcnt, i));
						}
					}
					lcnt++;
				}
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void GBFS(Node root) {
		visited = new int[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				visited[i][j] = 0;
			}
		}
		root.setH();
		queue.add(root);
		while (queue.size() != 0 && !endcondition) {
			Node poped = queue.poll();
			step++;
			visited[poped.getX()][poped.getY()] = 1;
			for (int i = 0; i < 4; i++) {
				int childx = poped.getX() + dx[i];
				int childy = poped.getY() + dy[i];
				if (isvalid(childx, childy) && visited[childx][childy] == 0) {
					if (maze[childx][childy] == 2) {
						Node child = new Node(poped, childx, childy);
						child.setH();
						queue.add(child);
					} else if (maze[childx][childy] == 4) {
						endcondition = true;
						fNode = new Node(poped, childx, childy);
						break;
					}
				}
			}
		}
	}

	boolean isvalid(int x, int y) {
		return (x >= 0 && x < m && y >= 0 && y < n);
	}

	void drawpath(Node child) {
		Node parent = child.parent;
		if (maze[parent.getX()][parent.getY()] == 3) {
			return;
		} else {
			maze[parent.getX()][parent.getY()] = 5;
			drawpath(parent);
		}
	}

	int countpath(int[][] arr) {
		int ret = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (arr[i][j] == 5)
					ret++;
			}
		}
		return ret;
	}

	void writeFile() {
		PrintWriter pw;
		try {
			pw = new PrintWriter(path + "/output.txt");
			for (int i = 0; i < m; i++) {
				String str = "";
				for (int j = 0; j < n; j++) {
					str += maze[i][j] + " ";
				}
				pw.println(str);
			}
			pw.println("---");
			pw.println("length=" + countpath(maze));
			pw.println("time=" + step);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void work(String _path) {
		path = _path;
		readFile();
		GBFS(root);
		if (endcondition) {
			drawpath(fNode);
			writeFile();
		}
	}

	public static void main(String[] args) {
		String path = args[0];
		new assignment1_2013011800_GBS().work(path);
	}

	public class Node {
		int x, y, h = 250000;
		Node parent;

		public Node(int _x, int _y) {
			this.x = _x;
			this.y = _y;
		}

		public Node(Node par, int _x, int _y) {
			this.parent = par;
			this.x = _x;
			this.y = _y;
		}

		public int getX() { return x; }

		public int getY() { return y; }

		public int getH() { return h; }

		public void setH() {
			for (Node node : destinations) {
				this.h = Math.min(h, Math.abs(node.getX() - x) + Math.abs(node.getY() - y));
			}
		}
	}

	public class NodeComparator implements Comparator<Node> {
		@Override
		public int compare(Node o1, Node o2) {
			if (o1.getH() < o2.getH()) {
				return -1;
			} else if (o1.getH() > o2.getH()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
