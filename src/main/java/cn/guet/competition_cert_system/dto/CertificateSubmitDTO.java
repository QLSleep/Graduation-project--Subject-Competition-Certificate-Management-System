package cn.guet.competition_cert_system.dto;

import cn.guet.competition_cert_system.common.CertType;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书提交 DTO
 */
@Data
@NoArgsConstructor
public class CertificateSubmitDTO {

    /** 证书类型 */
    private CertType certType;

    /** 证书编号 */
    private String certNumber;

    /** 赛事ID */
    private Long eventId;

    /** 赛届ID */
    private Long sessionId;

    /** 科目组别ID */
    private Long groupId;

    /** 赛类ID */
    private Long categoryId;

    /** 证书标题 */
    private String certTitle;

    /** 获奖等级 */
    private String awardLevel;

    /** 签发日期 */
    private LocalDate issuDate;

    /** 证书图片路径（上传接口返回） */
    private String resourceKey;

    /** 备注（可选） */
    private String remark;

    // ==================== 关联数据 ====================

    /** 学生信息列表（STUDENT类型时使用） */
    private List<StudentInfo> students;

    /** 指导教师列表（STUDENT类型时使用） */
    private List<TeacherInfo> instructors;

    /** 持有教师信息（INSTRUCTOR类型时使用） */
    private TeacherInfo holder;

    // ==================== 内部类 ====================

    @Data
    @NoArgsConstructor
    public static class StudentInfo {
        /** 姓名 */
        private String realName;
        /** 学号 */
        private String studentId;
    }

    @Data
    @NoArgsConstructor
    public static class TeacherInfo {
        /** 姓名 */
        private String realName;
        /** 工号 */
        private String teacherId;
    }
}
