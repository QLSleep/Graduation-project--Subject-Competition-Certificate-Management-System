package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.CertificateMapper;
import cn.guet.competition_cert_system.service.CertificateService;
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
  public void getUserCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUserCertificates'");
  }

  @Override
  public void getCertificateDetail() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCertificateDetail'");
  }

  @Override
  public void getStudentCertificatesByTeacher() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStudentCertificatesByTeacher'");
  }

  @Override
  public void getCollegeCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCollegeCertificates'");
  }

  @Override
  public void getAllCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllCertificates'");
  }

  @Override
  public void getPendingReviewCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPendingReviewCertificates'");
  }

  @Override
  public void submitCertificateApplication() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'submitCertificateApplication'");
  }

  @Override
  public void updateCertificate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateCertificate'");
  }

  @Override
  public void deleteCertificate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteCertificate'");
  }

  @Override
  public void uploadCertificateImage() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'uploadCertificateImage'");
  }

  @Override
  public void reviewCertificate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'reviewCertificate'");
  }

  @Override
  public void exportCollegeCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'exportCollegeCertificates'");
  }

  @Override
  public void exportAllCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'exportAllCertificates'");
  }

  @Override
  public void exportUserCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'exportUserCertificates'");
  }

  @Override
  public void exportMultipleUsersCertificates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'exportMultipleUsersCertificates'");
  }

}
