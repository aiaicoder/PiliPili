package com.pilipili.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 15712
* @description 针对表【UserMessage(用户消息表)】的数据库操作Mapper
* @createDate 2025-01-09 21:44:58
* @Entity com.pilipili.Model.entity.UserMessage
*/
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    Page<UserMessage> loadMessageByType(@Param("page") Page<UserMessage> page, @Param("userId") String userId, @Param("messageType") Integer messageType);
}




