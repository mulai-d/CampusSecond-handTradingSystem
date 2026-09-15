package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.dto.LoginRequest;
import cn.edu.sdjzu.campussecondhandtradingsystem.dto.LoginResponse;
import cn.edu.sdjzu.campussecondhandtradingsystem.dto.RegisterRequest;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody RegisterRequest request) {
        String username = request == null ? null : request.getUsername();
        String password = request == null ? null : request.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.fail("用户名和密码不能为空");
        }

        username = username.trim();
        if (username.length() > 50) {
            return Result.fail("用户名过长");
        }
        if (password.length() < 6 || password.length() > 100) {
            return Result.fail("密码长度需在 6 到 100 位之间");
        }
        if (userService.existsByUsername(username)) {
            return Result.fail("用户名已存在");
        }

        return userService.register(username, password)
                ? Result.success(true)
                : Result.fail("注册失败");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            return Result.fail("用户名和密码不能为空");
        }

        LoginResponse response = userService.login(request.getUsername().trim(), request.getPassword());
        return response == null ? Result.fail("用户名或密码错误") : Result.success(response);
    }
}
