package com.spt.tools.core.sort;

/**
 * <p>选择排序</p>
 * 
 * 内部循环查找下一个最小（或最大）值，外部循环将该值放入其适当的位置。
 * @author wlddh
 *
 */
public class SelectionSort {

	public static int[] doSelectionSort(int[] arr) {

		for (int i = 0; i < arr.length - 1; i++) {
			int index = i;
			for (int j = i + 1; j < arr.length; j++)
				if (arr[j] < arr[index])
					index = j;

			int smallerNumber = arr[index];
			arr[index] = arr[i];
			arr[i] = smallerNumber;
		}
		return arr;
	}

	public static void main(String a[]) {

		int[] arr1 = { 10, 34, 2, 56, 7, 67, 88, 42 };
		int[] arr2 = doSelectionSort(arr1);
		for (int i : arr2) {
			System.out.print(i);
			System.out.print(", ");
		}
	}
}
