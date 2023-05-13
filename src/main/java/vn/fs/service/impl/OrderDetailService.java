package vn.fs.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.fs.entities.OrderEntity;
import vn.fs.model.response.StatisticalOrderDetailOfProduct;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.service.IOrderDetailService;

/**
 * @author DongTHD
 *
 */
@Service
public class OrderDetailService implements IOrderDetailService {

	@Autowired
	OrderRepository repo;

	@Autowired
	private OrderDetailRepository orderDetailRepository;

	public List<OrderEntity> listAll() {
		return (List<OrderEntity>) repo.findAll();
	}

	@Override
	public List<StatisticalOrderDetailOfProduct> findOrderDetailOfProduct(Pageable pageable) {
		// TODO Auto-generated method stub
		List<StatisticalOrderDetailOfProduct> statisticalOrderDetailOfProducts = new ArrayList<>();
		Page<Object[]> page = orderDetailRepository.statisticsByProduct(pageable);
		List<Object[]> objects = page.getContent();
		for (Object[] object : objects) {
			StatisticalOrderDetailOfProduct orderDetailOfProduct = new StatisticalOrderDetailOfProduct();
			orderDetailOfProduct.setProductName(object[0].toString());
			orderDetailOfProduct.setStatus(Boolean.parseBoolean(object[1].toString()));
			orderDetailOfProduct.setQuantity(Integer.parseInt(object[2].toString()));
			orderDetailOfProduct.setSumPrice(Double.parseDouble(object[3].toString()));
			orderDetailOfProduct.setAveragePrice(Double.parseDouble(object[4].toString()));
			orderDetailOfProduct.setMinimumPrice(Double.parseDouble(object[5].toString()));
			orderDetailOfProduct.setMaximumPrice(Double.parseDouble(object[6].toString()));
			statisticalOrderDetailOfProducts.add(orderDetailOfProduct);
		}
		return statisticalOrderDetailOfProducts;
	}

	@Override
	public int getTotalItem() {
		// TODO Auto-generated method stub
		int totalItem = orderDetailRepository.getTotalItem();
		return totalItem;
	}

	@Override
	public int getTotalItem(String name) {
		// TODO Auto-generated method stub
		int totalItem = orderDetailRepository.getTotalItem(name);
		return totalItem;
	}

	@Override
	public List<StatisticalOrderDetailOfProduct> findProductOfName(String key, Pageable pageable) {
		// TODO Auto-generated method stub
		List<StatisticalOrderDetailOfProduct> statisticalOrderDetailOfProducts = new ArrayList<>();
		Page<Object[]> page = orderDetailRepository.statisticsByProductOfKey(key, pageable);
		List<Object[]> objects = page.getContent();
		for (Object[] object : objects) {
			StatisticalOrderDetailOfProduct orderDetailOfProduct = new StatisticalOrderDetailOfProduct();
			orderDetailOfProduct.setProductName(object[0].toString());
			orderDetailOfProduct.setStatus(Boolean.parseBoolean(object[1].toString()));
			orderDetailOfProduct.setQuantity(Integer.parseInt(object[2].toString()));
			orderDetailOfProduct.setSumPrice(Double.parseDouble(object[3].toString()));
			orderDetailOfProduct.setAveragePrice(Double.parseDouble(object[4].toString()));
			orderDetailOfProduct.setMinimumPrice(Double.parseDouble(object[5].toString()));
			orderDetailOfProduct.setMaximumPrice(Double.parseDouble(object[6].toString()));
			statisticalOrderDetailOfProducts.add(orderDetailOfProduct);
		}
		return statisticalOrderDetailOfProducts;
	}

	@Override
	public List<StatisticalOrderDetailOfProduct> findAllOrderDetailOfProduct() {
		List<StatisticalOrderDetailOfProduct> statisticalOrderDetailOfProducts = new ArrayList<>();
		List<Object[]> objects = orderDetailRepository.repo();
		for (Object[] object : objects) {
			StatisticalOrderDetailOfProduct orderDetailOfProduct = new StatisticalOrderDetailOfProduct();
			orderDetailOfProduct.setProductId(Long.parseLong(object[0].toString()));
			orderDetailOfProduct.setProductName(object[1].toString());
			orderDetailOfProduct.setStatus(Boolean.parseBoolean(object[2].toString()));
			orderDetailOfProduct.setQuantity(Integer.parseInt(object[3].toString()));
			orderDetailOfProduct.setSumPrice(Double.parseDouble(object[4].toString()));
			orderDetailOfProduct.setAveragePrice(Double.parseDouble(object[5].toString()));
			orderDetailOfProduct.setMinimumPrice(Double.parseDouble(object[6].toString()));
			orderDetailOfProduct.setMaximumPrice(Double.parseDouble(object[7].toString()));
			statisticalOrderDetailOfProducts.add(orderDetailOfProduct);
		}
		return statisticalOrderDetailOfProducts;
	}

}