package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 证书状态枚举
 * 数据库存储字符串常量
 */
@Getter
@AllArgsConstructor
public enum CertStatus {

    DRAFT("草稿未提交"),
    PENDING_REVIEW("待审核"),
    REJECTED("驳回-可重提"),
    REJECTED_NO_RESUBMIT("驳回-不可重提"),
    APPROVED("已通过"),
    REISSUED("已补发"),
    INVALID("已作废");

    private final String desc;
}
