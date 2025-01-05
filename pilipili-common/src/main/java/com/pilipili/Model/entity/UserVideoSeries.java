package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户视频序列归档
 * @author 15712
 * @TableName UserVideoSeries
 */
@TableName(value ="UserVideoSeries")
@Data
public class UserVideoSeries implements Serializable {
    /**
     * 列表ID
     */
    @TableId(type = IdType.AUTO)
    private Integer seriesId;

    /**
     * 列表名称
     */
    private String seriesName;

    /**
     * 描述
     */
    private String seriesDescription;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(exist = false)
    private Integer videoCover;

    @TableField(exist = false)
    private List<VideoInfo> videoInfoList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}