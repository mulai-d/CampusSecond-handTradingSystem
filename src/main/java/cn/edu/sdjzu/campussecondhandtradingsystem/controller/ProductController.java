package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Product;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(productService.listProducts(page, size, keyword, category));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.viewProduct(id);
        return product == null ? Result.fail(404, "product not found") : Result.success(product);
    }

    @GetMapping("/{id}/views")
    public Result<Long> views(@PathVariable Long id) {
        return Result.success(productService.getViewCount(id));
    }
}
