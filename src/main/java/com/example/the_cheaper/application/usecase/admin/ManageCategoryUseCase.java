package com.example.the_cheaper.application.usecase.admin;

import com.example.the_cheaper.application.command.CreateCategoryCommand;
import com.example.the_cheaper.application.command.UpdateCategoryCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.model.Category;
import com.example.the_cheaper.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public ManageCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CreateCategoryCommand command) {
        throw new NotImplementedException("Chức năng tạo danh mục chưa được triển khai");
    }

    public Category updateCategory(UpdateCategoryCommand command) {
        throw new NotImplementedException("Chức năng cập nhật danh mục chưa được triển khai");
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }
}
