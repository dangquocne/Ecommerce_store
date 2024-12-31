package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Category;

import java.util.List;

public interface CategoryService {


    public Category saveCategory(Category category);

    public Boolean existCategory(String name);

    public List<Category> getAllCategory();

    public Boolean deleteCategory(int id);

    public Category getCategoryById(int id);

    public List<Category> getAllActiveCategory();
}
