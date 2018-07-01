import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class assignment2_2013011800 {
	final String simtype = "cosine"; // type of similiarity measure
	final String wordembedding = "WordEmbedding.txt"; // input-file name
	final String wordtopic = "WordTopic.txt"; // class-file name
	final String wordclustering = "WordClustering.txt"; // output-file name
	Map<String, List<Double>> vectormap; // Map(String , WordEmbedding vector(dimension=300))
	Map<Integer, Set<String>> clustermap; // Map(Cluster Index , Set of Strings in each cluster)
	Map<String, Integer> wtoi; // Map(String , String Index)
	Map<Integer, String> itow; // Map(String Index , String)
	Map<Integer, PriorityQueue<Unit>> pqmap; // Map(String Index , PriorityQueue of Unit(String Index,similarity))
	List<Pair> resultset; // List of Pair(String Index,String Index) as complete-link clustering goes
	Double[][] smatrix; // similarity matrix(about all String Index-String Index pair)
	int[] chklist; // check whether a String is choosen in complete-link clustering
	int[] stringIdxToClassNum, stringIdxToClusterNum; // String Index to ClassNum/ClusterNum
	int[][] clusterToClass; // # of Class in each Cluster
	int wcnt, classnum, clusternum; // # of word , # of class , # of cluster
	double[] threshold = { 0.2, 0.4, 0.6, 0.8 }; // threshold for partitioning clusters
	String[] type = { "euclidean", "cosine" }; // types of similarity measure
	double emax = 0, emin = 2 * Math.sqrt(300); // max,min for euclidean-distance
	double cmax = 0, cmin = 1.0; // max,min for cosine-similarity
	Double[][] cmatrix;

	void writeOutputFile() {
		try {
			PrintWriter pw = new PrintWriter(wordclustering);
			for (int i = 1; i <= wcnt; i++) {
				String str = itow.get(i);
				pw.println(str);
				for (int j = 0; j < vectormap.get(str).size() - 1; j++) {
					pw.print(vectormap.get(str).get(j) + ",");
				}
				pw.print(vectormap.get(str).get(vectormap.get(str).size() - 1) + "\n");
				pw.println(stringIdxToClusterNum[i]);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	double dunnindex() {
		double ret = 0;
		double gmin;
		if (simtype.equalsIgnoreCase(type[0])) {
			gmin = 2 * Math.sqrt(300);
		} else {
			gmin = 1.0;
		}
		for (int i = 1; i <= clusternum; i++) {
			double I = 0;
			double min = gmin;
			for (int j = 1; j <= clusternum; j++) {
				if (i == j)
					continue;
				for (String k : clustermap.get(i)) {
					for (String l : clustermap.get(j)) {
						int kidx = wtoi.get(k);
						int lidx = wtoi.get(l);
						min = Math.min(min, smatrix[kidx][lidx]);
					}
				}
			}
			double a = min;
			double b = 0;
			double max = 0;
			if (clustermap.get(i).size() == 1) {
				b = 1;
			} else {
				for (String k : clustermap.get(i)) {
					for (String l : clustermap.get(i)) {
						int kidx = wtoi.get(k);
						int lidx = wtoi.get(l);
						if (kidx != lidx) {
							max = Math.max(max, smatrix[kidx][lidx]);
						}
					}
				}
				b = max;
			}
			a = Math.round(a * 100000) / 100000.0;
			b = Math.round(b * 100000) / 100000.0;
			I = a / b;
			I = Math.round(I * 100000) / 100000.0;
			ret += I / (double) clustermap.get(i).size();
		}
		ret = Math.round(ret * 100000) / 100000.0;
		return ret;
	}

	double silhouetteanalysis() {
		double ret = 0;
		for (int i = 1; i <= wcnt; i++) {
			double s = 0;
			int cidx = stringIdxToClusterNum[i];
			double a = 0;
			if (clustermap.get(cidx).size() == 1) {
				a = 0;
			} else {
				for (String str : clustermap.get(cidx)) {
					int lidx = wtoi.get(str);
					if (lidx != i) {
						a += smatrix[i][lidx] / (clustermap.get(cidx).size() - 1);
					}
				}
			}
			double min;
			if (simtype.equalsIgnoreCase(type[0])) {
				min = 2 * Math.sqrt(300);
			} else {
				min = 1.0;
			}
			double b = 0;
			for (int j = 1; j <= clusternum; j++) {
				b = 0;
				if (cidx == j)
					continue;
				for (String str : clustermap.get(j)) {
					int lidx = wtoi.get(str);
					b += smatrix[i][lidx] / clustermap.get(j).size();
				}
				min = Math.min(min, b);
			}
			a = Math.round(a * 100000) / 100000.0;
			b = Math.round(b * 100000) / 100000.0;
			s = (b - a) / Math.max(a, b);
			s = Math.round(s * 100000) / 100000.0;
			ret += s / (double) wcnt;
		}
		ret = Math.round(ret * 100000) / 100000.0;
		return ret;
	}

	double entropyanalysis() {
		double ret = 0;
		for (int i = 1; i <= clusternum; i++) {
			int size = 0;
			for (int j = 1; j <= classnum; j++) {
				size += clusterToClass[i][j];
			}
			ret += (double) size / wcnt * entropy(clusterToClass[i]);
		}
		ret = Math.round(ret * 100000) / 100000.0;
		return ret;
	}

	void partitioning(int idx) {
		clusternum = 0;
		stringIdxToClusterNum = new int[wcnt + 1];
		for (int i = 0; i < resultset.size(); i++) {
			Pair pair = resultset.get(i);
			if (Math.max(pair.sim, threshold[idx]) == pair.sim) {
				if (stringIdxToClusterNum[pair.kidx] == 0 && stringIdxToClusterNum[pair.lidx] == 0) {
					clusternum++;
					stringIdxToClusterNum[pair.kidx] = clusternum;
					stringIdxToClusterNum[pair.lidx] = clusternum;
				} else if (stringIdxToClusterNum[pair.kidx] == 0 && stringIdxToClusterNum[pair.lidx] != 0) {
					stringIdxToClusterNum[pair.kidx] = stringIdxToClusterNum[pair.lidx];
				} else if (stringIdxToClusterNum[pair.kidx] != 0 && stringIdxToClusterNum[pair.lidx] == 0) {
					stringIdxToClusterNum[pair.lidx] = stringIdxToClusterNum[pair.kidx];
				} else {
				}
			} else {
				if (stringIdxToClusterNum[pair.kidx] == 0 && stringIdxToClusterNum[pair.lidx] == 0) {
					clusternum++;
					stringIdxToClusterNum[pair.kidx] = clusternum;
					clusternum++;
					stringIdxToClusterNum[pair.lidx] = clusternum;
				} else if (stringIdxToClusterNum[pair.kidx] == 0 && stringIdxToClusterNum[pair.lidx] != 0) {
					clusternum++;
					stringIdxToClusterNum[pair.kidx] = clusternum;
				} else if (stringIdxToClusterNum[pair.kidx] != 0 && stringIdxToClusterNum[pair.lidx] == 0) {
					clusternum++;
					stringIdxToClusterNum[pair.lidx] = clusternum;
				} else {
				}
			}
		}
		clusterToClass = new int[clusternum + 1][classnum + 1];
		for (int i = 1; i <= wcnt; i++) {
			int clusterIdx = stringIdxToClusterNum[i];
			int classIdx = stringIdxToClassNum[i];
			clusterToClass[clusterIdx][classIdx]++;
		}
		clustermap = new HashMap<>();
		for (int i = 1; i <= clusternum; i++) {
			clustermap.put(i, new HashSet<>());
		}
		for (int i = 1; i <= wcnt; i++) {
			clustermap.get(stringIdxToClusterNum[i]).add(itow.get(i));
		}
	}

	void readClassfile() {
		stringIdxToClassNum = new int[wcnt + 1];
		try {
			BufferedReader br = new BufferedReader(new FileReader(wordtopic));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (str.length() == 0)
					continue;
				if (str.charAt(0) == '[') {
					classnum++;
				} else {
					stringIdxToClassNum[wtoi.get(str.toLowerCase())] = classnum;
				}
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

	void init() {
		vectormap = new HashMap<>();
		wtoi = new HashMap<>();
		itow = new HashMap<>();
		pqmap = new HashMap<>();
		wcnt = 0;
		classnum = 0;
	}

	void readInputfile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(wordembedding));
			String str = null, word = null;
			while ((str = br.readLine()) != null) {
				if (str.length() < 100) {
					wcnt++;
					word = str;
					wtoi.put(word, wcnt);
					itow.put(wcnt, word);
				} else {
					String[] ss = str.split(",");
					List<Double> vector = new ArrayList<>();
					for (int i = 0; i < ss.length; i++) {
						vector.add(Double.parseDouble(ss[i]));
					}
					vectormap.put(word, vector);
				}
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

	void computeMatrix() {
		cmatrix = new Double[wcnt + 1][wcnt + 1];
		smatrix = new Double[wcnt + 1][wcnt + 1];
		for (String k : vectormap.keySet()) {
			int kidx = wtoi.get(k);
			for (String l : vectormap.keySet()) {
				int lidx = wtoi.get(l);
				if (kidx == lidx) {
					smatrix[kidx][lidx] = (double) 0;
				} else {
					double sim = 0;
					if (simtype.equalsIgnoreCase(type[0])) {
						sim = euclideandistance(vectormap.get(k), vectormap.get(l));
					} else {
						sim = cosinesimilarity(vectormap.get(k), vectormap.get(l));
					}
					smatrix[kidx][lidx] = sim;
				}
			}
		}
		for (String k : vectormap.keySet()) {
			PriorityQueue<Unit> pq = new PriorityQueue<>();
			int kidx = wtoi.get(k);
			for (String l : vectormap.keySet()) {
				int lidx = wtoi.get(l);
				if (kidx != lidx) {
					double sim = smatrix[kidx][lidx];
					double temp = 0;
					if (simtype.equalsIgnoreCase(type[0])) {
						temp = (1 / (sim) - 1 / (emax)) / (1 / (emin) - 1 / (emax));
					} else {
						temp = (sim - cmin) / (cmax - cmin);
					}
					smatrix[kidx][lidx] = temp;
					cmatrix[kidx][lidx] = temp;
					pq.add(new Unit(lidx, smatrix[kidx][lidx]));
				}
			}
			pqmap.put(kidx, pq);
		}
	}

	void initialization() {
		resultset = new ArrayList<>();
		chklist = new int[wcnt + 1];
		for (int i = 1; i <= wcnt; i++) {
			chklist[i] = 1;
		}
	}

	void computeClustering() {
		for (int i = 1; i < wcnt; i++) {
			int k1 = 0, k2 = 0;
			double max = 0;
			for (int j = 1; j <= wcnt; j++) {
				if (chklist[j] == 1) {
					Unit u = pqmap.get(j).peek();
					if (Math.max(u.sim, max) == u.sim) {
						max = u.sim;
						k1 = j;
						k2 = u.index;
					}
				}
			}

			resultset.add(new Pair(k1, k2, cmatrix[k1][k2]));
			chklist[k2] = 0;
			pqmap.get(k1).clear();
			for (String l : vectormap.keySet()) {
				int lidx = wtoi.get(l);
				if (lidx != k1 && chklist[lidx] == 1) {
					smatrix[k1][lidx] = Math.min(smatrix[lidx][k1], smatrix[lidx][k2]);
					smatrix[lidx][k1] = Math.min(smatrix[lidx][k1], smatrix[lidx][k2]);
					PriorityQueue<Unit> temppqmap = new PriorityQueue<Unit>();
					for (Unit u : pqmap.get(lidx)) {
						if (u.index == k1 || u.index == k2)
							continue;
						temppqmap.add(u);
					}
					pqmap.get(lidx).clear();
					for (Unit u : temppqmap) {
						pqmap.get(lidx).add(u);
					}
					pqmap.get(k1).add(new Unit(lidx, smatrix[k1][lidx]));
					pqmap.get(lidx).add(new Unit(k1, smatrix[lidx][k1]));
				}
			}
		}
	}

	double euclideandistance(List<Double> l1, List<Double> l2) {
		double ret = 0;
		double diff = 0, sum = 0;
		for (int i = 0; i < l1.size(); i++) {
			diff = Math.abs(l1.get(i) - l2.get(i));
			sum += diff * diff;
		}
		ret = Math.sqrt(sum);
		emax = Math.max(emax, ret);
		emin = Math.min(emin, ret);
		return ret;
	}

	double cosinesimilarity(List<Double> l1, List<Double> l2) {
		double ret = 0;
		double size1 = 0, size2 = 0, dot = 0;
		for (int i = 0; i < l1.size(); i++) {
			dot += l1.get(i) * l2.get(i);
			size1 += l1.get(i) * l1.get(i);
			size2 += l2.get(i) * l2.get(i);
		}
		ret = dot / (Math.sqrt(size1) * Math.sqrt(size2));
		cmax = Math.max(cmax, ret);
		cmin = Math.min(cmin, ret);
		return ret;
	}

	double entropy(int[] arr) {
		double ret = 0;
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		for (int i = 0; i < arr.length; i++) {
			double p = arr[i] / sum;
			if (p != 0)
				ret -= p * Math.log(p) / Math.log(2);
		}
		return Math.round(ret * 1000) / 1000.0;
	}

	void partioningAndAnalysis() {
		double entropy = 0, silhouett = 0, dunnindex = 0;
		partitioning(1);
		entropy = entropyanalysis();
		silhouett = silhouetteanalysis();
		dunnindex = dunnindex();
		System.out.println("==========================================");
		System.out.println("threshold : " + threshold[1]);
		System.out.println("# of cluster : " + clusternum);
		System.out.println("entropy : " + entropy);
		System.out.println("silhouett : " + silhouett);
		System.out.println("dunnindex : " + dunnindex);
		System.out.println("==========================================");
	}

	void work() {
		init();
		readInputfile();
		computeMatrix();
		initialization();
		computeClustering();
		readClassfile();
		partioningAndAnalysis();
		writeOutputFile();
	}

	public static void main(String[] args) {
		new assignment2_2013011800().work();
	}

	public class Unit implements Comparable<Unit> {
		int index;
		double sim;

		public Unit(int _index, double _sim) {
			index = _index;
			sim = _sim;
		}

		@Override
		public int compareTo(Unit o) {
			// TODO Auto-generated method stub
			if (this.sim < o.sim) {
				return 1;
			} else if (this.sim > o.sim) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public class Pair {
		int kidx, lidx;
		double sim;

		public Pair(int _kidx, int _lidx, double _sim) {
			this.kidx = _kidx;
			this.lidx = _lidx;
			this.sim = _sim;
		}
	}
}

	
