package com.pilipili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pilipili.Model.entity.UserInfo;
import org.apache.ibatis.annotations.Param;


/**
* @author 15712
* @description 针对表【userInfo(用户信息表)】的数据库操作Mapper
* @createDate 2024-11-16 15:09:58
* @Entity generator.domain.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    Integer updateCountInfo(@Param("userId") String userId, @Param("changeCount")Integer changeCount);
}




