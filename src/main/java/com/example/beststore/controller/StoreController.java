package com.example.beststore.controller;

import com.example.beststore.model.CategoryModel;
import com.example.beststore.model.CategoryModelDto;
import com.example.beststore.model.Product;
import com.example.beststore.model.ProductDto;
import com.example.beststore.repository.CategoryRepository;
import com.example.beststore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
@ControllerAdvice
public class StoreController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping({"/", "", "/home"})
    public String HomePage(Model model, HttpSession session, Principal principal) {
        if (principal != null) {
            if(session.getAttribute("loginMessageShown") == null){
                model.addAttribute("message", "Login successful! Welcome, " + principal.getName() + "!");
                session.setAttribute("loginMessageShown", true);
            }
        }
        List<Product> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);

        boolean isLoggedIn = principal != null;
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "index";
    }

    @GetMapping("/add")
    public String addProductsForm(Model model){
        model.addAttribute("productDto", new ProductDto());
        List<CategoryModel> allCategories = productService.getAllCategories();
        model.addAttribute("categories", allCategories);

        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult result,
            Model model
    ){

        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()){
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }
        if(result.hasErrors()){
            List<CategoryModel> allCategories = productService.getAllCategories();
            model.addAttribute("categories", allCategories);
            return "add-product";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String stroageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try{
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir, stroageFileName);

            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + stroageFileName), StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }


        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(stroageFileName);

        productService.addProduct(product);

        return "redirect:/products";
    }
//    @PostMapping("/add")
//    public ResponseEntity<String> addProducts(
//            @RequestParam("name") String name,
//            @RequestParam("brand") String brand,
//            @RequestParam("category") String category,
//            @RequestParam("price") double price,
//            @RequestParam("description") String description,
//            @RequestParam("file") MultipartFile file
//    ){
//
//        try {
//            // Save image to public/images
//            String fileName = Path.of(file.getOriginalFilename()).getFileName().toString();
//            Path uploadPath = Paths.get("public/images", fileName);
//            Files.createDirectories(uploadPath.getParent());
//            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
//
//            // Save product to DB
//            Product product = new Product();
//            product.setName(name);
//            product.setBrand(brand);
//            product.setCategory(category);
//            product.setPrice(price);
//            product.setDescription(description);
//            product.setImageFileName(fileName);
//
//            productService.addProduct(product);
//
//            return ResponseEntity.ok("Product saved successfully!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save product.");
//        }
//
//    }

    @GetMapping("/update/{id}")
    public String ShowUpdateForm(@PathVariable Long id, Model model){
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        List<CategoryModel> allCategories = productService.getAllCategories();
        model.addAttribute("categories", allCategories);
        return "update-product";
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<String> UpdateProduct(
            @RequestParam("name") String name,
            @RequestParam("brand") String brand,
            @RequestParam("category") String category,
            @RequestParam("price") double price,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @PathVariable Long id
    ){
        try {
            // Save image to public/images
            String fileName = Path.of(file.getOriginalFilename()).getFileName().toString();
            Path uploadPath = Paths.get("public/images", fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            // Save product to DB
            Product product = productService.getProductById(id);
            product.setName(name);
            product.setBrand(brand);
            product.setCategory(category);
            product.setPrice(price);
            product.setDescription(description);
            product.setImageFileName(fileName);

            productService.addProduct(product);

            return ResponseEntity.ok("Product saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save product.");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        Product product = productService.getProductById(id);
        productService.deleteProduct(product);

        return "redirect:/products";
    }

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @GetMapping("/category")
//    public String addCategoryForm(Model model) {
//        model.addAttribute("categoryDto", new CategoryModelDto());
//        List<CategoryModel> categories = productService.getAllCategories();
//        model.addAttribute("categories", categories);
//        return "category";
//    }
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @PostMapping("/category/add")
//    public String addCategory(
//            @Valid @ModelAttribute("categoryDto") CategoryModelDto categoryDto,
//            BindingResult result,
//            Model model
//    ) {
//        if (result.hasErrors()) {
//            return "redirect:/products/category/add";
//        }
//
//        CategoryModel category = new CategoryModel();
//        category.setName(categoryDto.getName());
//
//        productService.saveOrUpdateCategory(category);
//
//        return "redirect:/products/category";
//    }
//
//    @GetMapping("/category/edit/{id}")
//    public String showEditCategoryForm(@PathVariable Long id, Model model) {
//        CategoryModel category = productService.getCategoriesById(id);
//        CategoryModelDto categoryModelDto = new CategoryModelDto();
//
//        categoryModelDto.setName(category.getName());
//
//        model.addAttribute("categoryDto", categoryModelDto);
//        model.addAttribute("update", true);                  // edit mode
//        model.addAttribute("categories", productService.getAllCategories());
//        return "category";                                  // same Thymeleaf view                         // Thymeleaf template: category.html (or category/index.html)
//    }
//
//    @PostMapping("/category/edit/{id}")
//    public String saveCategory(
//            @PathVariable Long id,
//            @ModelAttribute("category") CategoryModelDto categoryModelDto, Model model) {
//        CategoryModel existingCat = productService.getCategoriesById(id);
//        existingCat.setName(categoryModelDto.getName());
//        productService.saveOrUpdateCategory(existingCat);
//        return "redirect:/products/category";
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/category")
    public String showCategoryForm(Model model) {
        model.addAttribute("categoryDto", new CategoryModelDto()); // empty for add
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("update", false); // flag for form mode
        return "category";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/category/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        CategoryModel category = productService.getCategoriesById(id);
        CategoryModelDto dto = new CategoryModelDto();
        dto.setId(category.getId());
        dto.setName(category.getName());

        model.addAttribute("categoryDto", dto);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("update", true); // flag to switch to update mode
        return "category";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/category")
    public String saveCategory(
            @Valid @ModelAttribute("categoryDto") CategoryModelDto dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("update", dto.getId() != null); // preserve mode
            return "category"; // show form with errors
        }

        CategoryModel cat;
        if (dto.getId() != null) {
            // Update
            cat = productService.getCategoriesById(dto.getId());
            cat.setName(dto.getName());
        } else {
            // Add
            cat = new CategoryModel();
            cat.setName(dto.getName());
        }

        productService.saveOrUpdateCategory(cat);
        return "redirect:/products/category";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/category/delete/{id}")
    public String comfirmDelete(@PathVariable Long id){
        productService.deleteCategoryById(id);
        return "redirect:/products/category";
    }

}
