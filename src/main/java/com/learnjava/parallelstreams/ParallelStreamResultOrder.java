package com.learnjava.parallelstreams;

import com.learnjava.util.DataSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParallelStreamResultOrder {
	public static void main(String[] args) {
		Set<Integer> set = Set.of(1, 2, 3, 4, 5, 6, 7, 8);
		List<Integer> list = set
				.parallelStream()
				//.stream()
				.map(integer -> integer * 2)
				.collect(Collectors.toList());
		System.out.println(list);
		// [8, 6, 4, 2, 16, 14, 12, 10]
		// Order is not guaranteed for Set as it is not ordered

		LinkedList<Integer> linkedList = DataSet.generateIntegerLinkedList(10);
		list = linkedList.parallelStream()
				.map(integer -> integer * 2)
				.collect(Collectors.toList());
		System.out.println(list);
		// [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
		// Order is maintained for LL and AL but not for Set in the resultant list
	}
}
