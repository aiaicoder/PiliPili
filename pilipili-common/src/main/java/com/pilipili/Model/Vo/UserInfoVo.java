package com.pilipili.Model.Vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Data
public class UserInfoVo implements Serializable {

    /**
     * userId
     */
    @TableId
    private String userId;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 1：男,0:女,2:未知
     */
    private Integer sex;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 学校
     */
    private String school;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 最后登录ip
     */
    private String lastLoginIp;

    /**
     * 空间通告
     */
    private String noticeInfo;

    /**
     * 用户状态 0:禁用，1:正常
     */
    private Integer status;

    /**
     * 总金币数量
     */
    private Integer totalCoinCount;

    /**
     * 等级
     */
    private String grade;



    /**
     * 当前金币数量
     */
    private Integer currentCoinCount;


    /**
     * 粉丝数
     */
    private Integer fansCount;

    /**
     * 关注数
     */
    private Integer focusCount;

    private Integer likeCount;

    private Integer playCount;

    private Boolean havaFocus;

    /**
     * 主题
     */
    private Integer theme;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}