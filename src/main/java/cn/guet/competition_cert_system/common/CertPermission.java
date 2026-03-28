package cn.guet.competition_cert_system.common;

/**
 * 证书查询权限级别
 */
public enum CertPermission {

    /**
     * 所有证书 - 管理员
     */
    ALL,

    /**
     * 本学院证书 - 学院管理员
     */
    COLLEGE,

    /**
     * 关联证书 - 教师/学生/提交者/审核者
     */
    RELATED,

    /**
     * 仅自己相关的证书
     */
    SELF
}
