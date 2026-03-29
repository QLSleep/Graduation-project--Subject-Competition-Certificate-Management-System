package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 证书审核动作枚举
 * 数据库存储字符串常量
 */
@Getter
@AllArgsConstructor
public enum CertAuditAction {

    SUBMIT("提交"),
    REVIEW("审核"),
    REJECT("驳回"),
    APPROVE("通过"),
    REISSUE("补发"),
    INVALID("作废");

    private final String desc;
}
