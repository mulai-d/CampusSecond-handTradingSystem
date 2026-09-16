package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.config.JwtUtil;
import cn.edu.sdjzu.campussecondhandtradingsystem.dto.LoginResponse;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.User;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.UserMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean existsByUsername(String username) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)) > 0;
    }

    @Override
    public boolean register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return save(user);
    }

    @Override
    public LoginResponse login(String username, String password) {
        User user = getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username), false);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
}
