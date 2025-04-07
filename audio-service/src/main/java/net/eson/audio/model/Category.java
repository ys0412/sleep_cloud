package net.eson.audio.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Eson
 * @date 2025年04月02日 14:14
 */
@Data
@TableName(value = "category")
public class Category {
    @TableId
    private Integer id;           // 分类ID
    private String categoryName;  // 分类标识名称（用于程序内部标识）
    private Date createdAt;       // 创建时间
    private Date updatedAt;       // 更新时间
}
    