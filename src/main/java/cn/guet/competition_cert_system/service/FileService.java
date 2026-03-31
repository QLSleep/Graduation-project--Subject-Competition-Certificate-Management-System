package cn.guet.competition_cert_system.service;

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
}
