package com.learnjava.service;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;

public class ProductServiceUsingThread {
	private ProductInfoService productInfoService;
	private ReviewService reviewService;

	public ProductServiceUsingThread(ProductInfoService productInfoService, ReviewService reviewService) {
		this.productInfoService = productInfoService;
		this.reviewService = reviewService;
	}

	public Product retrieveProductDetails(String productId) throws InterruptedException {
		stopWatch.start();

		// ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
		// Review review = reviewService.retrieveReviews(productId); // blocking call

		ProductInfoRunnable productInfoRunnable = new ProductInfoRunnable(productId);
		ReviewRunnable reviewRunnable = new ReviewRunnable(productId);

		Thread productInfoThread = new Thread(productInfoRunnable);
		Thread reviewThread = new Thread(reviewRunnable);

		productInfoThread.start();
		reviewThread.start();

		productInfoThread.join();
		reviewThread.join();

		stopWatch.stop();
		log("Total Time Taken : " + stopWatch.getTime());
		return new Product(productId, productInfoRunnable.getProductInfo(), reviewRunnable.getReview());
	}

	public static void main(String[] args) throws InterruptedException {
		ProductInfoService productInfoService = new ProductInfoService();
		ReviewService reviewService = new ReviewService();
		ProductServiceUsingThread productService = new ProductServiceUsingThread(productInfoService, reviewService);
		String productId = "ABC123";
		Product product = productService.retrieveProductDetails(productId);
		log("Product is " + product);
	}

	private class ProductInfoRunnable implements Runnable {
		String productId;
		ProductInfo productInfo;

		public ProductInfoRunnable(String productId) {
			this.productId = productId;
		}

		public ProductInfo getProductInfo() {
			return productInfo;
		}

		@Override
		public void run() {
			this.productInfo = productInfoService.retrieveProductInfo(productId);
		}
	}

	private class ReviewRunnable implements Runnable {
		String productId;
		Review review;

		public ReviewRunnable(String productId) {
			this.productId = productId;
		}

		public Review getReview() {
			return review;
		}

		@Override
		public void run() {
			this.review = reviewService.retrieveReviews(productId);
		}
	}
}
