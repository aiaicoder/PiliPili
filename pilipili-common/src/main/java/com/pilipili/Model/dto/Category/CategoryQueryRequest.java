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
public class CategoryQueryRequest extends PageRequest implements Serializable {
    /**
     * 目录id
     */
    private Integer categoryId;

    /**
     * 父目录id
     */
    private Integer pCategoryId;

    /**
     * 目录名称
     */
    private String categoryName;

    /**
     * 目录编码
     */
    private String categoryCode;

    /**
     * 树形结构
     */
    private Boolean covertToTree;


    private static final long serialVersionUID = 1L;
}