package cn.guet.competition_cert_system.vo;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户证书详情VO
 * 包含证书基本信息、获得者列表和指导教师列表
 */
@Data
@NoArgsConstructor
public class UserCertificateDetailVO {

    /** 证书ID */
    private Long id;

    /** 证书类型 */
    private CertType certType;

    /** 证书号码 */
    private String certNumber;

    /** 归属学院 */
    private String collegeName;

    /** 创建人 */
    private String creatorName;

    /** 提交人 */
    private String submitterName;

    /** 审核人 */
    private String reviewerName;

    /** 赛事 */
    private String eventName;

    /** 赛届 */
    private String sessionName;

    /** 科目组别 */
    private String groupName;

    /** 赛类名称 */
    private String categoryName;

    /** 赛事级别 */
    private String level;

    /** 证书标题 */
    private String certTitle;

    /** 获奖级别 */
    private String awardLevel;

    /** 签发日期 */
    private LocalDate issueDate;

    /** 图片资源路径 */
    private String resourceKey;

    /** 状态 */
    private CertStatus status;

    /** 备注 */
    private String remark;

    /** 驳回原因 */
    private String rejectReason;

    /** 最后审核时间 */
    private LocalDateTime reviewedAt;

    /** 创建时间（仅详情展示） */
    private LocalDateTime createdAt;

    /** 更新时间（仅详情展示） */
    private LocalDateTime updatedAt;

    // ========== 以下为内部字段，由 SQL 的 ARRAY_AGG 填充 ==========

    /** 学生姓名数组（SQL text[] -> Java String[]） */
    private String[] studentNames;

    /** 学生学号数组（SQL text[] -> Java String[]） */
    private String[] studentIds;

    /** 教师姓名数组（SQL text[] -> Java String[]） */
    private String[] teacherNames;

    /** 教师工号数组（SQL text[] -> Java String[]） */
    private String[] teacherIds;

    // ========== 聚合后的 VO 列表（供外部使用） ==========

    /** 学生获得者列表（由 studentNames + studentIds 组装） */
    private List<CertificateStudentVO> recipients;

    /** 指导教师列表（由 teacherNames + teacherIds 组装，支持多教师） */
    private List<CertificateTeacherVO> instructors;

    /**
     * 组装 recipients 和 instructors 列表
     * 在 Service 层或 MyBatis 拦截器中调用
     */
    public void assemble() {
        // 组装学生列表
        this.recipients = new ArrayList<>();
        if (studentNames != null && studentIds != null) {
            int len = Math.min(studentNames.length, studentIds.length);
            for (int i = 0; i < len; i++) {
                if (studentNames[i] != null) {
                    CertificateStudentVO vo = new CertificateStudentVO();
                    vo.setRealName(studentNames[i]);
                    vo.setStudentId(studentIds[i]);
                    this.recipients.add(vo);
                }
            }
        }

        // 组装教师列表
        this.instructors = new ArrayList<>();
        if (teacherNames != null && teacherIds != null) {
            int len = Math.min(teacherNames.length, teacherIds.length);
            for (int i = 0; i < len; i++) {
                if (teacherNames[i] != null) {
                    CertificateTeacherVO vo = new CertificateTeacherVO();
                    vo.setRealName(teacherNames[i]);
                    vo.setTeacherId(teacherIds[i]);
                    this.instructors.add(vo);
                }
            }
        }
    }
}
