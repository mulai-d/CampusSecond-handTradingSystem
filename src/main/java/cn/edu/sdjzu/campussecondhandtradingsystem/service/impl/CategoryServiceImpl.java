package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Category;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.CategoryMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listAll() {
        return list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    @Override
    public Category addCategory(Category category) {
        if (category.getSort() == null) {
            category.setSort(0);
        }
        save(category);
        return category;
    }

    @Override
    public boolean updateCategory(Category category) {
        return updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        return removeById(id);
    }
}
