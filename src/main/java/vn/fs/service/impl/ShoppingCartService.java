package vn.fs.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import vn.fs.entities.CartItemEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.service.IShoppingCartService;

/**
 * @author DongTHD
 *
 */
@Service
public class ShoppingCartService implements IShoppingCartService {

	private Map<Long, CartItemEntity> map = new HashMap<Long, CartItemEntity>(); // <Long, CartItem>

	@Override
	public void add(CartItemEntity item) {
		CartItemEntity existedItem = map.get(item.getId());

		if (existedItem != null) {
			existedItem.setQuantity(item.getQuantity() + existedItem.getQuantity());
			existedItem.setTotalPrice(item.getTotalPrice() + existedItem.getProduct().getPrice() * existedItem.getQuantity());
		} else {
			map.put(item.getId(), item);
		}
	}

	@Override
	public void remove(CartItemEntity item) {

		map.remove(item.getId());

	}

	@Override
	public Collection<CartItemEntity> getCartItems() {
		return map.values();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public double getAmount() {
		return map.values().stream().mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice()).sum();
	}

	@Override
	public int getCountCart() {
		if (map.isEmpty()) {
			return 0;
		}
		int totalCartItems= 0;
		for(Map.Entry<Long, CartItemEntity> itemCart : map.entrySet()) {
			totalCartItems += itemCart.getValue().getQuantity();
		}
		return totalCartItems;
	}

	@Override
	public void remove(ProductEntity product) {

	}
}
