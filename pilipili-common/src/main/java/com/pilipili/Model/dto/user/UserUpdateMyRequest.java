package com.pilipili.Model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 用户生日
     */
    private String birthday;

    /**
     * 用户学校
     */
    private String school;

    /**
     * 用户通告
     */
    private String noticeInfo;

    private static final long serialVersionUID = 1L;
}