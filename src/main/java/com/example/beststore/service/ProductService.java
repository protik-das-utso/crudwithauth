package com.example.beststore.service;

import com.example.beststore.ServiceImpl.ProductServiceImpl;
import com.example.beststore.model.CategoryModel;
import com.example.beststore.model.Product;
import com.example.beststore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<Product> getAllProducts();
    void addProduct(Product product);
    Product getProductById(Long id);
    void deleteProduct(Product product);
    void saveOrUpdateCategory(CategoryModel category);
    List<CategoryModel> getAllCategories();
    CategoryModel getCategoriesById(Long id);
    void deleteCategoryById(Long id);
}
