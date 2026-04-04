package cn.guet.competition_cert_system.service;

import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 保存证书图片文件
     * @param file 上传的文件
     * @param folder 子文件夹路径（如证书编号）
     * @param filename 文件名（含扩展名）
     * @return 完整的resourceKey = {folder}/{filename}
     */
    String saveImage(MultipartFile file, String folder, String filename);

    /**
     * 获取文件上传根路径
     * @return uploadPath
     */
    String getUploadPath();

    /**
     * 清空指定目录下的所有文件
     * @param folder 子文件夹路径（如证书编号）
     */
    void deleteFilesInDirectory(String folder);

    /**
     * 重命名图片文件
     * @param folder 子文件夹路径（如证书编号）
     * @param oldFilename 旧文件名（含扩展名）
     * @param newFilename 新文件名（含扩展名）
     */
    void renameImage(String folder, String oldFilename, String newFilename);

    /**
     * 获取指定目录下的文件对象（用于下载）
     * @param folder 子文件夹路径
     * @param filename 文件名（含扩展名）
     * @return 文件对象
     */
    File getImageFile(String folder, String filename);
}
