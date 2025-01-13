package com.pilipili.service;

import com.pilipili.Model.entity.StatisticsInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author 15712
* @description 针对表【StatisticsInfo(数据统计)】的数据库操作Service
* @createDate 2025-01-09 21:44:36
*/
public interface StatisticsInfoService extends IService<StatisticsInfo> {

    void statisticData();

    Map<String, Integer> getActualTimeStatisticsInfo(String userId);

    List<StatisticsInfo> getWeeklyStatisticsInfo(String userId, List<String> beforeDateList, Integer dataType);

    List<StatisticsInfo> selectListTotalInfo(String preDate);

    List<StatisticsInfo> getWeeklyStatisticsTotalInfo(List<String> beforeDateList, Integer dataType);

    List<StatisticsInfo> getUserCountInfo(List<String> beforeDateList);

}
