import java.util.Scanner;

public class Main {
	int[] arr;

	void quicksort(int lo, int hi) {
		int i = lo, j = hi, pivot = arr[lo+(hi-lo)/2];
		while (i <= j) {
			while (arr[i] < pivot) {
				i++;
			}
			while (arr[j] > pivot) {
				j--;
			}
			if (i <= j) {
				int temp = arr[j];
				arr[j] = arr[i];
				arr[i] = temp;
				i++;
				j--;
			}
		}
		if (lo < j)
			quicksort(lo, j);
		if (i < hi)
			quicksort(i, hi);
	}

	void work() {
		Scanner scan = new Scanner(System.in);
		int size = scan.nextInt();
		arr = new int[size];
		for (int i = 0; i < size; i++) {
			arr[i] = scan.nextInt();
		}
		scan.close();
		quicksort(0, size - 1);
		for (int i = 0; i < size; i++) {
			System.out.print(arr[i] + " ");
		}
	}

	public static void main(String... args) {
		new Main().work();
	}
}