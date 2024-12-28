package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 视频弹幕
 * @author 15712
 * @TableName VideoDanMu
 */
@TableName(value ="VideoDanMu")
@Data
public class VideoDanMu implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Integer danMuId;

    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 唯一ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 发布时间
     */
    private Date postTime;

    /**
     * 内容
     */
    private String text;

    /**
     * 展示位置
     */
    private Integer mode;

    /**
     * 颜色
     */
    private String color;

    /**
     * 展示时间
     */
    private Integer time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}