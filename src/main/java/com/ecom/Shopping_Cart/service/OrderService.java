package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.OrderRequest;
import com.ecom.Shopping_Cart.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    public void saveOrder(int userId, OrderRequest orderRequest) throws Exception;

    public List<ProductOrder> getOrderByUser(int userId);

    public ProductOrder UpdateOrderStatus(int id, String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrderByOrderId(String orderId);

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);



}
