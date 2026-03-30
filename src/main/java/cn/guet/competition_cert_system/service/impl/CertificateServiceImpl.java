package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.entity.Certificate;
import cn.guet.competition_cert_system.entity.CertificateAuditLog;
import cn.guet.competition_cert_system.entity.CertificateInstructor;
import cn.guet.competition_cert_system.entity.CertificateRecipient;
import cn.guet.competition_cert_system.entity.User;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.exception.ForbiddenException;
import cn.guet.competition_cert_system.exception.NotFoundException;
import cn.guet.competition_cert_system.mapper.CertificateAuditLogMapper;
import cn.guet.competition_cert_system.mapper.CertificateInstructorMapper;
import cn.guet.competition_cert_system.mapper.CertificateMapper;
import cn.guet.competition_cert_system.mapper.CertificateRecipientMapper;
import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateMapper certificateMapper;
    private final CertificateInstructorMapper certificateInstructorMapper;
    private final CertificateRecipientMapper certificateRecipientMapper;
    private final UserMapper userMapper;
    private final CertificateAuditLogMapper certificateAuditLogMapper;

    // ==================== 查询操作 ====================

    @Override
    public List<CertificateSimpleVO> getUserCertificates(Long userId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long currentUserId = AuthContext.getUserId();

        // SELF 权限：只能查自己
        if (dataScope == DataScope.SELF && !currentUserId.equals(userId)) {
            throw new ForbiddenException("无权限查看该用户证书");
        }
        // RELATED/COLLEGE/ALL 权限：可以查任意用户

        List<CertificateSimpleVO> list = certificateMapper.getUserCertificates(userId);
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    @Override
    public UserCertificateDetailVO getCertificateDetail(Long certId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long currentUserId = AuthContext.getUserId();
        Long userCollegeId = AuthContext.getCollegeId();

        // 查证书
        Certificate cert = certificateMapper.selectById(certId);
        if (cert == null) {
            throw new NotFoundException("证书不存在");
        }

        // ALL 权限：什么都能看
        if (dataScope == DataScope.ALL) {
            UserCertificateDetailVO vo = certificateMapper.getCertificateDetailById(certId);
            if (vo != null) {
                vo.assemble();
            }
            return vo;
        }

        // COLLEGE 权限：只能看自己学院的证书
        if (dataScope == DataScope.COLLEGE) {
            if (!userCollegeId.equals(cert.getOwnerCollegeId())) {
                throw new ForbiddenException("无权限查看该证书");
            }
            UserCertificateDetailVO vo = certificateMapper.getCertificateDetailById(certId);
            if (vo != null) {
                vo.assemble();
            }
            return vo;
        }

        // SELF 权限：创建者/提交者/获得者/指导教师都能看
        if (dataScope == DataScope.SELF) {
            boolean isCreator = currentUserId.equals(cert.getCreatedBy());
            boolean isSubmitter = currentUserId.equals(cert.getSubmitterUserId());
            Boolean isRecipient = certificateRecipientMapper.isRecipient(certId, currentUserId);
            Boolean isInstructor = certificateInstructorMapper.isInstructor(certId, currentUserId);

            if (!isCreator && !isSubmitter && !isRecipient && !isInstructor) {
                throw new ForbiddenException("无权限查看该证书");
            }
            UserCertificateDetailVO vo = certificateMapper.getCertificateDetailById(certId);
            if (vo != null) {
                vo.assemble();
            }
            return vo;
        }

        // RELATED 权限：教师，只能看与自己关联的证书
        // 关联判断：这张证书的教师里有当前用户（是指导教师）
        if (dataScope == DataScope.RELATED) {
            // 检查当前用户是否是这张证书的指导教师
            Boolean isInstructor = certificateInstructorMapper.isInstructor(certId, currentUserId);
            if (isInstructor == null || !isInstructor) {
                throw new ForbiddenException("无权限查看该证书");
            }
            UserCertificateDetailVO vo = certificateMapper.getCertificateDetailById(certId);
            if (vo != null) {
                vo.assemble();
            }
            return vo;
        }

        throw new BusinessException(ResultCode.SYSTEM_ERROR, "系统异常");
    }

    @Override
    public List<CertificateSimpleVO> getStudentCertificatesByTeacher(Long teacherUserId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long currentUserId = AuthContext.getUserId();

        // SELF 权限：只能查自己
        if (dataScope == DataScope.SELF && !currentUserId.equals(teacherUserId)) {
            throw new ForbiddenException("无权限查看该教师的指导证书");
        }
        // RELATED/COLLEGE/ALL 权限：可以查

        List<CertificateSimpleVO> list = certificateMapper.getStudentCertificatesByTeacher(teacherUserId);
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    @Override
    public List<CertificateSimpleVO> getCollegeCertificates(Long collegeId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();

        // SELF/RELATED 权限：不能查学院数据
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限查看该学院证书");
        }

        // COLLEGE 权限：必须是自己学院
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限查看该学院证书");
        }
        // ALL 权限：可以查任意学院

        List<CertificateSimpleVO> list = certificateMapper.getCollegeCertificates(collegeId);
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    @Override
    public List<CertificateSimpleVO> getAllCertificates() {
        DataScope dataScope = AuthContext.getDataScope();

        // 只有 ALL 权限可以查看所有证书
        if (dataScope != DataScope.ALL) {
            throw new ForbiddenException("无权限查看所有证书");
        }

        List<CertificateSimpleVO> list = certificateMapper.getAllCertificates();
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    @Override
    public List<CertificateSimpleVO> getPendingReviewCertificates(Long collegeId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();

        // SELF/RELATED 权限：不能查待审核
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限查看待审核证书");
        }

        // COLLEGE 权限：必须是自己学院
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限查看该学院待审核证书");
        }
        // ALL 权限：可以查全部

        List<CertificateSimpleVO> list = certificateMapper.getPendingReviewCertificates(collegeId);
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    // ==================== 创建操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitCertificateApplication(CertificateSubmitDTO dto) {
        // 1. 证书编号唯一性校验
        if (Boolean.TRUE.equals(certificateMapper.existsCertNumberByCertNumber(dto.getCertNumber()))) {
            throw new BusinessException(ResultCode.CERT_NUMBER_DUPLICATED);
        }

        Long currentUserId = AuthContext.getUserId();
        Long collegeId = AuthContext.getCollegeId();
        LocalDateTime now = LocalDateTime.now();

        // 2. 构建 Certificate 主记录
        Certificate certificate = new Certificate();
        certificate.setCertType(dto.getCertType());
        certificate.setCertNumber(dto.getCertNumber());
        certificate.setOwnerCollegeId(collegeId);
        certificate.setCreatedBy(currentUserId);
        certificate.setSubmitterUserId(currentUserId);
        certificate.setEventId(dto.getEventId());
        certificate.setSessionId(dto.getSessionId());
        certificate.setGroupId(dto.getGroupId());
        certificate.setCategoryId(dto.getCategoryId());
        certificate.setCertTitle(dto.getCertTitle());
        certificate.setAwardLevel(dto.getAwardLevel());
        certificate.setIssueDate(dto.getIssueDate());
        certificate.setResourceKey(dto.getResourceKey());
        certificate.setStatus(CertStatus.PENDING_REVIEW);
        certificate.setRemark(dto.getRemark());
        certificate.setCreatedAt(now);
        certificate.setUpdatedAt(now);

        certificateMapper.insertCertificate(certificate);

        // 3. 根据证书类型处理关联数据
        if (dto.getCertType() == CertType.STUDENT) {
            // STUDENT 类型：学生×教师笛卡尔积，插入certificate_recipient
            // 先收集所有学生和教师的userId
            List<Long> studentUserIds = new java.util.ArrayList<>();
            if (dto.getStudents() != null) {
                for (CertificateSubmitDTO.StudentInfo studentInfo : dto.getStudents()) {
                    User user = userMapper.findByStudentId(studentInfo.getStudentId());
                    if (user == null) {
                        throw new BusinessException(ResultCode.STUDENT_NOT_FOUND, "学号不存在: " + studentInfo.getStudentId());
                    }
                    studentUserIds.add(user.getId());
                }
            }
            List<Long> teacherUserIds = new java.util.ArrayList<>();
            if (dto.getInstructors() != null) {
                for (CertificateSubmitDTO.TeacherInfo teacherInfo : dto.getInstructors()) {
                    User user = userMapper.findByTeacherId(teacherInfo.getTeacherId());
                    if (user == null) {
                        throw new BusinessException(ResultCode.TEACHER_NOT_FOUND, "工号不存在: " + teacherInfo.getTeacherId());
                    }
                    teacherUserIds.add(user.getId());
                }
            }
            // 笛卡尔积插入
            for (Long studentUserId : studentUserIds) {
                for (Long teacherUserId : teacherUserIds) {
                    CertificateRecipient recipient = new CertificateRecipient();
                    recipient.setCertificateId(certificate.getId());
                    recipient.setUserId(studentUserId);
                    recipient.setInstructorId(teacherUserId);
                    recipient.setCreatedAt(now);
                    certificateRecipientMapper.insert(recipient);
                }
            }
        } else if (dto.getCertType() == CertType.INSTRUCTOR) {
            // INSTRUCTOR 类型：插入持有教师到certificate_instructor
            if (dto.getHolder() != null) {
                User user = userMapper.findByTeacherId(dto.getHolder().getTeacherId());
                if (user == null) {
                    throw new BusinessException(ResultCode.TEACHER_NOT_FOUND, "工号不存在: " + dto.getHolder().getTeacherId());
                }
                CertificateInstructor instructor = new CertificateInstructor();
                instructor.setCertificateId(certificate.getId());
                instructor.setUserId(user.getId());
                instructor.setCreatedAt(now);
                certificateInstructorMapper.insert(instructor);
            }
        }

        // 4. 插入审核日志
        CertificateAuditLog auditLog = new CertificateAuditLog();
        auditLog.setCertificateId(certificate.getId());
        auditLog.setOperatorId(currentUserId);
        auditLog.setAction("SUBMIT");
        auditLog.setBeforeStatus(null);
        auditLog.setAfterStatus(CertStatus.PENDING_REVIEW.name());
        auditLog.setComment("提交证书申请");
        auditLog.setOperatedAt(now);
        certificateAuditLogMapper.insert(auditLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCertificate(Long certId, CertificateSubmitDTO dto) {
        // 1. 查证书是否存在
        Certificate existing = certificateMapper.selectById(certId);
        if (existing == null) {
            throw new NotFoundException("证书不存在");
        }

        // 2. DataScope 校验：COLLEGE_ADMIN 只能改自己学院的
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(existing.getOwnerCollegeId())) {
            throw new ForbiddenException("无权限修改该证书");
        }

        Long currentUserId = AuthContext.getUserId();
        LocalDateTime now = LocalDateTime.now();

        // 3. 更新主表字段
        existing.setCertTitle(dto.getCertTitle());
        existing.setAwardLevel(dto.getAwardLevel());
        existing.setIssueDate(dto.getIssueDate());
        existing.setResourceKey(dto.getResourceKey());
        existing.setRemark(dto.getRemark());
        existing.setUpdatedAt(now);
        certificateMapper.updateCertificate(existing);

        // 4. 重建关联数据：先删后插
        certificateMapper.deleteRecipientsByCertId(certId);
        certificateMapper.deleteInstructorsByCertId(certId);

        if (dto.getCertType() == CertType.STUDENT) {
            // STUDENT 类型：学生×教师笛卡尔积，插入certificate_recipient
            // 先收集所有学生和教师的userId
            List<Long> studentUserIds = new java.util.ArrayList<>();
            if (dto.getStudents() != null) {
                for (CertificateSubmitDTO.StudentInfo studentInfo : dto.getStudents()) {
                    User user = userMapper.findByStudentId(studentInfo.getStudentId());
                    if (user == null) {
                        throw new BusinessException(ResultCode.STUDENT_NOT_FOUND, "学号不存在: " + studentInfo.getStudentId());
                    }
                    studentUserIds.add(user.getId());
                }
            }
            List<Long> teacherUserIds = new java.util.ArrayList<>();
            if (dto.getInstructors() != null) {
                for (CertificateSubmitDTO.TeacherInfo teacherInfo : dto.getInstructors()) {
                    User user = userMapper.findByTeacherId(teacherInfo.getTeacherId());
                    if (user == null) {
                        throw new BusinessException(ResultCode.TEACHER_NOT_FOUND, "工号不存在: " + teacherInfo.getTeacherId());
                    }
                    teacherUserIds.add(user.getId());
                }
            }
            // 笛卡尔积插入
            for (Long studentUserId : studentUserIds) {
                for (Long teacherUserId : teacherUserIds) {
                    CertificateRecipient recipient = new CertificateRecipient();
                    recipient.setCertificateId(certId);
                    recipient.setUserId(studentUserId);
                    recipient.setInstructorId(teacherUserId);
                    recipient.setCreatedAt(now);
                    certificateRecipientMapper.insert(recipient);
                }
            }
        } else if (dto.getCertType() == CertType.INSTRUCTOR) {
            // INSTRUCTOR 类型：插入持有教师到certificate_instructor
            if (dto.getHolder() != null) {
                User user = userMapper.findByTeacherId(dto.getHolder().getTeacherId());
                if (user == null) {
                    throw new BusinessException(ResultCode.TEACHER_NOT_FOUND, "工号不存在: " + dto.getHolder().getTeacherId());
                }
                CertificateInstructor instructor = new CertificateInstructor();
                instructor.setCertificateId(certId);
                instructor.setUserId(user.getId());
                instructor.setCreatedAt(now);
                certificateInstructorMapper.insert(instructor);
            }
        }

        // 5. 审核日志
        CertificateAuditLog auditLog = new CertificateAuditLog();
        auditLog.setCertificateId(certId);
        auditLog.setOperatorId(currentUserId);
        auditLog.setAction("UPDATE");
        auditLog.setBeforeStatus(existing.getStatus().name());
        auditLog.setAfterStatus(existing.getStatus().name());
        auditLog.setComment("管理员更新证书");
        auditLog.setOperatedAt(now);
        certificateAuditLogMapper.insert(auditLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCertificate(Long certId) {
        // 1. 查证书
        Certificate existing = certificateMapper.selectById(certId);
        if (existing == null) {
            throw new NotFoundException("证书不存在");
        }

        // 2. 审核日志（先记日志再删）
        Long currentUserId = AuthContext.getUserId();
        LocalDateTime now = LocalDateTime.now();
        CertificateAuditLog auditLog = new CertificateAuditLog();
        auditLog.setCertificateId(certId);
        auditLog.setOperatorId(currentUserId);
        auditLog.setAction("DELETE");
        auditLog.setBeforeStatus(existing.getStatus().name());
        auditLog.setAfterStatus("DELETED");
        auditLog.setComment("管理员删除证书");
        auditLog.setOperatedAt(now);
        certificateAuditLogMapper.insert(auditLog);

        // 3. 删除关联表
        certificateMapper.deleteRecipientsByCertId(certId);
        certificateMapper.deleteInstructorsByCertId(certId);

        // 4. 删除主表
        certificateMapper.deleteById(certId);
    }

    @Override
    public void uploadCertificateImage() {
        throw new UnsupportedOperationException("Unimplemented method 'uploadCertificateImage'");
    }

    @Override
    public void reviewCertificate() {
        throw new UnsupportedOperationException("Unimplemented method 'reviewCertificate'");
    }

    @Override
    public void exportCollegeCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'exportCollegeCertificates'");
    }

    @Override
    public void exportAllCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'exportAllCertificates'");
    }

    @Override
    public void exportUserCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'exportUserCertificates'");
    }

    @Override
    public void exportMultipleUsersCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'exportMultipleUsersCertificates'");
    }
}
