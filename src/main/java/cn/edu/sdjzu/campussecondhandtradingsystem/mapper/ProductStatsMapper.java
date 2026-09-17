package cn.edu.sdjzu.campussecondhandtradingsystem.mapper;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ProductStatsMapper extends BaseMapper<Product> {

    @Select("SELECT category_id AS categoryId, category AS categoryName, COUNT(*) AS `count` " +
            "FROM product WHERE status = 1 AND category_id IS NOT NULL " +
            "GROUP BY category_id, category ORDER BY `count` DESC")
    List<Map<String, Object>> selectCategoryStats();

    @Select("SELECT " +
            "CASE " +
            "WHEN price < 50 THEN '0-50元' " +
            "WHEN price < 100 THEN '50-100元' " +
            "WHEN price < 200 THEN '100-200元' " +
            "ELSE '200元以上' END AS `range`, " +
            "COUNT(*) AS `count` " +
            "FROM product WHERE status = 1 " +
            "GROUP BY `range` ORDER BY MIN(price)")
    List<Map<String, Object>> selectPriceStats();
}
