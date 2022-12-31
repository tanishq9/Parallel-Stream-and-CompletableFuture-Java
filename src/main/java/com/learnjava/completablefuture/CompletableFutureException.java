package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureException {
	private final HelloWorldService helloWorldService;

	public CompletableFutureException(HelloWorldService helloWorldService) {
		this.helloWorldService = helloWorldService;
	}

	public static void main(String[] args) {
		CompletableFutureException completableFutureException = new CompletableFutureException(new HelloWorldService());
		System.out.println(completableFutureException.useHandle());
	}

	// handle - Invoked always as part of CF pipeline
	public String useHandle() {
		return CompletableFuture
				.supplyAsync(helloWorldService::helloWorld)  // runs this in a common fork-join pool
				.handle((res, exception) -> {
					if (exception != null) {
						System.out.println("Exception message: " + exception.getMessage());
						return "default value";
					}
					return res;
				})
				.thenApply(String::toUpperCase)
				.join();
	}

	// exceptionally - Invoked only if exception is thrown as part of CF pipeline
	public String useExceptionally() {
		return CompletableFuture
				.supplyAsync(helloWorldService::helloWorld)  // runs this in a common fork-join pool
				.exceptionally(throwable -> {
					System.out.println(throwable.getMessage());
					return throwable.getCause().getMessage() + " default value 1";
				})
				.thenApply(s -> {
					System.out.println("String is: " + s);
					return s.toUpperCase();
				})
				.thenApply(this::getResult)
				.exceptionally(throwable -> {
					System.out.println(throwable.getMessage());
					return throwable.getCause().getMessage() + " default value 2";
				})
				.thenApply(String::toUpperCase)
				// we can put .exceptionally to handle exceptions wherever we anticipate
				// after an exception is thrown, all operations would be skipped until handled by any handle operation like exceptionally
				.join();
	}

	String getResult(String str) {
		throw new RuntimeException("OOPS");
	}
}
