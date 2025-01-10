package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户消息表
 * @TableName UserMessage
 */
@TableName(value ="UserMessage")
@Data
public class UserMessage implements Serializable {
    /**
     * 消息ID号
     */
    @TableId(type = IdType.AUTO)
    private Integer messageId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 主体ID
     */
    private String videoId;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 发送人ID
     */
    private String sendUserId;

    /**
     * 0:未读 1:已读
     */
    private Integer readType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 扩展信息
     */
    private String extendJson;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}