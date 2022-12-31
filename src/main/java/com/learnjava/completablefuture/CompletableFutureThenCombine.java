package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureThenCombine {
	public static void main(String[] args) {
		System.out.println(getCombinedCompletableFutureResult());
		System.out.println(getCombinedCompletableFutureResultMultiple());
	}

	public static String getCombinedCompletableFutureResult() {
		HelloWorldService helloWorldService = new HelloWorldService();
		CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> helloWorldService.hello());
		CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> helloWorldService.world());
		// completableFuture1.thenCombine(completableFuture2, BiFunction<String, String>);
		// Combining 2 CompletableFuture(s)
		return hello.thenCombine(world, (h, w) -> h + w)
				.thenApply(String::toUpperCase)
				//.thenAccept(result -> System.out.println("Result is: " + result))
				.join(); // this won't be done in real world scenario as this blocks the calling thread
	}

	public static String getCombinedCompletableFutureResultMultiple() {
		HelloWorldService helloWorldService = new HelloWorldService();
		CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> helloWorldService.hello());
		CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> helloWorldService.world());
		// completableFuture1.thenCombine(completableFuture2, BiFunction<String, String>);
		// Combining 2 CompletableFuture(s)
		CompletableFuture<String> anotherCompletableFuture = CompletableFuture.supplyAsync(() -> "Hello");
		return hello.thenCombine(world, (h, w) -> h + w)
				.thenCombine(anotherCompletableFuture, (previousResult, anotherCompletableFutureResult) -> previousResult + anotherCompletableFutureResult)
				.thenApply(String::toUpperCase)
				//.thenAccept(result -> System.out.println("Result is: " + result))
				.join(); // this won't be done in real world scenario as this blocks the calling thread
	}
}
