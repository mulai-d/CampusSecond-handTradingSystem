package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Product;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.FavoriteService;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.ProductService;
import cn.edu.sdjzu.campussecondhandtradingsystem.vo.FavoriteItemVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private static final Long CURRENT_USER_ID = 666L;

    private final FavoriteService favoriteService;

    private final ProductService productService;

    public FavoriteController(FavoriteService favoriteService, ProductService productService) {
        this.favoriteService = favoriteService;
        this.productService = productService;
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody Favorite favorite) {
        if (favorite == null || favorite.getUserId() == null || favorite.getProductId() == null) {
            return Result.fail("userId and productId are required");
        }
        boolean saved = favoriteService.addFavorite(favorite.getUserId(), favorite.getProductId());
        return Result.success(saved);
    }

    @PostMapping("/product/{productId}")
    public Result<Boolean> addCurrentUserFavorite(@PathVariable Long productId) {
        return Result.success(favoriteService.addFavorite(CURRENT_USER_ID, productId));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(favoriteService.removeFavorite(id));
    }

    @DeleteMapping("/product/{productId}")
    public Result<Boolean> removeCurrentUserFavorite(@PathVariable Long productId) {
        return Result.success(favoriteService.removeFavorite(CURRENT_USER_ID, productId));
    }

    @GetMapping("/status/{productId}")
    public Result<Boolean> currentUserFavoriteStatus(@PathVariable Long productId) {
        return Result.success(favoriteService.getFavorite(CURRENT_USER_ID, productId) != null);
    }

    @GetMapping("/mine")
    public Result<PageResult<FavoriteItemVO>> listCurrentUserFavorites(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        PageResult<Favorite> favoritePage = favoriteService.listFavorites(CURRENT_USER_ID, page, size);
        List<FavoriteItemVO> records = favoritePage.getRecords().stream()
                .map(this::toFavoriteItem)
                .collect(Collectors.toList());
        return Result.success(PageResult.of(
                favoritePage.getPage(),
                favoritePage.getSize(),
                favoritePage.getTotal(),
                records));
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<Favorite>> listByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.success(favoriteService.listFavorites(userId, page, size));
    }

    private FavoriteItemVO toFavoriteItem(Favorite favorite) {
        FavoriteItemVO item = new FavoriteItemVO();
        item.setFavoriteId(favorite.getId());
        item.setUserId(favorite.getUserId());
        item.setProductId(favorite.getProductId());
        item.setFavoriteTime(favorite.getCreateTime());

        Product product = productService.getById(favorite.getProductId());
        if (product != null) {
            item.setTitle(product.getTitle());
            item.setDescription(product.getDescription());
            item.setCategory(product.getCategory());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setViewCount(product.getViewCount());
        }
        return item;
    }
}
