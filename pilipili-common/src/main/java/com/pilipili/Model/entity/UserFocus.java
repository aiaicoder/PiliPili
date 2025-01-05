package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName UserFocus
 */
@TableName(value ="UserFocus")
@Data
public class UserFocus implements Serializable {
    /**
     * 用户ID
     */
    @TableId
    private String userId;

    /**
     * 用户ID
     */
    private String focusUserId;

    /**
     * 
     */
    private Date focusTime;

    @TableField(exist = false)
    private String otherNickName;

    @TableField(exist = false)
    private String otherUserAvatar;

    @TableField(exist = false)
    private String otherUserProfile;

    @TableField(exist = false)
    private String otherUserId;

    @TableField(exist = false)
    private Integer focusType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}