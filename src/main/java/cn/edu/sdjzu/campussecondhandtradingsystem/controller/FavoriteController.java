package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody Favorite favorite) {
        if (favorite == null || favorite.getUserId() == null || favorite.getProductId() == null) {
            return Result.fail("userId and productId are required");
        }
        boolean saved = favoriteService.addFavorite(favorite.getUserId(), favorite.getProductId());
        return Result.success(saved);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(favoriteService.removeFavorite(id));
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<Favorite>> listByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.success(favoriteService.listFavorites(userId, page, size));
    }
}
