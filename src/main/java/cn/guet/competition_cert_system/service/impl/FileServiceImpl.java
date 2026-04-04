package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/jpg"};

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String saveImage(MultipartFile file, String folder, String filename) {
        // 1. 文件校验
        validateFile(file);

        // 2. 构建目录
        String folderPath = uploadPath + File.separator + folder;
        Path dirPath = Paths.get(folderPath);

        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            log.error("创建目录失败: {}", folderPath, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "创建存储目录失败");
        }

        // 3. 保存文件
        String fullPath = folderPath + File.separator + filename;
        Path filePath = Paths.get(fullPath);

        try {
            file.transferTo(filePath.toFile());
            log.info("文件保存成功: {}", fullPath);
        } catch (IOException e) {
            log.error("文件保存失败: {}", fullPath, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件保存失败");
        }

        // 4. 返回resourceKey（相对路径，用于URL访问）
        return folder + "/" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件不能为空");
        }

        // 大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件大小不能超过3MB");
        }

        // 格式校验
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只支持JPG格式");
        }

        // 扩展名校验
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".jpg")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只支持JPG格式");
        }
    }

    @Override
    public String getUploadPath() {
        return uploadPath;
    }

    @Override
    public void deleteFilesInDirectory(String folder) {
        String folderPath = uploadPath + File.separator + folder;
        File dir = Paths.get(folderPath).toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void renameImage(String folder, String oldFilename, String newFilename) {
        if (oldFilename.equals(newFilename)) {
            return;
        }
        String folderPath = uploadPath + File.separator + folder;
        Path oldPath = Paths.get(folderPath, oldFilename);
        Path newPath = Paths.get(folderPath, newFilename);
        try {
            Files.move(oldPath, newPath);
            log.info("文件重命名成功: {} -> {}", oldPath, newPath);
        } catch (IOException e) {
            log.error("文件重命名失败: {} -> {}", oldPath, newPath, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件重命名失败");
        }
    }

    @Override
    public File getImageFile(String folder, String filename) {
        String fullPath = uploadPath + File.separator + folder + File.separator + filename;
        return Paths.get(fullPath).toFile();
    }
}
