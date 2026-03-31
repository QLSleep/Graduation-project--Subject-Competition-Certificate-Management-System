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
  UNAUTHORIZED_ERROR(50019, "未授权"),

  // === 审核错误（50020+）===
  CERT_STATUS_NOT_ALLOWED(50020, "证书状态不允许此操作"),
  CERT_NOT_PENDING_REVIEW(50021, "证书不在待审核状态"),
  CERT_NOT_APPROVED(50022, "证书未通过审核"),
  CERT_ALREADY_REVIEWED(50023, "证书已审核"),
  REVIEW_COMMENT_REQUIRED(50024, "审核备注不能为空"),

  // === 图片错误（50025+）===
  CERT_IMAGE_NOT_FOUND(50025, "证书图片不存在"),
  CERT_IMAGE_NOT_UPLOADED(50026, "证书图片未上传"),

  // === 用户错误（50030+）===
  USERNAME_EXISTS(50030, "用户名已存在"),
  STUDENT_ID_EXISTS(50031, "学号已存在"),
  TEACHER_ID_EXISTS(50032, "工号已存在"),
  PASSWORD_MISMATCH(50033, "两次密码不一致"),
  INVALID_USER_TYPE(50034, "无效的用户类型"),
  VERIFY_CODE_ERROR(50035, "验证码错误或已过期"),
  COLLEGE_ADMIN_CANNOT_CREATE_ADMIN(50036, "院管无权创建管理员账户");

  private final int code;
  private final String message;
}
