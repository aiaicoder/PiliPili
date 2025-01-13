package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频播放历史
 *
 * @author 15712
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
    private String videoName;

    @TableField(exist = false)
    private String videoCover;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    private Integer duration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}