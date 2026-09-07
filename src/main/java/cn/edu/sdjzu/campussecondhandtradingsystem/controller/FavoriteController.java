package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/cs")
    public List<Favorite> getFavorites() {

        return favoriteService.list();
    }
}
