package cn.edu.sdjzu.campussecondhandtradingsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                        "/favorites/product/**",
                        "/favorites/status/**",
                        "/favorites/mine",
                        "/messages/product/**",
                        "/products/**",
                        "/upload");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path baseDir = Paths.get(uploadPath).isAbsolute()
                ? Paths.get(uploadPath)
                : Paths.get(System.getProperty("user.dir"), uploadPath);
        registry.addResourceHandler(urlPrefix + "/**")
                .addResourceLocations("file:" + baseDir.toAbsolutePath() + "/");
    }
}
