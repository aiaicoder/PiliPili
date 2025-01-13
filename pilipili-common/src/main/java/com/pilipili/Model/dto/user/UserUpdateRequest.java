package com.pilipili.Model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private String userId;

    /**
     * 用户角色：user/admin/ban
     */
    private Integer status;




    private static final long serialVersionUID = 1L;
}