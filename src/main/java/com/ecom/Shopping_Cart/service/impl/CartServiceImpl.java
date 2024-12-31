package com.ecom.Shopping_Cart.service.impl;

import com.ecom.Shopping_Cart.model.Cart;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.CartRepository;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.service.CartService;
import com.ecom.Shopping_Cart.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartByUser(int userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;
        List<Cart> updateCart = new ArrayList<>();

        for(Cart c : carts) {
           Double totalPrice = (c.getProduct().getDiscountPrice() *c.getQuantity());
           c.setTotalPrice(totalPrice);

           totalOrderPrice += totalPrice;
           c.setTotalOrderPrice(totalOrderPrice);
           updateCart.add(c);

        }



        return updateCart;
    }

    @Override
    public Integer getCountCart(Integer userId) {

        Integer countUser= cartRepository.countByUserId(userId);

        return countUser;
    }

    @Override
    public void updateQuantity(String sy, int cid) {
        Cart carts = cartRepository.findById(cid).get();

        int updateQuantity ;

        if(sy.equalsIgnoreCase("de")){
            updateQuantity = carts.getQuantity() - 1;

            if(updateQuantity <=0){
                cartRepository.delete(carts);
            }else{
                carts.setQuantity(updateQuantity);
                cartRepository.save(carts);
            }

        }else{
            updateQuantity = carts.getQuantity() + 1;
            carts.setQuantity(updateQuantity);
            cartRepository.save(carts);
        }


    }
}
