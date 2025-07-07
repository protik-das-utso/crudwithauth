package com.example.beststore.model;

import jakarta.validation.constraints.NotEmpty;

public class CategoryModelDto {

    private Long id;
    @NotEmpty(message = "Category name cannot be empty")

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
