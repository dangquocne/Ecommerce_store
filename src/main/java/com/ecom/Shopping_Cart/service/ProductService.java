package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Product;
import org.springframework.data.domain.Page;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public Product updateProduct(Product product, MultipartFile image);

    public List<Product> getAllIsActiveProduct(String category);

    public List<Product> searchProduct(String ch);

    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize,String category);

    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize,String ch);

    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

    Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category,String ch);
}
