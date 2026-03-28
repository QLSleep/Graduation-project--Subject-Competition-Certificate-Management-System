package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.CertPermission;
import cn.guet.competition_cert_system.mapper.CertificateMapper;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Certificate 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateMapper certificateMapper;

    @Override
    public List<Object> getUserCertificates(Long userId, CertPermission permission) {
        // 查询完整的证书详情
        List<UserCertificateDetailVO> fullList = certificateMapper.getUserCertificates(userId);

        // 根据权限决定返回类型
        if (isHighPermission(permission)) {
            // ALL/COLLEGE: 返回完整VO
            return new ArrayList<>(fullList);
        } else {
            // RELATED/SELF: 转换为精简VO
            return fullList.stream()
                    .map(this::convertToSimple)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Object getCertificateDetail(Long certId, CertPermission permission) {
        UserCertificateDetailVO fullVO = certificateMapper.getCertificateDetailById(certId);

        if (fullVO == null) {
            return null;
        }

        if (isHighPermission(permission)) {
            return fullVO;
        } else {
            return convertToSimple(fullVO);
        }
    }

    /**
     * 判断是否为高级权限（可查看完整信息）
     */
    private boolean isHighPermission(CertPermission permission) {
        return permission == CertPermission.ALL || permission == CertPermission.COLLEGE;
    }

    /**
     * 将完整VO转换为精简VO
     */
    private CertificateSimpleVO convertToSimple(UserCertificateDetailVO full) {
        CertificateSimpleVO simple = new CertificateSimpleVO();
        simple.setId(full.getId());
        simple.setCertType(full.getCertType());
        simple.setCertNumber(full.getCertNumber());
        simple.setCollegeName(full.getCollegeName());
        // 敏感字段已省略：creatorName, submitterName, reviewerName
        simple.setEventName(full.getEventName());
        simple.setSessionName(full.getSessionName());
        simple.setGroupName(full.getGroupName());
        simple.setCertTitle(full.getCertTitle());
        simple.setAwardLevel(full.getAwardLevel());
        simple.setIssuDate(full.getIssuDate());
        simple.setResourceKey(full.getResourceKey());
        simple.setStatus(full.getStatus());
        simple.setRemark(full.getRemark());
        // 敏感字段已省略：createdAt, updatedAt
        simple.setRecipients(full.getRecipients());
        simple.setInstructor(full.getInstructor());
        return simple;
    }

    @Override
    public void getStudentCertificatesByTeacher() {
        throw new UnsupportedOperationException("Unimplemented method 'getStudentCertificatesByTeacher'");
    }

    @Override
    public void getCollegeCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'getCollegeCertificates'");
    }

    @Override
    public void getAllCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllCertificates'");
    }

    @Override
    public void getPendingReviewCertificates() {
        throw new UnsupportedOperationException("Unimplemented method 'getPendingReviewCertificates'");
    }

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
