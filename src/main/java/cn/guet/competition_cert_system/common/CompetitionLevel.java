package cn.guet.competition_cert_system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 赛事级别枚举
 * 数据库存储英文常量名：SCHOOL, CITY, PROVINCIAL, NATIONAL, GLOBAL
 */
@Getter
@AllArgsConstructor
public enum CompetitionLevel {
    SCHOOL("校级"),
    CITY("市级"),
    PROVINCIAL("省级"),
    NATIONAL("国家级"),
    GLOBAL("全球");

    private final String desc;
}
