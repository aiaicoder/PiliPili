package com.pilipili.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.Model.dto.Category.CategoryQueryRequest;
import com.pilipili.Model.entity.CategoryInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 15712
* @description 针对表【CategoryInfo(分类信息)】的数据库操作Service
* @createDate 2024-11-25 22:31:13
*/
public interface CategoryInfoService extends IService<CategoryInfo> {

     Boolean saveOrUpdateCategory(CategoryInfo categoryInfo);

    Boolean deleteCategory(Integer categoryIdOrPid);


    List<CategoryInfo> getCategoryList(CategoryQueryRequest categoryQueryRequest);
    List<CategoryInfo> getCategoryListWeb();

    Boolean reorderCategory(Integer pCategoryId,String categoryIds);
}
