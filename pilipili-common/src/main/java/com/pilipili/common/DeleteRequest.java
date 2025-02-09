package com.pilipili.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Data
public class DeleteRequest implements Serializable {


    /**
     * id
     */
    private String id;

    /**
     * email
     */
    private String email;

    private static final long serialVersionUID = 1L;
}