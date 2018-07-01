

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/* DBSCAN class
 * 
 * String input						: path to input file
 * final int undefined				: final number to point of label(undefined)
 * final int undefined				: final number to point of label(outlier)
 * int n								: # of cluster to be formed
 * int Eps , MinPts					: Eps , MinPts
 * int cnum							: # of all cluster 
 * List<Point> DB					: List of Point
 * List<Pair> clist					: List of Pair	
 * Map<Integer,List<Point>> cmap		: Map of <cluster idx, List of Point>
 * 
 */

public class DBSCAN {
	String input;
	final int undefined = -10000, noise = -9999;
	int n, Eps, MinPts, cnum;
	List<Point> DB;
	List<Pair> clist;
	Map<Integer, List<Point>> cmap;

	/*
	 * work : main process in DBSCAN algorithm
	 * 
	 * store all parameter as input-filename,n,Eps,MinPts
	 * 
	 * read input-file ans store all data(object id , x and y coordinates
	 * process clustering(assign all label of each Point) by DBSCAN algorithm
	 * process clustering(form all cluster)
	 * write result of clustering
	 * 
	 * @param  	_input			input filename
	 * @param	_n				# of cluster to be formed
	 * @param	_Eps,_MinPts		Eps,MinPts
	 * 
	 */
	void work(String _input, String _n, String _Eps, String _MinPts) {
		input = _input;
		n = Integer.parseInt(_n);
		Eps = Integer.parseInt(_Eps);
		MinPts = Integer.parseInt(_MinPts);
		DB = new ArrayList<Point>();
		readInputFile();
		dbscan();
		clustering();
		writeOutputFile();
		//test();
	}

	/*
	 * DBSCAN algorithm : assign all label of each Point
	 * 
	 * for all Point which has no label,
	 * 
	 * retrieve neighbor of the Point as set.
	 * if neighbor's size < MinPts, then assign label as noise.
	 * if not, the point being core-point. so increment cluster idx(cnum).
	 * and enqueue all neighbor except core-point.
	 * 
	 * for all Point in queue,
	 * 
	 * if the Point's label is noise, then assign label as same cluster idx before.
	 * if the Point's label is undefined, then continue.
	 * enqueue all neighbor of the Point which are dense.
	 * 
	 */
	
	void dbscan() {
		Set<Point> neighbor;
		Queue<Point> seedSet;
		cnum = 0;
		for (Point p : DB) {
			neighbor = new HashSet<Point>();
			seedSet = new LinkedList<Point>();
			if (p.label != undefined)
				continue;
			neighbor = RangeQuery(p);
			if (neighbor.size() < MinPts) {
				p.label = noise;
				continue;
			}
			cnum++;
			p.label = cnum;
			for (Point q : neighbor) {
				if (q.id != p.id)
					seedSet.add(q);
			}
			Set<Point> newneighbor;
			while (!seedSet.isEmpty()) {
				newneighbor = new HashSet<Point>();
				Point q = seedSet.poll();
				if (q.label == noise)
					q.label = cnum;
				if (q.label != undefined)
					continue;
				q.label = cnum;
				newneighbor = RangeQuery(q);
				if (newneighbor.size() >= MinPts) {
					for (Point r : newneighbor)
						seedSet.add(r);
				}
			}
		}
	}

	void test() {
		int num_total = DB.size();
		int num_noise = 0;
		for (Point p : DB) {
			if (p.label == noise)
				num_noise++;
		}
		int num_clustering = 0;
		for (int i = 1; i <= cmap.size(); i++) {
			num_clustering += cmap.get(i).size();
		}
		int num_write = 0;
		for (int i = 0; i < n; i++) {
			num_write += cmap.get(clist.get(i).idx).size();
		}
		System.out.println("==============================");
		System.out.println("total:" + num_total);
		System.out.println("outlier:" + num_noise);
		System.out.println("must clustering:" + (num_total - num_noise));
		System.out.println("clustering:" + num_clustering);
		System.out.println("written:" + num_write);
		System.out.println("==============================");

		String[] ideal = new String[n];
		String inputname = input.split(".txt")[0];
		for (int i = 0; i < n; i++) {
			ideal[i] = inputname + "_cluster_" + i + "_ideal.txt";
		}
		int[] arr = new int[n];
		BufferedReader[] br = new BufferedReader[n];
		for (int i = 0; i < n; i++) {
			try {
				br[i] = new BufferedReader(new FileReader(ideal[i]));
				while ((br[i].readLine()) != null) {
					arr[i]++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Arrays.sort(arr);
		int num_miss = 0;
		for (int i = 0; i < n; i++) {
			System.out.println("[cluster(" + i + "):" + arr[n - 1 - i] + "/" + cmap.get(clist.get(i).idx).size());
			num_miss += Math.abs(arr[n - 1 - i] - cmap.get(clist.get(i).idx).size());
		}
		System.out.println("==============================");
		System.out.println("miss:" + num_miss);
		System.out.println("==============================");

	}

	/*
	 * clustering :  form all cluster
	 * 
	 * make clist as list<(cluster-idx,size of cluster)>.
	 * make cmap  as map<cluster-idx,list of Points>.
	 * 
	 */
	void clustering() {
		cmap = new HashMap<>();
		clist = new ArrayList<>();
		clist.add(new Pair(0, 0));
		for (int i = 1; i <= cnum; i++) {
			if (cnum != noise) {
				clist.add(new Pair(i, 0));
			}
		}
		for (Point p : DB) {
			int label = p.label;
			if (label == undefined)
				System.out.println("Error:object " + p.id + " is undefined!!!");
			if (label == noise)
				continue;
			if (cmap.get(label) == null) {
				List<Point> tlist = new ArrayList<Point>();
				tlist.add(p);
				cmap.put(label, tlist);
			} else {
				cmap.get(label).add(p);
			}
			clist.get(label).size++;
		}
		Collections.sort(clist);
		for (int idx : cmap.keySet()) {
			Collections.sort(cmap.get(idx));
		}
	}

	/*
	 * write clustering result in output file
	 */
	void writeOutputFile() {
		String[] output = new String[n];
		String inputname = input.split(".txt")[0];
		for (int i = 0; i < n; i++) {
			output[i] = inputname + "_cluster_" + i + ".txt";
		}
		PrintWriter[] pw = new PrintWriter[n];
		for (int i = 0; i < n; i++) {
			try {
				pw[i] = new PrintWriter(output[i]);
				for (Point p : cmap.get(clist.get(i).idx)) {
					pw[i].println(p.id);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw[i].close();
		}
	}

	/*
	 * RangeQuery : find neighbor
	 * 
	 * for all Point q,
	 * if dist(p,q) <= Eps, add to set of Point(neighbor)
	 * 
	 * return set of neighbors
	 * 
	 * 
	 * @param  	p				Point as a center.
	 * @return	Set<Point>		set of neighbors
	 */
	Set<Point> RangeQuery(Point p) {
		Set<Point> ret = new HashSet<Point>();
		for (Point q : DB) {
			if (dist(p, q) <= Eps)
				ret.add(q);
		}
		return ret;
	}

	/*
	 * read inputfile
	 */
	void readInputFile() {
		String str;
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			while ((str = br.readLine()) != null) {
				String[] delim = str.split("\t");
				int id = Integer.parseInt(delim[0]);
				double x = Double.parseDouble(delim[1]);
				double y = Double.parseDouble(delim[2]);
				DB.add(new Point(id, x, y));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * distance between two Points as Euclidean distance in 2d.
	 * 
	 */

	double dist(Point p, Point q) {
		double distx = Math.round(Math.abs(p.x - q.x) * 1000000d) / 1000000d;
		double disty = Math.round(Math.abs(p.y - q.y) * 1000000d) / 1000000d;
		double res = Math.round(Math.sqrt(distx * distx + disty * disty) * 1000000d) / 1000000d;
		return res;
	}

	/*
	 * main method
	 * 
	 * store all parameter as input-filename,n,Eps,MinPts
	 * and call work method after create DBSCAN instances
	 * 
	 */
	public static void main(String[] args) {
		String input, n, Eps, MinPts;
		if (args.length != 4) {
			System.out.println("args error!!!");
		} else {
			input = args[0];
			n = args[1];
			Eps = args[2];
			MinPts = args[3];
			new DBSCAN().work(input, n, Eps, MinPts);
		}
	}

	/*
	 * Point class
	 * 
	 * int id		: Point idx
	 * int label		: label of a Point(cluster idx / noise / undefined)
	 * double x,y	: x,y_coordinate value
	 * 
	 */
	
	public class Point implements Comparable<Point> {
		int id, label;
		double x, y;

		/* constructor */
		Point(int _id, double _x, double _y) {
			this.id = _id;
			this.x = _x;
			this.y = _y;
			this.label = undefined;
		}

		/* ascending order of Point.id */
		@Override
		public int compareTo(Point o) {
			// TODO Auto-generated method stub
			if (id > o.id) {
				return 1;
			} else if (id < o.id) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	/*
	 * Pair class
	 * 
	 * int id			: cluster idx
	 * int size			: size of cluster
	 * 
	 */

	public class Pair implements Comparable<Pair> {
		int idx, size;

		/* construtor */
		Pair(int _idx, int _size) {
			this.idx = _idx;
			this.size = _size;
		}

		/* descending order of cluster-size */
		@Override
		public int compareTo(Pair o) {
			// TODO Auto-generated method stub
			if (size > o.size) {
				return -1;
			} else if (size < o.size) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
