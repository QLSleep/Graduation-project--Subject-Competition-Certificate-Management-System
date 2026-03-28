package cn.guet.competition_cert_system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 赛类表（赛道类别，如软件类、嵌入式类、人工智能类等）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("competition_category")
public class CompetitionCategory {

    @TableId
    private Long id;

    /**
     * 所属赛事ID
     */
    private Long eventId;

    /**
     * 赛类名称，如：软件类、嵌入式类、人工智能类
     */
    private String categoryName;

    /**
     * 赛类描述
     */
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
