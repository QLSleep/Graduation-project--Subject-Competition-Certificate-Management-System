package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {
  SUCCESS(20000, "操作成功"),
  PARAM_ERROR(40000, "参数错误"),
  UNAUTHORIZED(40100, "未登录或Token已过期"),
  FORBIDDEN(40300, "权限不足"),
  NOT_FOUND(40400, "资源不存在"),
  BUSINESS_ERROR(50000, "业务异常"),
  SYSTEM_ERROR(50001, "系统异常，请稍后重试"),
  TOKEN_EXPIRED(40101, "Token已过期，请重新登录"),
  REFRESH_TOKEN_INVALID(40102, "Refresh Token无效"),

  // === 业务错误（50010+）===
  CERT_NUMBER_DUPLICATED(50010, "证书编号已存在"),
  USER_NOT_FOUND(50011, "用户不存在"),
  STUDENT_NOT_FOUND(50012, "学号不存在"),
  TEACHER_NOT_FOUND(50013, "工号不存在"),
  CERT_NOT_FOUND(50014, "证书不存在"),
  ROLE_NOT_CONFIGURED(50015, "该角色未配置权限"),
  USER_NO_ROLE(50016, "该用户未分配角色"),
  TOKEN_INVALID(50017, "Token无效或已过期"),
  REFRESH_TOKEN_EXPIRED(50018, "Refresh Token无效或已过期"),
  UNAUTHORIZED_ERROR(50019, "未授权");

  private final int code;
  private final String message;
}
