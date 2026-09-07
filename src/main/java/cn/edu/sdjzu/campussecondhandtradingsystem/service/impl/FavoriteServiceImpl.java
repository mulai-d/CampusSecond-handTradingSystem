package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.FavoriteMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.RedisCacheService;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.FavoriteService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private static final String FAVORITE_PAGE_KEY = "campus:favorite:user:";

    private static final Duration FAVORITE_PAGE_TTL = Duration.ofMinutes(5);

    private final RedisCacheService redisCacheService;

    public FavoriteServiceImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @Override
    public boolean addFavorite(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        long existingCount = count(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (existingCount > 0) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);

        try {
            boolean saved = save(favorite);
            if (saved) {
                invalidateFavoriteCache(userId);
            }
            return saved;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    @Override
    public boolean removeFavorite(Long id) {
        Favorite favorite = getById(id);
        boolean removed = removeById(id);
        if (removed && favorite != null) {
            invalidateFavoriteCache(favorite.getUserId());
        }
        return removed;
    }

    @Override
    public PageResult<Favorite> listFavorites(Long userId, long page, long size) {
        long current = Math.max(page, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        String cacheKey = FAVORITE_PAGE_KEY + userId + ":" + current + ":" + pageSize;

        Optional<PageResult<Favorite>> cached = redisCacheService.get(
                cacheKey, new TypeReference<PageResult<Favorite>>() {
                });
        if (cached.isPresent()) {
            return cached.get();
        }

        Page<Favorite> favoritePage = page(new Page<>(current, pageSize),
                Wrappers.<Favorite>lambdaQuery()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime));

        PageResult<Favorite> result = PageResult.of(
                favoritePage.getCurrent(),
                favoritePage.getSize(),
                favoritePage.getTotal(),
                favoritePage.getRecords());
        redisCacheService.set(cacheKey, result, FAVORITE_PAGE_TTL);
        return result;
    }

    private void invalidateFavoriteCache(Long userId) {
        redisCacheService.deleteByPattern(FAVORITE_PAGE_KEY + userId + ":*");
    }
}
