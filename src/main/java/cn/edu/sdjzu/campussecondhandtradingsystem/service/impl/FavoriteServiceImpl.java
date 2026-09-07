package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Favorite;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.FavoriteMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
}
