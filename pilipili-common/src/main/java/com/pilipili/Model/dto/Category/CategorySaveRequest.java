package com.pilipili.Model.dto.Category;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/11 19:31
 */
@Data
public class CategorySaveRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 背景
     */
    private String background;
    /**
     * 目录id
     */
    private Integer categoryId;
    /**
     * 对应的icon图标
     */
    private String icon;
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
}
