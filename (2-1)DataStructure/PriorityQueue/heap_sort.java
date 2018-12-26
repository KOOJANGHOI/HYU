package Heap;
 
import Selection.SelectionSort;
 
public class HeapSort {
    public static void heapSort(int[] arr) {
        Heap heap = new Heap();
 
        for (int i = 0; i < arr.length; i++) {
            heap.insertHeap(arr[i]);
        }
 
        for (int i = arr.length - 1; i >= 0; --i) {
            arr[i] = heap.deleteHeap();
 
        }
        System.out.println("힙 정렬 :");
        SelectionSort.printArr(arr);
    }
}


출처: http://palpit.tistory.com/130 [palpit's log-b]