package com.example.backend.controller;

import com.example.backend.dto.CategoryDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.Product;
import com.example.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createCategory(@RequestBody CategoryDto category) {
        ResponseEntity response = categoryService.createCategory(category);
        return response;
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getCategoryList() {
        ResponseEntity response = categoryService.getCategoryList();
        return response;
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResponseDto> getCategoryById(@PathVariable Integer categoryId) {
        ResponseEntity response = categoryService.getCategoryById(categoryId);
        return response;
    }

    @PutMapping
    public ResponseEntity<ResponseDto> updateCategory(@RequestBody CategoryDto category) {
        ResponseEntity response = categoryService.updateCategory(category);
        return response;
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer categoryId) {
        ResponseEntity response = categoryService.deleteCategory(categoryId);
        return response;
    }

    @GetMapping("/{categoryId}/product")
    public ResponseEntity<ResponseDto> getProductsForCategory(@PathVariable Integer categoryId){
        ResponseEntity response = categoryService.getProductsForCategory(categoryId);
        return response;
    }

}