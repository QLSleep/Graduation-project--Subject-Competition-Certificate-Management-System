package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.DataScope;
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
    public List<Object> getUserCertificates(Long userId, DataScope dataScope) {
        List<UserCertificateDetailVO> fullList = certificateMapper.getUserCertificates(userId);
        fullList.forEach(UserCertificateDetailVO::assemble);

        if (isHighPermission(dataScope)) {
            return new ArrayList<>(fullList);
        } else {
            return fullList.stream()
                    .map(this::convertToSimple)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Object getCertificateDetail(Long certId, DataScope dataScope) {
        UserCertificateDetailVO fullVO = certificateMapper.getCertificateDetailById(certId);

        if (fullVO == null) {
            return null;
        }

        fullVO.assemble();

        if (isHighPermission(dataScope)) {
            return fullVO;
        } else {
            return convertToSimple(fullVO);
        }
    }

    /**
     * 判断是否为高级数据范围（可查看完整信息）
     */
    private boolean isHighPermission(DataScope dataScope) {
        return dataScope == DataScope.ALL || dataScope == DataScope.COLLEGE;
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
        simple.setEventName(full.getEventName());
        simple.setSessionName(full.getSessionName());
        simple.setGroupName(full.getGroupName());
        simple.setCategoryName(full.getCategoryName());
        simple.setLevel(full.getLevel());
        simple.setCertTitle(full.getCertTitle());
        simple.setAwardLevel(full.getAwardLevel());
        simple.setIssuDate(full.getIssuDate());
        simple.setResourceKey(full.getResourceKey());
        simple.setStatus(full.getStatus());
        simple.setRemark(full.getRemark());
        simple.setRecipients(full.getRecipients());
        simple.setInstructors(full.getInstructors());
        return simple;
    }

    @Override
    public List<Object> getStudentCertificatesByTeacher(Long teacherUserId, DataScope dataScope) {
        List<UserCertificateDetailVO> fullList = certificateMapper.getStudentCertificatesByTeacher(teacherUserId);
        fullList.forEach(UserCertificateDetailVO::assemble);

        if (isHighPermission(dataScope)) {
            return new ArrayList<>(fullList);
        } else {
            return fullList.stream()
                    .map(this::convertToSimple)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<UserCertificateDetailVO> getCollegeCertificates(Long collegeId) {
        // TODO [权限认证] 补充时机：拦截器/Controller 层权限框架搭建完成后
        //   - RELATED/SELF 权限：拦截器层直接拒绝
        //   - COLLEGE 权限：拦截器层校验当前用户是否属于该 collegeId
        //   - ALL 权限：拦截器层通过
        // Service 层不做权限判断，只负责业务逻辑
        List<UserCertificateDetailVO> fullList = certificateMapper.getCollegeCertificates(collegeId);
        fullList.forEach(UserCertificateDetailVO::assemble);
        return fullList;
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
