package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Product;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.ProductMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.ProductService;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.RedisCacheService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final String PRODUCT_DETAIL_KEY = "campus:product:detail:";

    private static final String PRODUCT_PAGE_KEY = "campus:product:page:";

    private static final String PRODUCT_VIEW_KEY = "campus:product:view:";

    private static final Duration DETAIL_TTL = Duration.ofMinutes(30);

    private static final Duration PAGE_TTL = Duration.ofMinutes(10);

    private static final Duration VIEW_TTL = Duration.ofHours(1);

    private final RedisCacheService redisCacheService;

    public ProductServiceImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @Override
    public PageResult<Product> listProducts(long page, long size, String keyword, String category) {
        long current = Math.max(page, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        String cacheKey = PRODUCT_PAGE_KEY + current + ":" + pageSize + ":"
                + (keyword == null ? "" : keyword) + ":"
                + (category == null ? "" : category);

        Optional<PageResult<Product>> cached = redisCacheService.get(
                cacheKey, new TypeReference<PageResult<Product>>() {
                });
        if (cached.isPresent()) {
            return cached.get();
        }

        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(StringUtils.hasText(category), Product::getCategory, category)
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(Product::getTitle, keyword)
                        .or()
                        .like(Product::getDescription, keyword))
                .orderByDesc(Product::getCreateTime);

        Page<Product> productPage = page(new Page<>(current, pageSize), query);
        PageResult<Product> result = PageResult.of(
                productPage.getCurrent(),
                productPage.getSize(),
                productPage.getTotal(),
                productPage.getRecords());
        redisCacheService.set(cacheKey, result, PAGE_TTL);
        return result;
    }

    @Override
    public Product getProductDetail(Long id) {
        String cacheKey = PRODUCT_DETAIL_KEY + id;
        Optional<Product> cached = redisCacheService.get(cacheKey, new TypeReference<Product>() {
        });
        if (cached.isPresent()) {
            return cached.get();
        }

        Product product = getById(id);
        if (product == null || !Objects.equals(product.getStatus(), 1)) {
            return null;
        }

        redisCacheService.set(cacheKey, product, DETAIL_TTL);
        return product;
    }

    @Override
    public Product viewProduct(Long id) {
        Product product = getProductDetail(id);
        if (product == null) {
            return null;
        }

        long viewCount = incrementViewCount(id);
        product.setViewCount(viewCount);
        redisCacheService.set(PRODUCT_DETAIL_KEY + id, product, DETAIL_TTL);
        return product;
    }

    @Override
    public Long getViewCount(Long id) {
        String cacheKey = PRODUCT_VIEW_KEY + id;
        Optional<Long> cached = redisCacheService.get(cacheKey, new TypeReference<Long>() {
        });
        if (cached.isPresent()) {
            return cached.get();
        }

        Product product = getById(id);
        long viewCount = product == null ? 0L : product.getViewCount();
        redisCacheService.set(cacheKey, viewCount, VIEW_TTL);
        return viewCount;
    }

    private long incrementViewCount(Long id) {
        try {
            redisCacheService.increment(PRODUCT_VIEW_KEY + id);
        } catch (Exception ignored) {
            // Redis unavailable; the database remains the source of truth.
        }

        baseMapper.incrementViewCount(id);
        Product updated = getById(id);
        long viewCount = updated == null ? 0L : updated.getViewCount();
        redisCacheService.set(PRODUCT_VIEW_KEY + id, viewCount, VIEW_TTL);
        redisCacheService.delete(PRODUCT_DETAIL_KEY + id);
        return viewCount;
    }
}
