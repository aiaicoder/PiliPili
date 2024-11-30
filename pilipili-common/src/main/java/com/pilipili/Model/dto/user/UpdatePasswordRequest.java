package com.pilipili.Model.dto.user;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/11 19:31
 */
@Data
public class UpdatePasswordRequest {
    /**
     * 新密码
     */
    private String password;

    /**
     * 校验密码
     */
    private String checkPassword;
}
