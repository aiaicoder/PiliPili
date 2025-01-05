package com.pilipili.Model.dto.user;

import com.pilipili.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/2 16:19
 */
@Data
public class UserFocusFanRequest extends PageRequest implements Serializable {
    private Integer queryType;
}
