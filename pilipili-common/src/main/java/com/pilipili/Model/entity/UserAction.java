package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户行为 点赞、评论
 * @author 15712
 * @TableName UserAction
 */
@TableName(value ="UserAction")
@Data
public class UserAction implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Integer actionId;

    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 视频用户ID
     */
    private String videoUserId;

    /**
     * 评论ID
     */
    private Integer commentId;

    /**
     * 0:评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币
     */
    private Integer actionType;

    /**
     * 数量
     */
    private Integer actionCount;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 操作时间
     */
    private Date actionTime;


    /**
     * 视频封面
     */
    @TableField(exist = false)
    private String videoCover;

    /**
     * 视频名称
     */
    @TableField(exist = false)
    private String videoName;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    private String otherNickName;


    /**
     * 创建时间
     */
    @TableField(exist = false)
    private Date videoCreateTime;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}