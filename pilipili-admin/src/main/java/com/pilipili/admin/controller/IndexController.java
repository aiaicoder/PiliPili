package com.pilipili.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.pilipili.Constant.UserConstant;
import com.pilipili.Model.entity.StatisticsInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.StatisticsTypeEnum;
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
@RequestMapping("/index")
@RestController
@Validated
public class IndexController {

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
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Map<String, Object>> getActualTimeStatisticsInfo() {
        String preDate = DateUtils.getBeforeDate(1);
        List<StatisticsInfo> preDayData = statisticsInfoService.selectListTotalInfo(preDate);
        preDayData.forEach(item -> {
            if (item.getDataType().equals(StatisticsTypeEnum.FANS.getType())) {
                item.setStatisticsCount((int) userInfoService.count());
            }
        });
        Map<Integer, Integer> preDayDataMap = preDayData.stream().
                collect(Collectors.toMap(StatisticsInfo::getDataType, StatisticsInfo::getStatisticsCount, (data1, data2) -> data2));
        Map<String, Integer> statisticsInfoMap = statisticsInfoService.getActualTimeStatisticsInfo(null);
        Map<String, Object> result = new HashMap<>();
        result.put("preDayData", preDayDataMap);
        result.put("totalCountInfo", statisticsInfoMap);
        return ResultUtils.success(result);
    }

    @GetMapping("/getWeeklyStatisticsInfo")
    @ApiOperation("获取统计数据")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<List<StatisticsInfo>> getWeeklyStatisticsInfo(@NotNull Integer dataType) {
        List<String> beforeDateList = DateUtils.getBeforeDateList(7);
        List<StatisticsInfo> statisticsInfos = null;
        if (!StatisticsTypeEnum.FANS.getType().equals(dataType)) {
            statisticsInfos = statisticsInfoService.getWeeklyStatisticsTotalInfo(beforeDateList, dataType);
        } else {
            statisticsInfos = statisticsInfoService.getUserCountInfo(beforeDateList);
        }
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
