package com.pilipili.Model.dto.Category;


import com.pilipili.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryDeleteRequest extends PageRequest implements Serializable {
    /**
     * 目录id
     */
    private Integer categoryIdOrPid;


    private static final long serialVersionUID = 1L;
}