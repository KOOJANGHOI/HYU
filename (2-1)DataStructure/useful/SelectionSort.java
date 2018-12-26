
Computer Science 132
Introduction To Computing II

Dickinson College
Fall Semester 2001
Grant Braught

/**
 * SelectionSort.java - 
 *   Performs a selection sort on a random array
 *   of integers.
 *
 * @author Grant William Braught
 * @author Dickinson College
 * @version 2/18/2000
 */
public class SelectionSort {
    public static void main (String[] args) {

	// Create an array of 100 integers.
	int[] intArray = new int[100];
	
	// Fill the array with random integers in a 
	// specified range.
	randomFillArray(intArray, 1, 1000);
	
	// Print out the array.
	System.out.println("Unsorted Array:");
	printArray(intArray);
	// Sort the array.
	sortArray(intArray);
	// Print out the array.
	System.out.println("\nSorted Array:");
	printArray(intArray);
    }

    /**
     * Sort an array of integers using the Selection Sort
     * algorithm.
     *
     * @param sortMe the array to be sorted.
     */
    static void sortArray(int[] sortMe) {
	int j;

	// For each index of the array find the item that
	// belongs in that index and put it there.
	for (int i=0; i<sortMe.length-1; i++) {
	    
	    // Find the index of the minimum element in 
	    // the array in indexes >= i
	    j = findMinIndex(sortMe,i);
	    
	    // Swap the ith element with the jth element.
	    // This puts the correct value into the ith 
	    // index.  So the array is now sorted up to
	    // and including the ith element.
	    swapArrayElements(sortMe,i,j);
	}
    }

    /**
     * Scan an array of integers starting at a specified 
     * location and return the index of the smallest integer
     * found.
     *
     * @param scanMe the array to be scanned.
     * @param start the index at which to start the scan.
     *
     * @return the index of the minimum element found in the
     *         scan.
     */
    static int findMinIndex(int[] scanMe, int start) {

	// Assume for now that the first element is 
	// the smallest.
	int minIndex = start;
	
	// Scan each element of the array.
	for (int i=start + 1; i<scanMe.length; i++) {

	    // If the ith element is smaller than all
	    // of the previous elements then i is the 
	    // index of new smallest element. 
	    if (scanMe[i] < scanMe[minIndex]) {
		minIndex = i;
	    }
	}

	return minIndex;
    }

    /**
     * Swap two elements in an array of integers.
     *
     * @param intArray the array containing the elements to
     *                 be swapped.
     * @param i the index of one of the elements to swap.
     * @param j the index of the other element to swap. 
     */
    static void swapArrayElements(int[] intArray, int i, int j) {
	int tmp;

	tmp = intArray[i];
	intArray[i] = intArray[j];
	intArray[j] = tmp;
    }

    /**
     * Fill the array passed as the parameter with
     * random integers between min and max.
     * 
     * @param fillMe the array to be filled with random integers.
     * @param min the minimum random integer to use.
     * @param max the maximum random integer to use.
     */
    static void randomFillArray(int[] fillMe, int min, int max) {

	for (int i=0; i < fillMe.length; i++) {
	    fillMe[i] = randomInt(min,max);
	}
    }

    /**
     * Generate a random integer in the range [min...max]
     *      
     * @param min the minimum random integer to use.
     * @param max the maximum random integer to use.
     *
     * @return a random integer in the range [min...max].
     */
    static int randomInt(int min, int max) {

	// max - min + 1 gives the number of possible values
	// the random integer can have.  A number from 0 to
	// max - min + 1 is generated.  That value is shifted
	// upward by min and cast as an integer to drop the
	// decimal.
	return (int)((max-min+1)*Math.random()+min);
    }

    /**
     * Print the contents of an integer array to the screen.
     *
     * @param printMe the array to be printed.
     */
    static void printArray(int[] printMe) {
	
	// Print out all but the last element separated
	// by commas.
	for (int i=0; i<printMe.length-1; i++) {
	    System.out.print(printMe[i] + ", ");
	}

	// Print out the last element and a new line.
	System.out.println(printMe[printMe.length-1]);
    }
}



SelectionSort.java : javadoc documentation.

This document was last updated 05/10/2017 00:25:14
