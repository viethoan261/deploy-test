package vn.fs.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import vn.fs.entities.OrderEntity;
import vn.fs.model.response.StatisticalOrderDetailOfProduct;

public interface IOrderDetailService {
	public List<StatisticalOrderDetailOfProduct> findOrderDetailOfProduct (Pageable pageable);
	public List<StatisticalOrderDetailOfProduct> findProductOfName (String key,Pageable pageable);
	public int getTotalItem ();
	public int getTotalItem (String name);
	List<OrderEntity> listAll();
	public List<StatisticalOrderDetailOfProduct> findAllOrderDetailOfProduct();
}
