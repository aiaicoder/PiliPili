package com.pilipili.mapper;

import com.pilipili.Model.entity.UserVideoSeries;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
* @author 15712
* @description 针对表【UserVideoSeries(用户视频序列归档)】的数据库操作Mapper
* @createDate 2025-01-01 13:16:26
* @Entity com.pilipili.Model.entity.UserVideoSeries
*/
public interface UserVideoSeriesMapper extends BaseMapper<UserVideoSeries> {

    Integer selectMaxSort(@Param("userId") String userId);

    List<UserVideoSeries> getUserVideoSeries(@Param("userId") String userId);

    void changeVideoSeriesSort(@Param("seriesIdList") List<UserVideoSeries> userVideoSeries);

    List<UserVideoSeries> selectSeriesWithVideo(@Param("userId") String userId);
}




