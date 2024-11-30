package com.pilipili.Model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;


    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 验证码
     */
    private String checkCode;


    /**
     * 验证码的key
     */
    private String checkCodeKey;
}
