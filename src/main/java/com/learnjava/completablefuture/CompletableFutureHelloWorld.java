package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureHelloWorld {

	public static void main(String[] args) {
		HelloWorldService helloWorldService = new HelloWorldService();

		CompletableFuture
				.supplyAsync(helloWorldService::helloWorld)  // runs this in a common fork-join pool
				.thenApply(String::toUpperCase)
				.thenAccept((result) -> {
					System.out.println("Result is: " + result);
				})
				.join(); // this won't be done in real world scenario as this blocks the calling thread

		System.out.println("Done");
	}
}
