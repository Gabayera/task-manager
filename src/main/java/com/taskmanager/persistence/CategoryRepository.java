package com.taskmanager.persistence;

import com.taskmanager.domain.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAllByUserId(String userId);

    List<Category> findAll();

    void save(Category category);

    void delete(Category category);

    void delete(String id);
}