package com.aistudy.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件处理工具类
 * 提供文件上传、删除等功能
 */
public class FileUtil {

    private static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * 保存上传的文件
     * @param file 上传的文件
     * @param uploadDir 上传目录
     * @return 保存后的文件名
     */
    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = getFileExtension(originalFilename);
        if (!isImageFile(extension)) {
            throw new IllegalArgumentException("不支持的文件类型，仅允许: jpg, jpeg, png, gif, webp");
        }
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());
        return newFilename;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return filename.substring(dotIndex).toLowerCase();
        }
        return "";
    }

    /**
     * 判断是否为图片文件
     */
    private static boolean isImageFile(String extension) {
        for (String allowed : ALLOWED_IMAGE_TYPES) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
