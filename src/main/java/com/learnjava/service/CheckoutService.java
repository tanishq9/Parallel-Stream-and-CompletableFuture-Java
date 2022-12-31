package com.learnjava.service;

import com.learnjava.domain.checkout.Cart;
import com.learnjava.domain.checkout.CartItem;
import com.learnjava.domain.checkout.CheckoutResponse;
import com.learnjava.domain.checkout.CheckoutStatus;
import com.learnjava.util.DataSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;

public class CheckoutService {

	PriceValidatorService priceValidatorService;

	public CheckoutService(PriceValidatorService priceValidatorService) {
		this.priceValidatorService = priceValidatorService;
	}

	// check validity of cart item
	public CheckoutResponse checkout(Cart cart) {
		List<CartItem> cartItemList = cart.getCartItemList()
				.parallelStream()
				.map(cartItem -> {
					boolean isPriceInvalid = priceValidatorService.isCartItemInvalid(cartItem);
					cartItem.setExpired(isPriceInvalid);
					return cartItem;
				})
				.filter(CartItem::isExpired)
				.collect(Collectors.toList());

		if (cartItemList.size() > 0) {
			return new CheckoutResponse(CheckoutStatus.FAILURE, cartItemList);
		}
		return new CheckoutResponse(CheckoutStatus.SUCCESS);
	}

	public static void main(String[] args) {
		CheckoutService checkoutService = new CheckoutService(new PriceValidatorService());
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		checkoutService.checkout(DataSet.createCart(12)); // would take ~500ms since number of cores are 6 (threads = 12) in my machine
		// checkoutService.checkout(DataSet.createCart(13)); // would take ~500ms x 2 (1 second) since number of cores are 6 (threads = 12) in my machine
		stopWatch.stop();
		System.out.println("Time taken: " + stopWatch.getTime());
	}
}
