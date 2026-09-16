package cn.edu.sdjzu.campussecondhandtradingsystem.config;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.User;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final UserMapper userMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 商品留言列表为公开接口，放行 GET 请求
        if ("GET".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith("/messages/product/")) {
            return true;
        }

        // 商品浏览相关接口（列表、详情、浏览量）为公开接口，放行 GET 请求
        if ("GET".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith("/products")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith(BEARER)) {
            token = header.substring(BEARER.length());
        }

        Long userId = token == null ? null : jwtUtil.parseToken(token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.fail(401, "未登录或登录已过期")));
            return false;
        }

        // 查询用户角色并存入上下文，便于后续权限校验
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        String role = user != null ? user.getRole() : "user";
        UserContext.set(userId, role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
