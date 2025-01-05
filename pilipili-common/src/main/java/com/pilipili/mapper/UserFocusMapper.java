package com.pilipili.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserFocus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 15712
* @description 针对表【UserFocus】的数据库操作Mapper
* @createDate 2025-01-01 13:16:30
* @Entity com.pilipili.Model.entity.UserFocus
*/
public interface UserFocusMapper extends BaseMapper<UserFocus> {

    Page<UserFocus> getFansList(Page<UserFocus> userFocusPage, @Param("userId") String userId, @Param("queryType")Integer queryType);
}




