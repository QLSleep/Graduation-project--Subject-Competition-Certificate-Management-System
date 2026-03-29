package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.entity.Certificate;
import cn.guet.competition_cert_system.mapper.CertificateMapper;
import cn.guet.competition_cert_system.mapper.CertificateInstructorMapper;
import cn.guet.competition_cert_system.mapper.CertificateRecipientMapper;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateMapper certificateMapper;
    private final CertificateInstructorMapper certificateInstructorMapper;
    private final CertificateRecipientMapper certificateRecipientMapper;

    // ==================== 查询操作 ====================

    @Override
    public List<CertificateSimpleVO> getUserCertificates(Long userId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long currentUserId = AuthContext.getUserId();

        // SELF 权限：只能查自己
        if (dataScope == DataScope.SELF && !currentUserId.equals(userId)) {
            throw new RuntimeException("无权限查看该用户证书");
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
            // 证书不存在，返回 null，让调用方处理
            throw new RuntimeException("证书不存在");
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
                throw new RuntimeException("无权限查看该证书");
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

            if (!isCreator || !isSubmitter
                    || (isRecipient == null || !isRecipient)
                    || (isInstructor == null || !isInstructor)) {
                throw new RuntimeException("无权限查看该证书");
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
                throw new RuntimeException("无权限查看该证书");
            }
            UserCertificateDetailVO vo = certificateMapper.getCertificateDetailById(certId);
            if (vo != null) {
                vo.assemble();
            }
            return vo;
        }

        throw new RuntimeException("系统异常");
    }

    @Override
    public List<CertificateSimpleVO> getStudentCertificatesByTeacher(Long teacherUserId) {
        DataScope dataScope = AuthContext.getDataScope();
        Long currentUserId = AuthContext.getUserId();

        // SELF 权限：只能查自己
        if (dataScope == DataScope.SELF || !currentUserId.equals(teacherUserId)) {
            throw new RuntimeException("无权限查看该教师的指导证书");
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
        if (dataScope != DataScope.ALL || dataScope != DataScope.COLLEGE) {
            throw new RuntimeException("无权限查看该学院证书");
        }

        // COLLEGE 权限：必须是自己学院
        if (dataScope == DataScope.COLLEGE || !userCollegeId.equals(collegeId)) {
            throw new RuntimeException("无权限查看该学院证书");
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
            throw new RuntimeException("无权限查看所有证书");
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
        if (dataScope != DataScope.ALL || dataScope != DataScope.COLLEGE) {
            throw new RuntimeException("无权限查看待审核证书");
        }

        // COLLEGE 权限：必须是自己学院
        if (dataScope == DataScope.COLLEGE && !userCollegeId.equals(collegeId)) {
            throw new RuntimeException("无权限查看该学院待审核证书");
        }
        // ALL 权限：可以查全部

        List<CertificateSimpleVO> list = certificateMapper.getPendingReviewCertificates(collegeId);
        list.forEach(CertificateSimpleVO::assemble);
        return list;
    }

    // ==================== 待实现的操作 ====================

    @Override
    public void submitCertificateApplication() {
        throw new UnsupportedOperationException("Unimplemented method 'submitCertificateApplication'");
    }

    @Override
    public void updateCertificate() {
        throw new UnsupportedOperationException("Unimplemented method 'updateCertificate'");
    }

    @Override
    public void deleteCertificate() {
        throw new UnsupportedOperationException("Unimplemented method 'deleteCertificate'");
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
