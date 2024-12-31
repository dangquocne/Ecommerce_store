package com.ecom.Shopping_Cart.service.impl;

import com.ecom.Shopping_Cart.model.Cart;
import com.ecom.Shopping_Cart.model.OrderAddress;
import com.ecom.Shopping_Cart.model.OrderRequest;
import com.ecom.Shopping_Cart.model.ProductOrder;
import com.ecom.Shopping_Cart.repository.CartRepository;
import com.ecom.Shopping_Cart.repository.ProductOrderRepository;
import com.ecom.Shopping_Cart.service.OrderService;
import com.ecom.Shopping_Cart.util.CommonUtil;
import com.ecom.Shopping_Cart.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(int userId, OrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setQuantity(cart.getQuantity());

            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress orderAddress = new OrderAddress();
            orderAddress.setFirstName(orderRequest.getFirstName());
            orderAddress.setLastName(orderRequest.getLastName());

            orderAddress.setEmail(orderRequest.getEmail());
            orderAddress.setMobileNo(orderRequest.getMobileNo());

            orderAddress.setAddress(orderRequest.getAddress());
            orderAddress.setCity(orderRequest.getCity());
            orderAddress.setState(orderRequest.getState());
            orderAddress.setPincode(orderRequest.getPincode());

            order.setOrderAddress(orderAddress);
            ProductOrder saveOrder = productOrderRepository.save(order);
            commonUtil.sendEmailForProductOrder(saveOrder,"success");



        }


    }

    @Override
    public List<ProductOrder> getOrderByUser(int userId) {
        List<ProductOrder> orderUser = productOrderRepository.findByUserId(userId);
        return orderUser;
    }

    @Override
    public ProductOrder UpdateOrderStatus(int id, String status) {
        Optional<ProductOrder> findById = productOrderRepository.findById(id);
        if(findById.isPresent()) {
            ProductOrder order = findById.get();
            order.setStatus(status);
            ProductOrder saveOrder = productOrderRepository.save(order);
            return saveOrder;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepository.findAll();
    }

    @Override
    public ProductOrder getOrderByOrderId(String orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }


    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return productOrderRepository.findAll(pageable);
    }
}
