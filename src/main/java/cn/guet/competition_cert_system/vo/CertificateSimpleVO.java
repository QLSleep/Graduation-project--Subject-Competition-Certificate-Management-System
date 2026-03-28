package cn.guet.competition_cert_system.vo;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书精简版VO
 * 给 RELATED/SELF 权限的用户查看，不包含敏感字段
 */
@Data
@NoArgsConstructor
public class CertificateSimpleVO {

    /** 证书ID */
    private Long id;

    /** 证书类型 */
    private CertType certType;

    /** 证书号码 */
    private String certNumber;

    /** 归属学院 */
    private String collegeName;

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

    /** 级别 */
    private String awardLevel;

    /** 签发日期 */
    private LocalDate issuDate;

    /** 图片资源路径 */
    private String resourceKey;

    /** 状态 */
    private CertStatus status;

    /** 备注 */
    private String remark;

    // ========== 以下为内部字段 ==========

    /** 学生姓名数组 */
    private String[] studentNames;

    /** 学生学号数组 */
    private String[] studentIds;

    /** 教师姓名数组 */
    private String[] teacherNames;

    /** 教师工号数组 */
    private String[] teacherIds;

    // ========== 聚合后的 VO 列表 ==========

    /** 学生获得者列表 */
    private List<CertificateStudentVO> recipients;

    /** 指导教师列表（支持多教师） */
    private List<CertificateTeacherVO> instructors;

    /**
     * 组装 recipients 和 instructors 列表
     */
    public void assemble() {
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
