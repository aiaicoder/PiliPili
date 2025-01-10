package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 视频播放历史
 * @TableName VideoPlayHistory
 */
@TableName(value ="VideoPlayHistory")
@Data
public class VideoPlayHistory implements Serializable {
    /**
     * 用户ID
     */
    @TableId
    private String userId;

    /**
     * 视频ID
     */
    @TableId
    private String videoId;

    /**
     * 子集索引
     */
    private Integer fileIndex;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}