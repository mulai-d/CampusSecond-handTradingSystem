package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import cn.edu.sdjzu.campussecondhandtradingsystem.dto.LoginResponse;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    boolean existsByUsername(String username);

    boolean register(String username, String password);

    LoginResponse login(String username, String password);
}
