package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 证书类型枚举
 * 数据库存储常量名：INSTRUCTOR, STUDENT
 */
@Getter
@AllArgsConstructor
public enum CertType {
    INSTRUCTOR("指导教师证书"),
    STUDENT("学生证书");

    private final String desc;
}
