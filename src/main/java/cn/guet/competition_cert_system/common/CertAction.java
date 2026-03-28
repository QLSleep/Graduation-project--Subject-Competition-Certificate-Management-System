package cn.guet.competition_cert_system.common;

/**
 * 证书操作动作权限
 * 描述"能做什么操作"
 */
public enum CertAction {

    /**
     * 创建证书
     */
    CREATE,

    /**
     * 编辑证书
     */
    EDIT,

    /**
     * 删除/作废证书
     */
    DELETE,

    /**
     * 审核（通过/驳回）
     */
    AUDIT,

    /**
     * 补发
     */
    REISSUE,

    /**
     * 导出
     */
    EXPORT
}
