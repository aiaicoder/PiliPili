package com.pilipili.mapper;

import com.pilipili.Model.entity.StatisticsInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Key;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author 15712
* @description 针对表【StatisticsInfo(数据统计)】的数据库操作Mapper
* @createDate 2025-01-09 21:44:36
* @Entity com.pilipili.Model.entity.StatisticsInfo
*/
public interface StatisticsInfoMapper extends BaseMapper<StatisticsInfo> {

    List<StatisticsInfo> statisticUserFocus(@Param("statisticDate") String statisticDate);
    List<StatisticsInfo> statisticUserComment(@Param("statisticDate") String statisticDate);
    List<StatisticsInfo> selectStatisticInfo(@Param("statisticDate") String statisticDate, @Param("actionTypeArray") Integer[] actionTypeArray);
    void insertOrUpdateBatch(@Param("statisticsInfos") List<StatisticsInfo> statisticsInfos);

    Map<String, Integer> getActualTimeStatisticsInfo(@Param("userId") String userId);

    List<StatisticsInfo> selectListTotalInfo(@Param("preDate") String preDate);

    List<StatisticsInfo> selectListTotalInfoWeekly(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("dataType") Integer dataType);

    List<StatisticsInfo> selectTotalUserInfoWeekly(@Param("startDate") String startDate, @Param("endDate") String endDate);
}




