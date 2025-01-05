package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author 15712
 * @TableName UserVideoSeriesVideo
 */
@TableName(value ="UserVideoSeriesVideo")
@Data
public class UserVideoSeriesVideo implements Serializable {
    /**
     * 列表ID
     */
    @TableId
    private Integer seriesId;

    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 排序
     */
    private Integer sort;

    @TableField(exist = false)
    private String videName;

    @TableField(exist = false)
    private String videoCover;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date videoCreateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}