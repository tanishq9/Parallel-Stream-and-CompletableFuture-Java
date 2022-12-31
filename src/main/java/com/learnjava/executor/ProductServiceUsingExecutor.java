package com.learnjava.executor;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProductServiceUsingExecutor {

	static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // this will create thread pool having thread count equal to number of cores in the machine
	private ProductInfoService productInfoService;
	private ReviewService reviewService;

	public ProductServiceUsingExecutor(ProductInfoService productInfoService, ReviewService reviewService) {
		this.productInfoService = productInfoService;
		this.reviewService = reviewService;
	}

	public Product retrieveProductDetails(String productId) throws ExecutionException, InterruptedException {
		stopWatch.start();

		// ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
		// Review review = reviewService.retrieveReviews(productId); // blocking call

		Future<ProductInfo> productInfoFuture = executorService.submit(() -> productInfoService.retrieveProductInfo(productId));
		Future<Review> reviewFuture = executorService.submit(() -> reviewService.retrieveReviews(productId));

		ProductInfo productInfo = productInfoFuture.get();
		Review review = reviewFuture.get();

		stopWatch.stop();
		log("Total Time Taken : " + stopWatch.getTime());
		return new Product(productId, productInfo, review);
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		ProductInfoService productInfoService = new ProductInfoService();
		ReviewService reviewService = new ReviewService();
		ProductServiceUsingExecutor productServiceUsingExecutor = new ProductServiceUsingExecutor(productInfoService, reviewService);
		String productId = "ABC123";
		Product product = productServiceUsingExecutor.retrieveProductDetails(productId);
		log("Product is " + product);
		executorService.shutdown();
	}
}

