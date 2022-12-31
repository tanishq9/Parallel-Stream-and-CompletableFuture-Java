package com.learnjava.completablefuture;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.domain.Inventory;
import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import java.util.concurrent.CompletableFuture;

public class ProductServiceUsingCompletableFuture {
	private ProductInfoService productInfoService;
	private ReviewService reviewService;
	private InventoryService inventoryService;

	public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService, InventoryService inventoryService) {
		this.productInfoService = productInfoService;
		this.reviewService = reviewService;
		this.inventoryService = inventoryService;
	}

	public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService) {
		this.productInfoService = productInfoService;
		this.reviewService = reviewService;
	}

	public Product retrieveProductDetails(String productId) {
		stopWatch.start();

		CompletableFuture<ProductInfo> completableFutureProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
		CompletableFuture<Review> completableFutureReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

		// ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
		// Review review = reviewService.retrieveReviews(productId); // blocking call

		Product product = completableFutureProductInfo
				.thenCombine(completableFutureReview, (productInfo, review) -> new Product(productId, productInfo, review))
				.join(); // blocks the calling thread

		stopWatch.stop();
		log("Total Time Taken : " + stopWatch.getTime());
		return product;
	}

	public CompletableFuture<Product> retrieveProductDetails_ServerSide(String productId) {
		stopWatch.start();

		CompletableFuture<ProductInfo> completableFutureProductInfo =
				CompletableFuture
						.supplyAsync(() -> productInfoService.retrieveProductInfo(productId))
						// we cannot return a recovery value in case product service call fails hence we will be throwing exception and not returning a recovery value
						.whenComplete((result, exception) -> {
							System.out.println("Handling exception from Product service:" + exception.getMessage());
						});

		CompletableFuture<Review> completableFutureReview =
				CompletableFuture
						.supplyAsync(() -> reviewService.retrieveReviews(productId))
						.exceptionally((exception) -> {
							System.out.println("Handling exception from Review service:" + exception.getMessage());
							return Review.builder()
									.noOfReviews(0)
									.overallRating(0)
									.build();
						});

		// ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
		// Review review = reviewService.retrieveReviews(productId); // blocking call

		return completableFutureProductInfo
				.thenCombine(completableFutureReview, (productInfo, review) -> new Product(productId, productInfo, review));
	}

	public Product retrieveProductDetails_WithInventory(String productId) {
		stopWatch.start();

		// https://stackoverflow.com/questions/30778017/modifying-objects-within-stream-in-java8-while-iterating
		CompletableFuture<ProductInfo> completableFutureProductInfo =
				CompletableFuture
						.supplyAsync(() -> productInfoService.retrieveProductInfo(productId))
						.thenApply(productInfo -> {
							productInfo
									.getProductOptions()
									.parallelStream()
									.forEach(
											// forEach performs an action for each element of this stream.
											productOption -> {
												Inventory inventory = inventoryService.retrieveInventory(productOption);
												productOption.setInventory(inventory);
											}
									);
							return productInfo;
						});

		CompletableFuture<Review> completableFutureReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

		// ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
		// Review review = reviewService.retrieveReviews(productId); // blocking call

		Product product = completableFutureProductInfo
				.thenCombine(completableFutureReview, (productInfo, review) -> new Product(productId, productInfo, review))
				.join(); // blocks the calling thread

		stopWatch.stop();
		log("Total Time Taken : " + stopWatch.getTime());
		return product;
	}


	public static void main(String[] args) {

		ProductInfoService productInfoService = new ProductInfoService();
		ReviewService reviewService = new ReviewService();
		InventoryService inventoryService = new InventoryService();
		ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(productInfoService, reviewService, inventoryService);
		String productId = "ABC123";
		Product product = productService.retrieveProductDetails_WithInventory(productId);
		log("Product is " + product);
	}
}
