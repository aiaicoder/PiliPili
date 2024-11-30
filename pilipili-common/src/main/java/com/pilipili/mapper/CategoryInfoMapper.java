package com.pilipili.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pilipili.Model.entity.CategoryInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* @author 15712
* @description 针对表【CategoryInfo(分类信息)】的数据库操作Mapper
* @createDate 2024-11-25 22:31:13
* @Entity generator.domain.CategoryInfo
*/
public interface CategoryInfoMapper extends BaseMapper<CategoryInfo> {


    Integer getMaxSortId(@Param("pCategoryId") Integer pCategoryId);

    void updateSortBatch(@Param("categoryList") List<CategoryInfo> categoryInfos);

}




