package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertType {
  INSTRUCTOR(111, "指导教师证书"),
  STUDENT(222, "学生证书");

  private final int code; // 类型码
  private final String desc; // 类型描述
}
