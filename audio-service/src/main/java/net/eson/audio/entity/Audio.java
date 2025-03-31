package net.eson.audio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Eson
 * @date 2025年03月31日 17:33
 */
@Data
@TableName(value = "audio")
public class Audio {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String url;
    private Integer duration;
    private String category;
    private Integer sortOrder;
    private Integer playCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
    