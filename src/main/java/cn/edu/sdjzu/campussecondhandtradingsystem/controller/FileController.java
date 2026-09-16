package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileController {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/jpg"));

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return Result.fail("仅支持 jpg、png、gif、webp 格式的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativePath = dateDir + "/" + fileName;

        Path baseDir = Paths.get(uploadPath).isAbsolute()
                ? Paths.get(uploadPath)
                : Paths.get(System.getProperty("user.dir"), uploadPath);
        File destDir = baseDir.resolve(dateDir).toFile();
        if (!destDir.exists() && !destDir.mkdirs()) {
            return Result.fail("创建上传目录失败");
        }

        File destFile = baseDir.resolve(relativePath).toFile();
        file.transferTo(destFile);

        String url = urlPrefix + "/" + relativePath;
        return Result.success(url);
    }
}
