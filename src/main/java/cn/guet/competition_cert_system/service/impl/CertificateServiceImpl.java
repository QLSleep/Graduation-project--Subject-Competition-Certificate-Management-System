package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.CertAuditAction;
import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import cn.guet.competition_cert_system.common.CompetitionLevel;
import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.CertificateExportResult;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.entity.Certificate;
import cn.guet.competition_cert_system.entity.CertificateAuditLog;
import cn.guet.competition_cert_system.entity.CertificateInstructor;
import cn.guet.competition_cert_system.entity.CertificateRecipient;
import cn.guet.competition_cert_system.entity.College;
import cn.guet.competition_cert_system.entity.User;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.exception.ForbiddenException;
import cn.guet.competition_cert_system.exception.NotFoundException;
import cn.guet.competition_cert_system.mapper.CertificateAuditLogMapper;
import cn.guet.competition_cert_system.mapper.CertificateInstructorMapper;
import cn.guet.competition_cert_system.mapper.CertificateMapper;
import cn.guet.competition_cert_system.mapper.CertificateRecipientMapper;
import cn.guet.competition_cert_system.mapper.CollegeMapper;
import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.service.FileService;
import cn.guet.competition_cert_system.vo.CertificateExportVO;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.CertificateStatsVO;
import cn.guet.competition_cert_system.vo.CollegeCertImageVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateMapper certificateMapper;
    private final CertificateInstructorMapper certificateInstructorMapper;
    private final CertificateRecipientMapper certificateRecipientMapper;
    private final UserMapper userMapper;
    private final CertificateAuditLogMapper certificateAuditLogMapper;
    private final CollegeMapper collegeMapper;
    private final FileService fileService;

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
        if (dataScope == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED_ERROR, "用户权限范围未设置");
        }
        
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
            Boolean isInstructor = false;
            
            // INSTRUCTOR 类型证书：检查 certificate_instructor 表
            if (cert.getCertType() == cn.guet.competition_cert_system.common.CertType.INSTRUCTOR) {
                isInstructor = certificateInstructorMapper.isInstructor(certId, currentUserId);
            } 
            // STUDENT 类型证书：检查 certificate_recipient 表的 instructor_id
            else if (cert.getCertType() == cn.guet.competition_cert_system.common.CertType.STUDENT) {
                isInstructor = certificateRecipientMapper.isInstructor(certId, currentUserId);
            }
            
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
        // 管理员（超管/学院管理员）提交直接通过审核，无需审核流程
        List<String> roles = AuthContext.getRoles();
        if (roles.contains("SYSTEM_ADMIN") || roles.contains("COLLEGE_ADMIN")) {
            certificate.setStatus(CertStatus.APPROVED);
        } else {
            certificate.setStatus(CertStatus.PENDING_REVIEW);
        }
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
        auditLog.setAfterStatus(certificate.getStatus().name());
        auditLog.setComment(roles.contains("SYSTEM_ADMIN") || roles.contains("COLLEGE_ADMIN") ? "管理员直接创建并通过审核" : "提交证书申请");
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

        // 3. 更新主表字段（不包括 cert_type/cert_number/resource_key）
        existing.setEventId(dto.getEventId());
        existing.setSessionId(dto.getSessionId());
        existing.setGroupId(dto.getGroupId());
        existing.setCategoryId(dto.getCategoryId());
        existing.setCertTitle(dto.getCertTitle());
        existing.setAwardLevel(dto.getAwardLevel());
        existing.setIssueDate(dto.getIssueDate());
        existing.setRemark(dto.getRemark());
        // 状态：仅当传入了有效状态时才更新（管理员直接改状态）
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
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
    @Transactional(rollbackFor = Exception.class)
    public String uploadCertificateImage(Long certId, MultipartFile file) {
        // 1. 查证书详情
        Certificate certificate = certificateMapper.selectById(certId);
        if (certificate == null) {
            throw new NotFoundException("证书不存在");
        }

        // 2. DataScope 权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        Long currentUserId = AuthContext.getUserId();

        boolean hasPermission = false;
        if (dataScope == DataScope.ALL) {
            hasPermission = true;
        } else if (dataScope == DataScope.COLLEGE) {
            hasPermission = userCollegeId.equals(certificate.getOwnerCollegeId());
        } else if (dataScope == DataScope.SELF) {
            hasPermission = currentUserId.equals(certificate.getCreatedBy())
                    || currentUserId.equals(certificate.getSubmitterUserId());
        } else if (dataScope == DataScope.RELATED) {
            Boolean isInstructor = certificateInstructorMapper.isInstructor(certId, currentUserId);
            hasPermission = isInstructor != null && isInstructor;
        }

        if (!hasPermission) {
            throw new ForbiddenException("无权限上传该证书图片");
        }

        // 3. 查询证书详情获取文件名所需信息
        UserCertificateDetailVO detail = certificateMapper.getCertificateDetailById(certId);
        if (detail == null) {
            throw new NotFoundException("证书不存在");
        }
        detail.assemble();

        // 4. 构建文件名
        String filename = buildFilename(detail);

        // 5. 保存文件
        String resourceKey = fileService.saveImage(file, certificate.getCertNumber(), filename);

        // 6. 更新证书resourceKey
        certificate.setResourceKey(resourceKey);
        certificate.setUpdatedAt(LocalDateTime.now());
        certificateMapper.updateCertificate(certificate);

        return resourceKey;
    }

    /**
     * 构建文件名: {赛届}{赛事名}{赛级}{奖项}{姓名1、姓名2...}.jpg
     */
    private String buildFilename(UserCertificateDetailVO detail) {
        StringBuilder sb = new StringBuilder();

        // 赛届
        if (detail.getSessionName() != null) {
            sb.append(detail.getSessionName());
        }

        // 赛事名
        if (detail.getEventName() != null) {
            sb.append(detail.getEventName());
        }

        // 赛级
        if (detail.getLevel() != null) {
            try {
                CompetitionLevel level = CompetitionLevel.valueOf(detail.getLevel());
                sb.append(level.getDesc());
            } catch (IllegalArgumentException ignored) {
                sb.append(detail.getLevel());
            }
        }

        // 奖项
        if (detail.getAwardLevel() != null) {
            sb.append(detail.getAwardLevel());
        }

        // 姓名
        if (detail.getCertType() == CertType.STUDENT) {
            // 学生证书：拼接所有学生姓名
            if (detail.getRecipients() != null && !detail.getRecipients().isEmpty()) {
                String names = detail.getRecipients().stream()
                        .map(r -> r.getRealName())
                        .filter(n -> n != null)
                        .collect(Collectors.joining("、"));
                sb.append(names);
            }
        } else if (detail.getCertType() == CertType.INSTRUCTOR) {
            // 教师证书：拼接所有教师姓名
            if (detail.getInstructors() != null && !detail.getInstructors().isEmpty()) {
                String names = detail.getInstructors().stream()
                        .map(i -> i.getRealName())
                        .filter(n -> n != null)
                        .collect(Collectors.joining("、"));
                sb.append(names);
            }
        }

        sb.append(".jpg");
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCertificate(Long certId, String action, String comment) {
        // 1. 查证书
        Certificate certificate = certificateMapper.selectById(certId);
        if (certificate == null) {
            throw new NotFoundException("证书不存在");
        }

        // 2. DataScope 权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        Long currentUserId = AuthContext.getUserId();
        boolean isCollegeAdmin = dataScope == DataScope.COLLEGE;
        boolean isAllAdmin = dataScope == DataScope.ALL;

        // 3. 动作解析
        CertAuditAction auditAction;
        CertStatus newStatus;
        try {
            auditAction = CertAuditAction.valueOf(action);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "无效的审核动作");
        }

        // 4. 动作与权限校验
        switch (auditAction) {
            case APPROVE, REJECT, REJECT_NO_RESUBMIT -> {
                // 需要 AUDIT 权限，且数据范围匹配
                if (isCollegeAdmin && !userCollegeId.equals(certificate.getOwnerCollegeId())) {
                    throw new ForbiddenException("无权限审核该证书");
                }
                if (!isCollegeAdmin && !isAllAdmin) {
                    throw new ForbiddenException("无权限审核证书");
                }
            }
            case REISSUE -> {
                // 需要 AUDIT 权限，但 REVIEWER 不能补发
                if (!isAllAdmin) {
                    throw new ForbiddenException("无权限补发证书");
                }
                if (isCollegeAdmin && !userCollegeId.equals(certificate.getOwnerCollegeId())) {
                    throw new ForbiddenException("无权限补发该证书");
                }
            }
            case INVALID -> {
                // INVALID：SYSTEM_ADMIN 不限学院，COLLEGE_ADMIN 限本学院
                if (!isAllAdmin && !isCollegeAdmin) {
                    throw new ForbiddenException("无权限作废证书");
                }
                if (isCollegeAdmin && !userCollegeId.equals(certificate.getOwnerCollegeId())) {
                    throw new ForbiddenException("无权限作废该证书");
                }
            }
            default -> throw new BusinessException(ResultCode.PARAM_ERROR, "无效的审核动作");
        }

        // 5. 前置状态校验
        CertStatus currentStatus = certificate.getStatus();
        switch (auditAction) {
            case APPROVE, REJECT, REJECT_NO_RESUBMIT -> {
                if (currentStatus != CertStatus.PENDING_REVIEW) {
                    throw new BusinessException(ResultCode.CERT_NOT_PENDING_REVIEW);
                }
            }
            case REISSUE -> {
                if (currentStatus != CertStatus.APPROVED) {
                    throw new BusinessException(ResultCode.CERT_NOT_APPROVED);
                }
            }
            case INVALID -> {
                if (currentStatus != CertStatus.APPROVED
                        && currentStatus != CertStatus.REJECTED
                        && currentStatus != CertStatus.REJECTED_NO_RESUBMIT
                        && currentStatus != CertStatus.REISSUED) {
                    throw new BusinessException(ResultCode.CERT_STATUS_NOT_ALLOWED, "该证书状态不允许作废");
                }
            }
            default -> { /* 已在上一步拦截 */ }
        }

        // 6. 驳回/作废时 comment 必填
        if ((auditAction == CertAuditAction.REJECT
                || auditAction == CertAuditAction.REJECT_NO_RESUBMIT
                || auditAction == CertAuditAction.INVALID)
                && (comment == null || comment.isBlank())) {
            throw new BusinessException(ResultCode.REVIEW_COMMENT_REQUIRED);
        }

        // 7. 执行状态变更
        newStatus = switch (auditAction) {
            case APPROVE -> CertStatus.APPROVED;
            case REJECT -> CertStatus.REJECTED;
            case REJECT_NO_RESUBMIT -> CertStatus.REJECTED_NO_RESUBMIT;
            case REISSUE -> CertStatus.REISSUED;
            case INVALID -> CertStatus.INVALID;
            default -> throw new BusinessException(ResultCode.PARAM_ERROR, "无效的审核动作");
        };

        String beforeStatus = currentStatus.name();
        LocalDateTime now = LocalDateTime.now();

        certificate.setStatus(newStatus);
        certificate.setReviewerUserId(currentUserId);
        certificate.setReviewedAt(now);
        certificate.setRejectReason(auditAction == CertAuditAction.REJECT
                || auditAction == CertAuditAction.REJECT_NO_RESUBMIT ? comment : null);
        certificate.setUpdatedAt(now);
        certificateMapper.updateCertificate(certificate);

        // 8. 记录审核日志
        CertificateAuditLog auditLog = new CertificateAuditLog();
        auditLog.setCertificateId(certId);
        auditLog.setOperatorId(currentUserId);
        auditLog.setAction(auditAction.name());
        auditLog.setBeforeStatus(beforeStatus);
        auditLog.setAfterStatus(newStatus.name());
        auditLog.setComment(comment);
        auditLog.setOperatedAt(now);
        certificateAuditLogMapper.insert(auditLog);
    }

    @Override
    public CertificateExportResult exportCollegeStudentCertificates(Long collegeId) {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限导出该学院证书");
        }
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限导出学院证书");
        }

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getCollegeStudentCertificatesForExport(collegeId);
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        College college = collegeMapper.selectById(collegeId);
        String collegeCode = college != null ? college.getCollegeCode() : String.valueOf(collegeId);
        String filename = collegeId + "_" + collegeCode + "_学生证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportCollegeInstructorCertificates(Long collegeId) {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限导出该学院证书");
        }
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限导出学院证书");
        }

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getCollegeInstructorCertificatesForExport(collegeId);
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        College college = collegeMapper.selectById(collegeId);
        String collegeCode = college != null ? college.getCollegeCode() : String.valueOf(collegeId);
        String filename = collegeId + "_" + collegeCode + "_指导教师证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportMyStudentCertificates() {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        if (dataScope != DataScope.SELF) {
            throw new ForbiddenException("无权限导出个人证书");
        }
        Long userId = AuthContext.getUserId();

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getMyStudentCertificatesForExport(userId);
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        String realName = AuthContext.getRealName();
        String filename = realName + "_学生证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportMyInstructorCertificates() {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        if (dataScope != DataScope.SELF) {
            throw new ForbiddenException("无权限导出个人证书");
        }
        Long userId = AuthContext.getUserId();

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getMyInstructorCertificatesForExport(userId);
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        String realName = AuthContext.getRealName();
        String filename = realName + "_指导教师证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportTeacherGuidedStudentCertificates() {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        if (dataScope != DataScope.RELATED) {
            throw new ForbiddenException("无权限导出指导的学生证书");
        }
        Long teacherUserId = AuthContext.getUserId();

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getTeacherGuidedStudentCertificatesForExport(teacherUserId);
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        String realName = AuthContext.getRealName();
        String filename = realName + "_指导学生证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportAllStudentCertificates() {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        if (dataScope != DataScope.ALL) {
            throw new ForbiddenException("无权限导出全部学生证书");
        }

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getAllStudentCertificatesForExport();
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        String filename = "全部学生证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public CertificateExportResult exportAllInstructorCertificates() {
        // DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        if (dataScope != DataScope.ALL) {
            throw new ForbiddenException("无权限导出全部教师证书");
        }

        // 查询数据
        List<CertificateExportVO> list = certificateMapper.getAllInstructorCertificatesForExport();
        // 生成序号
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSeq(String.valueOf(i + 1));
        }

        // 构建文件名
        String filename = "全部指导教师证书.xlsx";

        return new CertificateExportResult(list, filename);
    }

    @Override
    public File downloadCertificateImage(Long certId) {
        // 1. 查询证书
        Certificate cert = certificateMapper.selectById(certId);
        if (cert == null) {
            throw new NotFoundException("证书不存在");
        }

        // 2. DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        Long userId = AuthContext.getUserId();

        boolean hasAccess = switch (dataScope) {
            case ALL -> true;
            case COLLEGE -> cert.getOwnerCollegeId().equals(userCollegeId);
            case RELATED, SELF -> cert.getSubmitterUserId().equals(userId)
                    || isTeacherOfCertificate(userId, certId);
            default -> false;
        };
        if (!hasAccess) {
            throw new ForbiddenException("无权限下载该证书图片");
        }

        // 3. 校验 resourceKey
        if (cert.getResourceKey() == null || cert.getResourceKey().isBlank()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_UPLOADED);
        }

        // 4. 解析物理路径并返回文件
        String fullPath = fileService.getUploadPath() + "/" + cert.getResourceKey();
        File file = new File(fullPath);
        if (!file.exists()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }
        return file;
    }

    /**
     * 判断当前用户是否为该证书的指导教师
     */
    private boolean isTeacherOfCertificate(Long teacherUserId, Long certId) {
        // isInstructor 已同时查 certificate_instructor 和 certificate_recipient
        return Boolean.TRUE.equals(certificateInstructorMapper.isInstructor(certId, teacherUserId));
    }

    @Override
    public File downloadStudentCertificateImages(Long studentId) {
        // 1. DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userId = AuthContext.getUserId();
        if (dataScope == DataScope.SELF && !userId.equals(studentId)) {
            throw new ForbiddenException("无权限下载该学生的证书图片");
        }

        // 2. 查询学生信息
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new NotFoundException("用户不存在");
        }

        // 3. 查询resourceKey列表
        List<String> resourceKeys = certificateMapper.getStudentCertificateImageKeys(studentId);
        if (resourceKeys.isEmpty()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }

        // 4. 创建ZIP
        String basePath = fileService.getUploadPath();
        String zipName = student.getStudentId() + "_" + student.getRealName() + ".zip";
        File zipFile = new File(System.getProperty("java.io.tmpdir"), zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String resourceKey : resourceKeys) {
                File imgFile = new File(basePath, resourceKey);
                if (imgFile.exists()) {
                    zos.putNextEntry(new ZipEntry(imgFile.getName()));
                    try (FileInputStream fis = new FileInputStream(imgFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        return zipFile;
    }

    @Override
    public File downloadInstructorCertificateImages(Long teacherId) {
        // 1. DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userId = AuthContext.getUserId();
        if (dataScope == DataScope.SELF && !userId.equals(teacherId)) {
            throw new ForbiddenException("无权限下载该教师的证书图片");
        }

        // 2. 查询教师信息
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            throw new NotFoundException("用户不存在");
        }

        // 3. 查询resourceKey列表（仅INSTRUCTOR类型）
        List<String> resourceKeys = certificateMapper.getInstructorCertificateImageKeys(teacherId);
        if (resourceKeys.isEmpty()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }

        // 4. 创建ZIP
        String basePath = fileService.getUploadPath();
        String zipName = teacher.getTeacherId() + "_" + teacher.getRealName() + ".zip";
        File zipFile = new File(System.getProperty("java.io.tmpdir"), zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String resourceKey : resourceKeys) {
                File imgFile = new File(basePath, resourceKey);
                if (imgFile.exists()) {
                    zos.putNextEntry(new ZipEntry(imgFile.getName()));
                    try (FileInputStream fis = new FileInputStream(imgFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        return zipFile;
    }

    @Override
    public File downloadCollegeCertificateImages(Long collegeId) {
        // 1. DataScope权限校验
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限导出该学院证书图片");
        }
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限导出学院证书图片");
        }

        // 2. 查询学院信息
        College college = collegeMapper.selectById(collegeId);
        if (college == null) {
            throw new NotFoundException("学院不存在");
        }

        // 3. 查询学生和教师的resourceKey列表
        List<String> studentKeys = certificateMapper.getCollegeStudentCertificateImageKeys(collegeId);
        List<String> instructorKeys = certificateMapper.getCollegeInstructorCertificateImageKeys(collegeId);
        if (studentKeys.isEmpty() && instructorKeys.isEmpty()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }

        // 4. 创建ZIP（含子文件夹）
        String basePath = fileService.getUploadPath();
        String zipName = collegeId + "_" + college.getCollegeName() + ".zip";
        File zipFile = new File(System.getProperty("java.io.tmpdir"), zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // 写入学生证书图片
            if (!studentKeys.isEmpty()) {
                zos.putNextEntry(new ZipEntry("学生证书/"));
                zos.closeEntry();
                for (String resourceKey : studentKeys) {
                    File imgFile = new File(basePath, resourceKey);
                    if (imgFile.exists()) {
                        zos.putNextEntry(new ZipEntry("学生证书/" + imgFile.getName()));
                        try (FileInputStream fis = new FileInputStream(imgFile)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }

            // 写入教师证书图片
            if (!instructorKeys.isEmpty()) {
                zos.putNextEntry(new ZipEntry("教师证书/"));
                zos.closeEntry();
                for (String resourceKey : instructorKeys) {
                    File imgFile = new File(basePath, resourceKey);
                    if (imgFile.exists()) {
                        zos.putNextEntry(new ZipEntry("教师证书/" + imgFile.getName()));
                        try (FileInputStream fis = new FileInputStream(imgFile)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        return zipFile;
    }

    @Override
    public File downloadAllStudentCertificateImages() {
        // DataScope权限校验
        if (AuthContext.getDataScope() != DataScope.ALL) {
            throw new ForbiddenException("无权限导出全部学生证书图片");
        }

        // 查询所有学生的证书图片
        List<CollegeCertImageVO> images = certificateMapper.getAllStudentCertificateImages();
        if (images.isEmpty()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }

        // 按collegeId分组
        Map<Long, List<CollegeCertImageVO>> grouped = images.stream()
                .collect(Collectors.groupingBy(CollegeCertImageVO::getCollegeId));

        String basePath = fileService.getUploadPath();
        String zipName = "全部学生证书.zip";
        File zipFile = new File(System.getProperty("java.io.tmpdir"), zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Map.Entry<Long, List<CollegeCertImageVO>> entry : grouped.entrySet()) {
                List<CollegeCertImageVO> group = entry.getValue();
                String folderName = entry.getKey() + "_" + group.get(0).getCollegeName() + "/";

                zos.putNextEntry(new ZipEntry(folderName));
                zos.closeEntry();

                for (CollegeCertImageVO img : group) {
                    File imgFile = new File(basePath, img.getResourceKey());
                    if (imgFile.exists()) {
                        zos.putNextEntry(new ZipEntry(folderName + imgFile.getName()));
                        try (FileInputStream fis = new FileInputStream(imgFile)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        return zipFile;
    }

    @Override
    public File downloadAllTeacherCertificateImages() {
        // DataScope权限校验
        if (AuthContext.getDataScope() != DataScope.ALL) {
            throw new ForbiddenException("无权限导出全部教师证书图片");
        }

        // 查询所有教师的证书图片
        List<CollegeCertImageVO> images = certificateMapper.getAllInstructorCertificateImages();
        if (images.isEmpty()) {
            throw new BusinessException(ResultCode.CERT_IMAGE_NOT_FOUND);
        }

        // 按collegeId分组
        Map<Long, List<CollegeCertImageVO>> grouped = images.stream()
                .collect(Collectors.groupingBy(CollegeCertImageVO::getCollegeId));

        String basePath = fileService.getUploadPath();
        String zipName = "全部教师证书.zip";
        File zipFile = new File(System.getProperty("java.io.tmpdir"), zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Map.Entry<Long, List<CollegeCertImageVO>> entry : grouped.entrySet()) {
                List<CollegeCertImageVO> group = entry.getValue();
                String folderName = entry.getKey() + "_" + group.get(0).getCollegeName() + "/";

                zos.putNextEntry(new ZipEntry(folderName));
                zos.closeEntry();

                for (CollegeCertImageVO img : group) {
                    File imgFile = new File(basePath, img.getResourceKey());
                    if (imgFile.exists()) {
                        zos.putNextEntry(new ZipEntry(folderName + imgFile.getName()));
                        try (FileInputStream fis = new FileInputStream(imgFile)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        return zipFile;
    }

    // ==================== 统计操作 ====================

    @Override
    public CertificateStatsVO getMyInstructorCertStats() {
        Long userId = AuthContext.getUserId();
        return CertificateStatsVO.fromStatusCountList(
                certificateMapper.countInstructorCertsByTeacher(userId));
    }

    @Override
    public Long getMyGuidedStudentCount() {
        Long userId = AuthContext.getUserId();
        Long count = certificateMapper.countDistinctStudentsByTeacher(userId);
        return count != null ? count : 0L;
    }

    @Override
    public CertificateStatsVO getMyStudentCertStats() {
        Long userId = AuthContext.getUserId();
        return CertificateStatsVO.fromStatusCountList(
                certificateMapper.countStudentCertsByStudent(userId));
    }

    @Override
    public CertificateStatsVO getCollegeCertStats(Long collegeId, CertType certType) {
        // 权限校验：COLLEGE_ADMIN 只能查自己学院，ALL 可查任意
        DataScope dataScope = AuthContext.getDataScope();
        Long userCollegeId = AuthContext.getCollegeId();
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new ForbiddenException("无权限查看该学院统计");
        }
        if (dataScope != DataScope.ALL && dataScope != DataScope.COLLEGE) {
            throw new ForbiddenException("无权限查看学院统计");
        }
        return CertificateStatsVO.fromStatusCountList(
                certificateMapper.countCertsByCollege(collegeId, certType.name()));
    }

    @Override
    public CertificateStatsVO getAllCertStats(CertType certType) {
        // 权限校验：仅超管可访问
        if (AuthContext.getDataScope() != DataScope.ALL) {
            throw new ForbiddenException("无权限查看全部统计");
        }
        return CertificateStatsVO.fromStatusCountList(
                certificateMapper.countCertsByType(certType.name()));
    }
}
