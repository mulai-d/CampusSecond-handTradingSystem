package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FavoriteService extends IService<Favorite> {

    boolean addFavorite(Long userId, Long productId);

    boolean removeFavorite(Long id);

    Favorite getFavorite(Long userId, Long productId);

    boolean removeFavorite(Long userId, Long productId);

    PageResult<Favorite> listFavorites(Long userId, long page, long size);
}
