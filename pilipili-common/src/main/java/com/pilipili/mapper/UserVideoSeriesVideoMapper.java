package com.pilipili.mapper;

import com.pilipili.Model.entity.UserVideoSeriesVideo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 15712
* @description 针对表【UserVideoSeriesVideo】的数据库操作Mapper
* @createDate 2025-01-01 13:16:21
* @Entity com.pilipili.Model.entity.UserVideoSeriesVideo
*/
public interface UserVideoSeriesVideoMapper extends BaseMapper<UserVideoSeriesVideo> {

    Integer selectMaxSort(@Param("userId") String userId);

    List<UserVideoSeriesVideo> getDetailSeriesVideo(@Param("seriesId") Integer seriesId);
}




