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
  TOKEN_EXPIRED(40101, "Token已过期，请重新登录"), // 双Token用
  REFRESH_TOKEN_INVALID(40102, "Refresh Token无效");

  private final int code;
  private final String message;
}
