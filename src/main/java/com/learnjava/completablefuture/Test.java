package com.learnjava.completablefuture;

import java.util.concurrent.CompletableFuture;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getName());
			long start = System.currentTimeMillis();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
			System.out.println("Time taken: " + (end - start));
			System.out.println(Thread.currentThread().getName());
			System.out.println("Computing Result");
			return "Result";
		});
		System.out.println("Done");
		Thread.sleep(10000);
		// CompletableFuture is not like reactive stream that it won't start publishing until it is subscribed.
		// The function inside supplyAsync would be triggered as soon as the code executes.
	}
}
