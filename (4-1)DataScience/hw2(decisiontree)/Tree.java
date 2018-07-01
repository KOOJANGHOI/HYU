import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {
	List<Map<List<String>, String>> data, tdata, rdata;
	Map<String, List<String>> gattributes;
	List<String> label;
	List<String> gattribute;
	String trainingset, testset, resultset, classname = null;
	int numattr = 0;
	Node root;

	void readTrainingData(String _trainingset) {
		try {
			data = new ArrayList<>();
			gattributes = new HashMap<>();
			label = new ArrayList<>();
			BufferedReader bf = new BufferedReader(new FileReader(_trainingset));
			String str;
			int len = 0;
			gattribute = new ArrayList<>();
			while ((str = bf.readLine()) != null) {
				String[] ss = str.split("\t");
				if (len == 0) {
					numattr = ss.length - 1;
					for (int i = 0; i < numattr; i++) {
						gattribute.add(ss[i]);
					}
					len++;
					classname = ss[numattr];
				} else {
					List<String> temp = new ArrayList<>();
					Map<List<String>, String> tuples = new HashMap<>();
					for (int i = 0; i < numattr; i++) {
						if (gattributes.containsKey(gattribute.get(i))) {
							if (!gattributes.get(gattribute.get(i)).contains(ss[i]))
								gattributes.get(gattribute.get(i)).add(ss[i]);
						} else {
							List<String> tmp = new ArrayList<>();
							tmp.add(ss[i]);
							gattributes.put(gattribute.get(i), tmp);
						}
						temp.add(ss[i]);
					}
					if (!label.contains(ss[numattr])) {
						label.add(ss[numattr]);
					}
					tuples.put(temp, ss[numattr]);
					data.add(tuples);
				}
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void readTestData(String _testset) {
		try {
			tdata = new ArrayList<>();
			BufferedReader bf = new BufferedReader(new FileReader(_testset));
			String str;
			int len = 0;
			while ((str = bf.readLine()) != null) {
				String[] ss = str.split("\t");
				if (len == 0) {
					len++;
				} else {
					List<String> temp = new ArrayList<>();
					Map<List<String>, String> tuples = new HashMap<>();
					for (int i = 0; i < numattr; i++) {
						temp.add(ss[i]);
					}
					tuples.put(temp, null);
					tdata.add(tuples);
				}
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void work(String _trainingset, String _testset, String _result) {
		trainingset = _trainingset;
		testset = _testset;
		resultset = _result;
		readTrainingData(trainingset);
		root = id3(data, gattributes, gattribute);
		readTestData(testset);
		TreeTest();
		writeResult(resultset);
	}

	void writeResult(String resultset) {
		try {
			PrintWriter pw = new PrintWriter(resultset);
			for (String attr : gattribute) {
				pw.print(attr + "\t");
			}
			pw.print(classname + "\n");
			for (Map<List<String>, String> line : rdata) {
				for (List<String> tuple : line.keySet()) {
					for (String value : tuple) {
						pw.print(value + "\t");
					}
					pw.print(line.get(tuple) + "\n");
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void TreeTest() {
		rdata = new ArrayList<>();
		for (Map<List<String>, String> line : tdata) {
			for (List<String> tuple : line.keySet()) {
				String tlabel = findLabel(tuple, root);
				Map<List<String>, String> temp = new HashMap<>();
				temp.put(tuple, tlabel);
				rdata.add(temp);
			}
		}
	}

	String findLabel(List<String> tuple, Node node) {
		boolean flag = false;
		if (node.label == null) {
			for (Node childnode : node.child) {
				if (childnode.value.equalsIgnoreCase(tuple.get(gattribute.indexOf(node.decision)))) {
					flag = true;
					return findLabel(tuple, childnode);
				}
			}
			if (!flag) {
				return majorityVote(node.list);
			}
		}
		return node.label;
	}

	Map<String, Integer> summarizeExamples(List<Map<List<String>, String>> examples) {
		List<Map<List<String>, String>> temp = new ArrayList<Map<List<String>, String>>(examples);
		Map<String, Integer> ret = new HashMap<>();
		for (String key : label) {
			ret.put(key, 0);
		}
		for (int i = 0; i < temp.size(); i++) {
			String fkey = (String) temp.get(i).values().toArray()[0];
			Integer count = ret.get(fkey);
			ret.put(fkey, (count + 1));
		}
		return ret;
	}

	String majorityVote(List<Map<List<String>, String>> _tuples) {
		List<Map<List<String>, String>> tmp = new ArrayList<Map<List<String>, String>>(_tuples);

		String ret = null;
		int[] temp = new int[label.size()];
		for (int i = 0; i < tmp.size(); i++) {
			String fkey = (String) tmp.get(i).values().toArray()[0];
			temp[label.indexOf(fkey)]++;
		}
		int max = -1;
		for (int i = 0; i < label.size(); i++) {
			if (temp[i] > max) {
				max = temp[i];
				ret = label.get(i);
			}
		}
		return ret;
	}

	Node id3(List<Map<List<String>, String>> _tuples, Map<String, List<String>> _attributes, List<String> _attribute) {
		List<Map<List<String>, String>> tuples = new ArrayList<Map<List<String>, String>>(_tuples);
		Map<String, List<String>> attributes = new HashMap<String, List<String>>(_attributes);
		List<String> attribute = new ArrayList<String>(_attribute);

		Node node = new Node(tuples);

		Map<String, Integer> dictionary = summarizeExamples(tuples);
		for (String key : dictionary.keySet()) {
			if (dictionary.get(key) >= tuples.size()*0.6) {
				node.label = key;
				return node;
			}
		}

		if (attribute.size() == 0) {
			node.label = majorityVote(tuples);
			return node;
		}
		// String bestattr = getBestAttr(getInfoGain(tuples, attributes, attribute));
		// String bestattr = getBestAttr(getGainRatio(tuples,attributes,attribute));
		String bestattr = getBestAttr(getGiniIndex(tuples, attributes, attribute));

		int idx = attribute.indexOf(bestattr);
		node.decision = bestattr;

		for (String value : attributes.get(bestattr)) {
			
			
			List<Map<List<String>, String>> ftuples = new ArrayList<>();
			for (Map<List<String>, String> tuple : tuples) {
				for (List<String> key : tuple.keySet()) {
					if (key.size() == _attribute.size())
						ftuples.add(tuple);
				}
			}
			

			List<Map<List<String>, String>> subexamples = new ArrayList<>();
			for (Map<List<String>, String> tuple : ftuples) {
				for (List<String> line : tuple.keySet()) {
					if (line.get(idx).equalsIgnoreCase(value)) {
						subexamples.add(tuple);
					}
				}
			}

			Node child = null;
			if (subexamples.size() == 0) {
				child = new Node(subexamples);
				child.label = majorityVote(tuples);
				child.value = value;
			} else {
				Map<String, List<String>> subattributes = new HashMap<String, List<String>>(attributes);
				subattributes.remove(bestattr);

				List<String> subattribute = new ArrayList<String>(attribute);
				subattribute.remove(idx);

				for (Map<List<String>, String> tuple : subexamples) {
					for (List<String> line : tuple.keySet()) {
						line.remove(idx);
					}
				}

				child = id3(subexamples, subattributes, subattribute);
				child.value = value;
				node.addBranch(child);
			}
		}
		return node;
	}

	String getBestAttr(Map<String, Double> infogain) {
		String ret = "";
		double min = 100.0;
		for (String key : infogain.keySet()) {
			if (infogain.get(key) < min) {
				min = infogain.get(key);
				ret = key;
			}
		}
		return ret;
	}

	Map<String, Double> getGiniIndex(List<Map<List<String>, String>> _tuples, Map<String, List<String>> _attributes,
			List<String> _attribute) {
		List<Map<List<String>, String>> tuples = new ArrayList<Map<List<String>, String>>(_tuples);
		Map<String, List<String>> attributes = new HashMap<String, List<String>>(_attributes);
		List<String> attribute = new ArrayList<String>(_attribute);

		Map<String, Double> gainstore = new HashMap<>();
		int arr[] = new int[label.size()];
		for (int i = 0; i < tuples.size(); i++) {
			String fkey = (String) tuples.get(i).values().toArray()[0];
			arr[label.indexOf(fkey)]++;
		}
		for (String attr : attributes.keySet()) {
			int idx = attribute.indexOf(attr);
			double global = tuples.size();
			double gain = 0;
			double info_a = 0;
			for (String value : attributes.get(attr)) {
				double local = 0;
				int[] Arr = new int[label.size()];
				for (int i = 0; i < tuples.size(); i++) {
					for (List<String> key : tuples.get(i).keySet()) {
						if (key.get(idx).equalsIgnoreCase(value)) {
							local++;
							String fkey = (String) tuples.get(i).values().toArray()[0];
							Arr[label.indexOf(fkey)]++;
						}
					}
				}
				info_a += local / global * gini(Arr);
			}
			gain = gini(arr) - info_a;
			gain = Math.round(gain * 1000) / 1000.0;
			gainstore.put(attr, gain);
		}
		return gainstore;
	}

	double gini(int[] arr) {
		double ret = 1;
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}

		for (int i = 0; i < arr.length; i++) {
			double p = arr[i] / sum;
			ret -= Math.pow(p, 2);
		}
		return Math.round(ret * 1000) / 1000.0;
	}

	Map<String, Double> getGainRatio(List<Map<List<String>, String>> _tuples, Map<String, List<String>> _attributes,
			List<String> _attribute) {
		Map<String, Double> gainstore = new HashMap<>();
		gainstore = getInfoGain(_tuples, _attributes, _attribute);

		for (String attr : _attributes.keySet()) {
			int idx = _attribute.indexOf(attr);
			int[] arr = new int[_attributes.get(attr).size()];
			int index = 0;
			for (String value : _attributes.get(attr)) {
				int local = 0;
				for (int i = 0; i < _tuples.size(); i++) {
					for (List<String> key : _tuples.get(i).keySet()) {
						if (key.get(idx).equalsIgnoreCase(value)) {
							local++;
						}
					}
				}
				arr[index] = local;
				index++;
			}
			double gain = gainstore.get(attr);
			gainstore.put(attr, gain / entropy(arr));
		}
		return gainstore;
	}

	Map<String, Double> getInfoGain(List<Map<List<String>, String>> _tuples, Map<String, List<String>> _attributes,
			List<String> _attribute) {

		Map<String, Double> gainstore = new HashMap<>();
		int infoDArray[] = new int[label.size()];
		for (int i = 0; i < _tuples.size(); i++) {
			String fkey = (String) _tuples.get(i).values().toArray()[0];
			infoDArray[label.indexOf(fkey)]++;
		}
		for (String attr : _attributes.keySet()) {
			int idx = _attribute.indexOf(attr);
			double global = _tuples.size();
			double gain = 0;
			double info_a = 0;
			for (String value : _attributes.get(attr)) {
				double local = 0;
				int[] infoADArray = new int[label.size()];
				for (int i = 0; i < _tuples.size(); i++) {
					for (List<String> key : _tuples.get(i).keySet()) {
						if (key.get(idx).equalsIgnoreCase(value)) {
							local++;
							String fkey = (String) _tuples.get(i).values().toArray()[0];
							infoADArray[label.indexOf(fkey)]++;
						}
					}
				}
				info_a += local / global * entropy(infoADArray);
			}
			gain = entropy(infoDArray) - info_a;
			gain = Math.round(gain * 1000) / 1000.0;
			gainstore.put(attr, gain);
		}
		return gainstore;
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

	public static void main(String[] args) {
		String trainingset, testset, result;
		if (args.length != 3) {
			System.out.println("args error!!!");
		} else {
			trainingset = args[0];
			testset = args[1];
			result = args[2];
			new Tree().work(trainingset, testset, result);
		}
	}

	public class Node {
		String label, decision, value;
		List<Node> child;
		List<Map<List<String>, String>> list;

		public Node(List<Map<List<String>, String>> _list) {
			this.list = _list;
			this.child = new ArrayList<Node>();
		}

		void addBranch(Node _child) {
			this.child.add(_child);
		}
	}
}