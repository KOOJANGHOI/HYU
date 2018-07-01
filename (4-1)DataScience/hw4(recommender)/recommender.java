import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/* Recommender Class
 * 
 * final Random r				: Random instance
 * float learnRate				: learning rate
 * float regUser,regItem			: regularization term for user,item
 * int numIterations				: # of iteration
 * int numFactors				: # of latent factor
 * int numUsers,numItems			: # of user,item
 * String input					: inputfile name
 * SparseMatrix trainMatrix 		: rating matrix(observed user-item pair)
 * SparseMatrix netrainMatrix 	: rating matrix(neutral user-item pair)
 * DenseMatrix userFactors		: user-factor matrix 
 * DenseMatrix itemFactors		: item-factor matrix
 * DenseMatrix ratingMatrix		: rating matrix(result. ie, all user-item pair)
 */

public class recommender {
	final Random r = new Random(System.currentTimeMillis());
	float learnRate, regUser, regItem;
	int numIterations, numFactors, numUsers, numItems;
	String input;
	SparseMatrix trainMatrix, netrainMatrix; //
	DenseMatrix userFactors, itemFactors, ratingMatrix;

	void work(String _input) {
		input = _input;
		BuildTrainMatrix();
		BPRRecommender bprrecommender = new BPRRecommender();
		netrainMatrix = bprrecommender.recommend();
		setup();
		trainModel();
		buildRatingMatrix();
	}

	void BuildTrainMatrix() {
		BufferedReader bf;
		String str;
		try {
			bf = new BufferedReader(new FileReader(input));
			str = null;
			while ((str = bf.readLine()) != null) {
				String[] ss = str.split("\t");
				int user = Integer.parseInt(ss[0]);
				int item = Integer.parseInt(ss[1]);
				numUsers = Math.max(numUsers, user);
				numItems = Math.max(numItems, item);
			}
			str = null;
			bf.close();
			trainMatrix = new SparseMatrix(numUsers, numItems);
			bf = new BufferedReader(new FileReader(input));
			while ((str = bf.readLine()) != null) {
				String[] ss = str.split("\t");
				int user = Integer.parseInt(ss[0]);
				int item = Integer.parseInt(ss[1]);
				int rating = Integer.parseInt(ss[2]);
				trainMatrix.matrix[user - 1][item - 1] = rating;
			}
			bf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void setup() {
		numIterations = 300;
		learnRate = 0.01f;
		regUser = 0.01f;
		regItem = 0.01f;
		numFactors = 50;
		userFactors = new DenseMatrix(numUsers, numFactors);
		itemFactors = new DenseMatrix(numItems, numFactors);
		userFactors.init(0.0f, 0.1f);
		itemFactors.init(0.0f, 0.1f);
	}

	void trainModel() {
		System.out.println("MYREC:training");
		for (int iter = 1; iter <= numIterations; iter++) {
			double loss = 0.0d;
			double IA = 0, NA = 0, UA = 0;
			int cnt = 0;
			for (int sampleCount = 0, smax = numUsers * 100; sampleCount < smax; sampleCount++) {
				int userIdx, INItemIdx, NEItemIdx, UNItemIdx;
				while (true) {
					userIdx = uniform(numUsers);
					List<Integer> INItemList = trainMatrix.getColumns(userIdx);
					List<Integer> NEItemList = netrainMatrix.getColumns(userIdx);
					if (INItemList.size() == 0 || INItemList.size() == numItems)
						continue;
					if (NEItemList.size() == 0 || NEItemList.size() == numItems)
						continue;
					INItemIdx = INItemList.get(uniform(INItemList.size()));
					NEItemIdx = NEItemList.get(uniform(NEItemList.size()));
					do {
						UNItemIdx = uniform(numItems);
					} while (INItemList.contains(UNItemIdx) || NEItemList.contains(UNItemIdx));

					break;
				}
				double INPredictRating = rowMult(userFactors, userIdx, itemFactors, INItemIdx);
				double NEPredictRating = rowMult(userFactors, userIdx, itemFactors, NEItemIdx);
				double UNPredictRating = rowMult(userFactors, userIdx, itemFactors, UNItemIdx);
				IA += INPredictRating;
				NA += NEPredictRating;
				UA += UNPredictRating;
				cnt++;
				double alpha = 0.15;
				double beta = 1;
				double gamma = 0.85;
				double diffValueA = alpha * (INPredictRating - NEPredictRating);
				double diffValueB = beta * (INPredictRating - UNPredictRating);
				double diffValueC = gamma * (NEPredictRating - UNPredictRating);
				double lossValue = -Math.log(logistic(diffValueA)) - Math.log(logistic(diffValueB))
						- Math.log(logistic(diffValueC));
				loss += lossValue;
				double deriValueA = alpha * logistic(-diffValueA);
				double deriValueB = beta * logistic(-diffValueB);
				double deriValueC = gamma * logistic(-diffValueC);
				for (int factorIdx = 0; factorIdx < numFactors; factorIdx++) {
					double userFactorValue = userFactors.get(userIdx, factorIdx);
					double INItemFactorValue = itemFactors.get(INItemIdx, factorIdx);
					double NEItemFactorValue = itemFactors.get(NEItemIdx, factorIdx);
					double UNItemFactorValue = itemFactors.get(UNItemIdx, factorIdx);
					userFactors.add(userIdx, factorIdx,
							learnRate * (deriValueA * (INItemFactorValue - NEItemFactorValue)
									+ deriValueB * (INItemFactorValue - UNItemFactorValue)
									+ deriValueC * (NEItemFactorValue - UNItemFactorValue)
									- regUser * userFactorValue));
					itemFactors.add(INItemIdx, factorIdx, learnRate * ((deriValueA + deriValueB) * userFactorValue
							- regItem * itemFactors.get(INItemIdx, factorIdx)));
					itemFactors.add(NEItemIdx, factorIdx, learnRate * ((-deriValueA + deriValueC) * userFactorValue
							- regItem * itemFactors.get(NEItemIdx, factorIdx)));
					itemFactors.add(UNItemIdx, factorIdx, learnRate * ((-deriValueB - deriValueC) * userFactorValue
							- regItem * itemFactors.get(UNItemIdx, factorIdx)));
					loss += regUser * userFactorValue * userFactorValue
							+ regItem * INItemFactorValue * INItemFactorValue
							+ regItem * UNItemFactorValue * UNItemFactorValue
							+ regItem * NEItemFactorValue * NEItemFactorValue;
				}
			}
			System.out.println("[iter #" + iter + "][" + IA / cnt + "][" + NA / cnt + "][" + UA / cnt + "]");
		}

	}

	void buildRatingMatrix() {
		String output = input + "_prediction.txt";
		PrintWriter pw;
		try {
			pw = new PrintWriter(output);
			ratingMatrix = userFactors.mult(itemFactors.transpose());
			double max = -5, min = 10;
			for (int ui = 0; ui < numUsers; ui++) {
				for (int ii = 0; ii < numItems; ii++) {
					double rating = ratingMatrix.get(ui, ii);
					max = Math.max(max, rating);
					min = Math.min(min, rating);
				}
			}
			for (int ui = 0; ui < numUsers; ui++) {
				for (int ii = 0; ii < numItems; ii++) {
					int user = ui + 1;
					int item = ii + 1;
					double rating = ratingMatrix.get(ui, ii);
					double normrating = 5 * (rating - min) / (max - min);
					pw.printf("%d\t%d\t%f\n", user, item, normrating);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	double logistic(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	int uniform(int range) {
		return r.nextInt(range);
	}

	double rowMult(DenseMatrix m, int mrow, DenseMatrix n, int nrow) {
		assert m.numColumns == n.numColumns;
		double res = 0;
		for (int j = 0, k = m.numColumns; j < k; j++)
			res += m.get(mrow, j) * n.get(nrow, j);
		return res;
	}

	/* main method
	 * 
	 * store all parameters as training,test filename
	 * and call work method after create recommender instance.
	 * 
	 */
	public static void main(String[] args) {
		if (args.length != 2)
			System.out.println("args error");
		String base = args[0];
		new recommender().work(base);
	}

	/* SparseMatrix Class
	 * 
	 * int numRows,numColumns 		: # row , col
	 * double[][] matrix				: rating matrix
	 */
	public class SparseMatrix {
		int numRows, numColumns;
		double[][] matrix;

		/* constructor */
		public SparseMatrix(int _numRows, int _numColumns) {
			this.numRows = _numRows;
			this.numColumns = _numColumns;
			this.matrix = new double[numRows][numColumns];
		}

		public List<Integer> getColumns(int row) {
			List<Integer> res = new ArrayList<>();
			if (row > numRows)
				return res;
			for (int j = 0; j < numColumns; j++) {
				if (matrix[row][j] != 0.0)
					res.add(j);
			}
			return res;
		}

		public double get(int row, int col) {
			return this.matrix[row][col];
		}

		public void set(int row, int col, double value) {
			this.matrix[row][col] = value;
		}

		public double predict(int row, int col) {
			double res = 0;
			for (int j = 0; j < userFactors.numColumns; j++)
				res += userFactors.get(row, j) * itemFactors.get(row, j);
			return res;
		}

	}

	/* DenseMatrix Class
	 * 
	 * int numRows,numColumns 		: # row , col
	 * double[][] matrix				: rating matrix
	 */
	public class DenseMatrix {
		int numRows, numColumns;
		double[][] matrix;

		public DenseMatrix(int _numRows, int _numColumns) {
			this.numRows = _numRows;
			this.numColumns = _numColumns;
			this.matrix = new double[numRows][numColumns];
		}

		public double get(int row, int col) {
			return this.matrix[row][col];
		}

		public void set(int row, int col, double val) {
			matrix[row][col] = val;
		}

		public void add(int row, int column, double val) {
			matrix[row][column] += val;
		}

		public void init(double mean, double sigma) {
			for (int i = 0; i < numRows; i++)
				for (int j = 0; j < numColumns; j++)
					matrix[i][j] = mean + sigma * r.nextGaussian();
		}

		public DenseMatrix transpose() {
			DenseMatrix mat = new DenseMatrix(numColumns, numRows);
			for (int i = 0; i < mat.numRows; i++)
				for (int j = 0; j < mat.numColumns; j++)
					mat.set(i, j, this.matrix[j][i]);
			return mat;
		}

		public DenseMatrix mult(DenseMatrix mat) {
			DenseMatrix res = new DenseMatrix(this.numRows, mat.numColumns);
			for (int i = 0; i < res.numRows; i++) {
				for (int j = 0; j < res.numColumns; j++) {
					double product = 0;
					for (int k = 0; k < this.numColumns; k++)
						product += matrix[i][k] * mat.matrix[k][j];
					res.set(i, j, product);
				}
			}
			return res;
		}
	}

	/* Node Class
	 * 
	 * int user,item 				: user , item
	 * double rating					: rating
	 */
	public class Node implements Comparable<Node> {
		int user, item;
		double rating;

		public Node(int _user, int _item) {
			this.user = _user;
			this.item = _item;
		}

		public Node(int _user, int _item, double _rating) {
			this(_user, _item);
			this.rating = _rating;
		}

		@Override
		public int compareTo(Node o) {
			// TODO Auto-generated method stub
			if (rating < o.rating)
				return 1;
			else if (rating == o.rating) {
				return 0;
			}
			return -1;
		}
	}

	
	/* BPRRecommender Class
	 * 
	 * float learnRate				: learning rate
	 * float regUser,regItem			: regularization term for user,item
	 * int numIterations				: # of iteration
	 * int numFactors				: # of latent factor
	 * DenseMatrix userFactors		: user-factor matrix 
	 * DenseMatrix itemFactors		: item-factor matrix
	 */
	 public class BPRRecommender {
		float learnRate, regUser, regItem;
		int numIterations, numFactors;
		DenseMatrix userFactors, itemFactors;

		SparseMatrix recommend() {
			setup();
			return trainModel();
		}

		void setup() {
			numIterations = 300;
			learnRate = 0.01f;
			regUser = 0.01f;
			regItem = 0.01f;
			numFactors = 10;
			userFactors = new DenseMatrix(numUsers, numFactors);
			itemFactors = new DenseMatrix(numItems, numFactors);
			userFactors.init(0.0f, 0.1f);
			itemFactors.init(0.0f, 0.1f);
		}

		SparseMatrix trainModel() {
			System.out.println("BPRMF:training");
			for (int iter = 1; iter <= numIterations; iter++) {
				double loss = 0.0d;
				double posAvg = 0, negAvg = 0;
				int cnt = 0;
				for (int sampleCount = 0, smax = numUsers * 100; sampleCount < smax; sampleCount++) {
					int userIdx, posItemIdx, negItemIdx;
					while (true) {
						userIdx = uniform(numUsers);
						List<Integer> itemList = trainMatrix.getColumns(userIdx);
						if (itemList.size() == 0 || itemList.size() == numItems)
							continue;
						posItemIdx = itemList.get(uniform(itemList.size()));
						do {
							negItemIdx = uniform(numItems);
						} while (itemList.contains(negItemIdx));
						break;
					}
					double posPredictRating = rowMult(userFactors, userIdx, itemFactors, posItemIdx);
					double negPredictRating = rowMult(userFactors, userIdx, itemFactors, negItemIdx);
					posAvg += posPredictRating;
					negAvg += negPredictRating;
					cnt++;
					double diffValue = posPredictRating - negPredictRating;
					double lossValue = -Math.log(logistic(diffValue));
					loss += lossValue;
					double deriValue = logistic(-diffValue);
					for (int factorIdx = 0; factorIdx < numFactors; factorIdx++) {
						double userFactorValue = userFactors.get(userIdx, factorIdx);
						double posItemFactorValue = itemFactors.get(posItemIdx, factorIdx);
						double negItemFactorValue = itemFactors.get(negItemIdx, factorIdx);
						userFactors.add(userIdx, factorIdx, learnRate
								* (deriValue * (posItemFactorValue - negItemFactorValue) - regUser * userFactorValue));
						itemFactors.add(posItemIdx, factorIdx,
								learnRate * (deriValue * userFactorValue - regItem * posItemFactorValue));
						itemFactors.add(negItemIdx, factorIdx,
								learnRate * (deriValue * (-userFactorValue) - regItem * negItemFactorValue));
						loss += regUser * userFactorValue * userFactorValue
								+ regItem * posItemFactorValue * posItemFactorValue
								+ regItem * negItemFactorValue * negItemFactorValue;
					}
				}
				System.out.println("[iter #" + iter + "][" + posAvg / cnt + "][" + negAvg / cnt + "]");
			}
			DenseMatrix res = userFactors.mult(itemFactors.transpose());
			List<Node> list = new ArrayList<>();
			for (int ui = 0; ui < numUsers; ui++) {
				for (int ii = 0; ii < numItems; ii++) {
					if (trainMatrix.get(ui, ii) == 0) {
						list.add(new Node(ui, ii, res.get(ui, ii)));
					}
				}
			}
			Collections.sort(list);
			SparseMatrix netrainMatrix = new SparseMatrix(numUsers, numItems);
			for (int i = 0; i < list.size() * 0.2; i++) {
				int user = list.get(i).user;
				int item = list.get(i).item;
				double rating = list.get(i).rating;
				netrainMatrix.set(user, item, rating);
			}
			return netrainMatrix;
		}
	}
}