package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pilipili.Model.entity.StatisticsInfo;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ResultUtils;
import com.pilipili.service.StatisticsInfoService;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/3 22:37
 */
@RequestMapping("/uCenter/statistics")
@RestController
@Validated
public class UCenterStatisticController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private StatisticsInfoService statisticsInfoService;


    @GetMapping("/getActualTimeStatisticsInfo")
    @ApiOperation("获取统计数据")
    @SaCheckLogin
    public BaseResponse<Map<String, Object>> getActualTimeStatisticsInfo() {
        UserInfo loginUser = userInfoService.getLoginUser();
        String preDate = DateUtils.getBeforeDate(1);
        LambdaQueryWrapper<StatisticsInfo> lambdaQueryWrapper = Wrappers.lambdaQuery(StatisticsInfo.class)
                .eq(StatisticsInfo::getUserId, loginUser.getUserId())
                .eq(StatisticsInfo::getStatisticsDate, preDate);
        List<StatisticsInfo> statisticsInfos = statisticsInfoService.list(lambdaQueryWrapper);
        Map<Integer, Integer> preDayData = statisticsInfos.stream().
                collect(Collectors.toMap(StatisticsInfo::getDataType, StatisticsInfo::getStatisticsCount, (data1, data2) -> data2));
        Map<String, Integer> statisticsInfoMap = statisticsInfoService.getActualTimeStatisticsInfo(loginUser.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("preDayData", preDayData);
        result.put("totalCountInfo", statisticsInfoMap);
        return ResultUtils.success(result);
    }

    @GetMapping("/getWeeklyStatisticsInfo")
    @ApiOperation("获取统计数据")
    @SaCheckLogin
    public BaseResponse<List<StatisticsInfo>> getWeeklyStatisticsInfo(@NotNull Integer dataType) {
        List<String> beforeDateList = DateUtils.getBeforeDateList(7);
        UserInfo loginUser = userInfoService.getLoginUser();
        List<StatisticsInfo> statisticsInfos = statisticsInfoService.getWeeklyStatisticsInfo(loginUser.getUserId(), beforeDateList, dataType);
        Map<String, StatisticsInfo> dataMap = statisticsInfos.stream().collect(Collectors.toMap(StatisticsInfo::getStatisticsDate, statisticsInfo -> statisticsInfo, (data1, data2) -> data2));
        List<StatisticsInfo> result = new ArrayList<>();
        for (String date : beforeDateList) {
            StatisticsInfo statisticsInfo = dataMap.get(date);
            if (statisticsInfo == null) {
                statisticsInfo = new StatisticsInfo();
                statisticsInfo.setStatisticsDate(date);
                statisticsInfo.setStatisticsCount(0);
            }
            result.add(statisticsInfo);
        }
        return ResultUtils.success(result);
    }


}
