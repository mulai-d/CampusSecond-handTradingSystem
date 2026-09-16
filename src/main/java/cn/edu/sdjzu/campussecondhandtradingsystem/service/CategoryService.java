package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> listAll();

    Category addCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long id);
}
