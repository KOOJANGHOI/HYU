import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class assignment1_2013011800_IDS {
	int[][] maze, result;
	int m, n, max = 250000;
	int count = 0;
	Node root;
	boolean endcondition = false;
	int[] dx = { 0, 0, 1, -1 };
	int[] dy = { 1, -1, 0, 0 };
	String path = null;

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
						if (maze[lcnt][i] == 3)
							root = new Node(lcnt, i);
					}
					lcnt++;
				}
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void IDDFS(Node root) {
		for (int depth = 0; depth < max; depth++) {
			DLS(copyarr(maze), root, depth);
			if (endcondition)
				return;
		}
		return;
	}

	void DLS(int[][] curmaze, Node node, int depth) {
		count++;
		if (depth == 0) {
			return;
		} else if (depth > 0) {
			for (int i = 0; i < 4; i++) {
				int childx = node.getX() + dx[i];
				int childy = node.getY() + dy[i];
				if (isvalid(childx, childy) && curmaze[childx][childy] != 1) {
					if (curmaze[childx][childy] == 4) {
						endcondition = true;
						result = copyarr(curmaze);
						break;
					} else if (curmaze[childx][childy] == 2) {
						int[][] tarr = copyarr(curmaze);
						tarr[childx][childy] = 5;
						DLS(tarr, new Node(childx, childy), depth - 1);
					}
				}
			}
		}
		return;
	}

	boolean isvalid(int x, int y) {
		return (x >= 0 && x < m && y >= 0 && y < n);
	}

	int[][] copyarr(int[][] arr) {
		int[][] temp = new int[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				temp[i][j] = arr[i][j];
			}
		}
		return temp;
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
					str += result[i][j] + " ";
				}
				pw.println(str);
			}
			pw.println("---");
			pw.println("length=" + countpath(result));
			pw.println("time=" + count);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void work(String _path) {
		path = _path;
		readFile();
		IDDFS(root);
		if (endcondition) {
			writeFile();
		}
	}

	public static void main(String[] args) {
		String path = args[0];
		new assignment1_2013011800_IDS().work(path);
	}

	public class Node {
		int x, y;

		public Node(int _x, int _y) {
			this.x = _x;
			this.y = _y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}
}