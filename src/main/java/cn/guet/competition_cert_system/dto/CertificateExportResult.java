package cn.guet.competition_cert_system.dto;

import cn.guet.competition_cert_system.vo.CertificateExportVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 证书导出结果（包含数据和文件名）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateExportResult {

    /** 导出数据列表 */
    private List<CertificateExportVO> data;

    /** 文件名 */
    private String filename;
}
