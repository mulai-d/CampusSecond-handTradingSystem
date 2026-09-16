package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Category;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.CategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    @PostMapping
    public Result<Category> add(@RequestBody Category category) {
        return Result.success(categoryService.addCategory(category));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        boolean ok = categoryService.updateCategory(category);
        return ok ? Result.success() : Result.fail("分类不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean ok = categoryService.deleteCategory(id);
        return ok ? Result.success() : Result.fail("分类不存在");
    }
}
