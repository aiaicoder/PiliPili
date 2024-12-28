package com.pilipili.Model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/28 19:22
 */
@Data
public class UserActionRequest implements Serializable {


    private String videoId;

    /**
     * 操作类型
     */
    private Integer actionType;


    private Integer actionCount;

    /**
     * 评论Id
     */
    private Integer commentId;

}
