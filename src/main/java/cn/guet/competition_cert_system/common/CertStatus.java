package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertStatus {

  VALID(1001, "有效"),
  INVALID(1002, "无效"),
  REISSUED(1003, "重新签发"),
  DRAFT(1004, "草稿");

  private final int code;
  private final String desc;
}
