package cn.guet.competition_cert_system.common;

/**
 * 证书数据可见范围
 * 描述"能看到什么数据"
 */
public enum DataScope {

    /**
     * 所有数据 - 超级管理员
     */
    ALL,

    /**
     * 本学院数据 - 学院管理员、审核员
     */
    COLLEGE,

    /**
     * 关联数据 - 教师（自己的 + 指导的学生）
     */
    RELATED,

    /**
     * 仅自己相关的证书 - 学生
     */
    SELF
}
