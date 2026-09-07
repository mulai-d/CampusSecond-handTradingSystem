package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProductService extends IService<Product> {

    PageResult<Product> listProducts(long page, long size, String keyword, String category);

    Product getProductDetail(Long id);

    Product viewProduct(Long id);

    Long getViewCount(Long id);
}
