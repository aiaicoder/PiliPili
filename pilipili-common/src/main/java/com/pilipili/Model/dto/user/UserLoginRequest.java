package com.pilipili.Model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String email;

    private String password;

    private String checkCode;

    private String checkCodeKey;

    private Boolean rememberMe;
}
