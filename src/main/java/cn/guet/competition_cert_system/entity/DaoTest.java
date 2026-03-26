package cn.guet.competition_cert_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DAO层专用测试表实体类
 * 完全独立，无任何关联，仅供测试使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dao_test")
public class DaoTest {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 测试唯一编码，用于测试唯一约束
     */
    private String testCode;

    /**
     * 测试名称
     */
    private String testName;

    /**
     * 测试值
     */
    private Integer testValue;

    /**
     * 是否激活（默认true）
     */
    private Boolean isActive;

    /**
     * 测试枚举类型（TYPE_A、TYPE_B、TYPE_C）
     */
    private String testType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间（与 PostgreSQL TIMESTAMPTZ 兼容）
     */
    private Instant createdAt;

    /**
     * 更新时间（与 PostgreSQL TIMESTAMPTZ 兼容）
     */
    private Instant updatedAt;
}
