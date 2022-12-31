package com.learnjava.parallelstreams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.time.StopWatch;

public class ParallelStreamArrayListComparison {
	public static void main(String[] args) {
		List<String> namesList = new ArrayList<>();
		namesList.add("Adam");
		namesList.add("Jack");
		namesList.add("Siri");
		namesList.add("Christine");

		ParallelStreamArrayListComparison example = new ParallelStreamArrayListComparison();
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		List<String> transformedList = example.stringTransform(namesList, true);
		System.out.println(transformedList);
		stopWatch.stop();
		System.out.println("Time taken: " + stopWatch.getTime());
	}

	public List<String> stringTransform(List<String> namesList, boolean isParallel) {
		Stream<String> stringStream = namesList.stream();

		if (isParallel) {
			stringStream.parallel();
		}

		return stringStream
				.map(ParallelStreamArrayListComparison::addNameLengthTransform)
				.collect(Collectors.toList());
	}

	public List<String> stringTransform(List<String> namesList) {
		return namesList
				.stream()
				.map(ParallelStreamArrayListComparison::addNameLengthTransform)
				.collect(Collectors.toList());
	}

	public List<String> stringTransformParallel(List<String> namesList) {
		return namesList
				.parallelStream()
				.map(ParallelStreamArrayListComparison::addNameLengthTransform)
				.collect(Collectors.toList());
	}

	static String addNameLengthTransform(String name) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return name.length() + " " + name;
	}
}
