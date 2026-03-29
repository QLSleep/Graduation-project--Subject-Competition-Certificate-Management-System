package cn.guet.competition_cert_system.utils;

import cn.guet.competition_cert_system.common.ResultCode;
import lombok.Data;

@Data
public class Result<T> {
  private int code;
  private String message;
  private T data;

  // 成功，带数据
  public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setCode(ResultCode.SUCCESS.getCode());
    result.setMessage(ResultCode.SUCCESS.getMessage());
    result.setData(data);
    return result;
  }

  // 成功，无数据
  public static <T> Result<T> success() {
    return success(null);
  }

  // 失败，使用枚举消息
  public static <T> Result<T> error(ResultCode resultCode) {
    Result<T> result = new Result<>();
    result.setCode(resultCode.getCode());
    result.setMessage(resultCode.getMessage());
    return result;
  }

  // 失败，自定义消息
  public static <T> Result<T> error(int code, String message) {
    Result<T> result = new Result<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  // 失败，自定义消息（默认500状态码）
  public static <T> Result<T> error(String message) {
    return error(ResultCode.BUSINESS_ERROR.getCode(), message);
  }
}
