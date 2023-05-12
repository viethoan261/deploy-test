package vn.fs.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import vn.fs.entities.CartItemEntity;
import vn.fs.entities.ProductEntity;

/**
 * @author DongTHD
 *
 */
@Service
public interface IShoppingCartService {

	int getCountCart();

	double getAmount();

	void clear();

	Collection<CartItemEntity> getCartItems();

	void remove(CartItemEntity item);

	void add(CartItemEntity item);

	void remove(ProductEntity product);

}
