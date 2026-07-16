package com.spt.tools.core.sort;

public class SwapNumber {
	public static void main(String[] args) {
		swap1();
		swap2();
	}

	public static void swap1() {
		int i = 4, j = 5;
		System.out.println("i:" + i + ",j:" + j);
		i = i ^ j;
		j = i ^ j;
		i = i ^ j;
		System.out.println("i:" + i + ",j:" + j);
	}
	public static void swap2() {
		int i = 1, j = 5;
		System.out.println("i:" + i + ",j:" + j);
		int tmp=i;
		i = j;
		j = tmp;
		System.out.println("i:" + i + ",j:" + j);
	}
}
