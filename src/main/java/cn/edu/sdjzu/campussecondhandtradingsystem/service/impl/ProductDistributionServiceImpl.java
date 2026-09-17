package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.ProductStatsMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.ProductDistributionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductDistributionServiceImpl implements ProductDistributionService {

    private final ProductStatsMapper productStatsMapper;

    public ProductDistributionServiceImpl(ProductStatsMapper productStatsMapper) {
        this.productStatsMapper = productStatsMapper;
    }

    @Override
    public Map<String, Object> getDistribution() {
        List<Map<String, Object>> categoryStats = productStatsMapper.selectCategoryStats();
        List<Map<String, Object>> priceStats = productStatsMapper.selectPriceStats();

        // 价格区间统计覆盖全部在售商品，用其求和得到在售商品总数
        int total = priceStats.stream()
                .mapToInt(item -> ((Number) item.get("count")).intValue())
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("categoryStats", categoryStats);
        result.put("priceStats", priceStats);
        result.put("total", total);
        return result;
    }
}
