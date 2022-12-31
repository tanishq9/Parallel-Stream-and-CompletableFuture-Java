package com.learnjava.parallelstreams;

import com.learnjava.util.DataSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.time.StopWatch;

public class ParallelStreamLinkedListComparison {
	public static void main(String[] args) {
		LinkedList<Integer> linkedList = DataSet.generateIntegerLinkedList(1000);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		System.out.println(transformList(linkedList, false));
		stopWatch.stop();
		System.out.println("Time taken: " + stopWatch.getTime());
		stopWatch.reset();
		stopWatch.start();
		System.out.println(transformList(linkedList, true));
		stopWatch.stop();
		System.out.println("Time taken: " + stopWatch.getTime());
		// Using parallel streams for LinkedList gives disappointing results, this is because this type of collection is difficult to split into individual chunks.
	}

	static List<Integer> transformList(LinkedList<Integer> list, boolean isParallel) {
		Stream<Integer> stream = list.stream();
		if (isParallel) {
			stream.parallel();
		}
		return stream
				.map(integer -> integer * 2)
				.collect(Collectors.toList());
	}
}
