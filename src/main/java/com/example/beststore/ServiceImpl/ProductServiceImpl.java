package com.example.beststore.ServiceImpl;

import com.example.beststore.model.CategoryModel;
import com.example.beststore.model.Product;
import com.example.beststore.repository.CategoryRepository;
import com.example.beststore.repository.ProductRepository;
import com.example.beststore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id){
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteProduct(Product product){
        productRepository.delete(product);
    }

    @Override
    public void saveOrUpdateCategory(CategoryModel category) {
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryModel> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryModel getCategoriesById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

}
