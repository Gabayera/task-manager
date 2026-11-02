package com.taskmanager.service;

import com.taskmanager.domain.Category;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.CategoryRepository;
import com.taskmanager.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategories(String userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    public Category createCategory(String userId, String name, String color) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name cannot be empty");
        }

        List<Category> existingCategories = categoryRepository.findAllByUserId(userId);
        for (Category c : existingCategories) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException("Category name already exists");
            }
        }

        List<String> existingIds = new ArrayList<>();
        for (Category c : existingCategories) {
            existingIds.add(c.getId());
        }
        
        String id = IdGenerator.getNextId("C", existingIds);
        Category category = new Category(id, userId, name, color);
        categoryRepository.save(category);
        return category;
    }

    public void renameCategory(String categoryId, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new ValidationException("Name cannot be empty");
        }
        
        List<Category> allCategories = categoryRepository.findAll();
        Category target = null;
        for (Category c : allCategories) {
            if (c.getId().equals(categoryId)) {
                target = c;
                break;
            }
        }

        if (target == null) {
            throw new ValidationException("Category not found");
        }

        for (Category c : categoryRepository.findAllByUserId(target.getUserId())) {
            if (!c.getId().equals(categoryId) && c.getName().equalsIgnoreCase(newName.trim())) {
                throw new ValidationException("Category name already exists");
            }
        }

        Category updated = new Category(target.getId(), target.getUserId(), newName, target.getColor());
        categoryRepository.save(updated);
    }

    public void deleteCategory(String categoryId) {
        categoryRepository.delete(categoryId);
    }
}