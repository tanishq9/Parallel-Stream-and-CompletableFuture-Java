package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.time.StopWatch;

public class CompletableFutureAllOf {

	HelloWorldService helloWorldService;

	CompletableFutureAllOf(HelloWorldService helloWorldService) {
		this.helloWorldService = helloWorldService;
	}

	public static void main(String[] args) {
		CompletableFutureAllOf completableFutureAllOf =
				new CompletableFutureAllOf(new HelloWorldService());

		System.out.println(completableFutureAllOf.testAllOf());
	}

	private List<String> testAllOf() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> helloWorldService.helloWorld());
		CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> helloWorldService.helloWorld());
		CompletableFuture<String> cf3 = CompletableFuture.supplyAsync(() -> helloWorldService.helloWorld());
		CompletableFuture<String> cf4 = CompletableFuture.supplyAsync(() -> helloWorldService.helloWorld());
		CompletableFuture<String> cf5 = CompletableFuture.supplyAsync(() -> helloWorldService.helloWorld());

		// Waiting for all completable futures to complete
		var completableFutureAllOf = CompletableFuture.allOf(cf1, cf2, cf3, cf4, cf5);

		// now we can proceed and call join for each completableFuture, it won't block the thread for long duration when each CF is completed (guarantee of allOf)
		return completableFutureAllOf.thenApply(
				v -> {
					stopWatch.stop();
					System.out.println("Time taken: " + stopWatch.getTime());
					List<String> result = new ArrayList<>();
					result.add(cf1.join());
					result.add(cf2.join());
					result.add(cf3.join());
					result.add(cf4.join());
					result.add(cf5.join());
					return result;
				})
				.join();
	}
}
