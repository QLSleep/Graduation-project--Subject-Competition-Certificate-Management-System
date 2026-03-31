package cn.guet.competition_cert_system.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书导出VO（Excel专用）
 */
@Data
@NoArgsConstructor
public class CertificateExportVO {

    /** 序号 */
    @ExcelProperty("序号")
    private String seq;

    /** 学院名称 */
    @ExcelProperty("学院")
    private String collegeName;

    /** 赛事名称 */
    @ExcelProperty("赛事名称")
    private String eventName;

    /** 赛事级别 */
    @ExcelProperty("赛事级别")
    private String level;

    /** 获奖等级 */
    @ExcelProperty("获奖等级")
    private String awardLevel;

    /** 获奖学生（逗号分隔） */
    @ExcelProperty("获奖学生")
    private String studentsStr;

    /** 指导教师（逗号分隔） */
    @ExcelProperty("指导教师")
    private String instructorsStr;

    /** 主办单位 */
    @ExcelProperty("主办单位")
    private String sponsor;

    /** 备注 */
    @ExcelProperty("备注")
    private String remark;
}
