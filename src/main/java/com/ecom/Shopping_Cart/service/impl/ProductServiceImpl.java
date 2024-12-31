package com.ecom.Shopping_Cart.service.impl;

import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import com.ecom.Shopping_Cart.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;



    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product) ;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Boolean deleteProduct(int id) {
        Product deleteProduct = productRepository.findById(id).orElse(null);

        if(!ObjectUtils.isEmpty(deleteProduct)){
            productRepository.delete(deleteProduct);
            return true;
        }

        return false;
    }

    @Override
    public Product getProductById(int id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }


    @Override
    @Transactional
    public Product updateProduct(Product product, MultipartFile image) {

        Product dbProduct = getProductById(product.getId());
        String imageName= image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        //5=100*(5/100) 100-5=95
        double discount = product.getPrice()*(product.getDiscount()/100.0);
        double discountPrice = product.getPrice() - discount;

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setDiscountPrice(discountPrice);
        dbProduct.setStock(product.getStock());
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setImage(imageName);

        Product updateProduct = productRepository.save(dbProduct);

        if(!ObjectUtils.isEmpty(updateProduct)){
            if(!image.isEmpty()){
                try{
                    //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                            + image.getOriginalFilename());



                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return product;

        }
        return null;
    }

    @Override
    public List<Product> getAllIsActiveProduct(String category) {
        List<Product> products = null;
        if(ObjectUtils.isEmpty(category)){
            products = productRepository.findByIsActiveTrue();
        }else{
            products=productRepository.findByCategory(category);
        }


        return products;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
    }


    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize,String category) {

        //phân trang
        Pageable pageable =PageRequest.of(pageNo,pageSize);// Gửi số trang và vui lòng trả lạ kích thước của trang

        Page<Product> pageProduct = null;//Đây là biến lưu kết quả các sản phẩm đã được phân trang, trả về dưới dạng Page

        if(ObjectUtils.isEmpty(category)){
            pageProduct = productRepository.findByIsActiveTrue(pageable); //Phương thức này tìm các sản phẩm đang hoạt động và áp dụng phân trang.
        }else{
            pageProduct =productRepository.findByCategory(pageable,category); //Phương thức này tìm các sản phẩm theo danh mục (category) và áp dụng phân trang.
        }

        return pageProduct;
    }


    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
        //phân trang
        Pageable pageable =PageRequest.of(pageNo,pageSize);// Gửi số trang và vui lòng trả lạ kích thước của trang
        return productRepository.findAll(pageable);
    }


    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
        Pageable pageable =PageRequest.of(pageNo,pageSize);

        Page<Product> pageProduct = null;//Đây là biến lưu kết quả các sản phẩm đã được phân trang, trả về dưới dạng Page
        pageProduct= productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);

        return pageProduct;

    }
}
