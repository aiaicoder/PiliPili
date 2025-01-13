package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.StatisticsInfo;
import com.pilipili.Model.entity.UserFocus;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.enums.StatisticsTypeEnum;
import com.pilipili.mapper.StatisticsInfoMapper;
import com.pilipili.service.StatisticsInfoService;
import com.pilipili.service.UserFocusService;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.utils.DateUtils;
import com.pilipili.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 15712
 * @description 针对表【StatisticsInfo(数据统计)】的数据库操作Service实现
 * @createDate 2025-01-09 21:44:36
 */
@Service
public class StatisticsInfoServiceImpl extends ServiceImpl<StatisticsInfoMapper, StatisticsInfo>
        implements StatisticsInfoService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserFocusService userFocusService;


    @Resource
    private StatisticsInfoMapper statisticsInfoMapper;

    @Resource
    private UserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void statisticData() {
        String statisticDate = DateUtils.getBeforeDate(1);
        Map<String, Integer> videoPlayCountMap = redisUtils.getVideoPlayCount(statisticDate);
        List<String> videoIds = new ArrayList<>(videoPlayCountMap.keySet());
        List<VideoInfo> videoInfos = videoInfoService.listByIds(videoIds);
        List<StatisticsInfo> statisticsInfos = new ArrayList<>();
        // 按userId分组并统计播放量
        Map<String, Integer> userPlayCountMap = videoInfos.stream()
                .collect(Collectors.groupingBy(
                        VideoInfo::getUserId,
                        Collectors.summingInt(video -> videoPlayCountMap.getOrDefault(video.getVideoId(), 0))
                ));

        userPlayCountMap.entrySet().stream().forEach(entry -> {
            StatisticsInfo statisticsInfo = new StatisticsInfo();
            statisticsInfo.setStatisticsDate(statisticDate);
            statisticsInfo.setUserId(entry.getKey());
            statisticsInfo.setDataType(StatisticsTypeEnum.PLAY.getType());
            statisticsInfo.setStatisticsCount(entry.getValue());
            statisticsInfos.add(statisticsInfo);
        });

        //统计粉丝数
        List<StatisticsInfo> userFocusList = statisticsInfoMapper.statisticUserFocus(statisticDate);
        userFocusList.forEach(statisticsInfo -> {
            statisticsInfo.setDataType(StatisticsTypeEnum.FANS.getType());
            statisticsInfo.setStatisticsDate(statisticDate);
        });
        statisticsInfos.addAll(userFocusList);

        //统计评论数
        List<StatisticsInfo> userCommentList = statisticsInfoMapper.statisticUserComment(statisticDate);
        userCommentList.forEach(statisticsInfo -> {
            statisticsInfo.setDataType(StatisticsTypeEnum.COMMENT.getType());
            statisticsInfo.setStatisticsDate(statisticDate);
        });
        statisticsInfos.addAll(userCommentList);

        //统计收藏数，点赞数，投币数
        List<StatisticsInfo> userActionList = statisticsInfoMapper.selectStatisticInfo(statisticDate,
                new Integer[]{StatisticsTypeEnum.COIN.getType(), StatisticsTypeEnum.COLLECTION.getType(), StatisticsTypeEnum.LIKE.getType()});
        userActionList.forEach(statisticsInfo -> {
            statisticsInfo.setStatisticsDate(statisticDate);
        });

        statisticsInfos.addAll(userActionList);
        statisticsInfoMapper.insertOrUpdateBatch(statisticsInfos);
    }

    @Override
    public Map<String, Integer> getActualTimeStatisticsInfo(String userId) {
        Map<String, Integer> result = statisticsInfoMapper.getActualTimeStatisticsInfo(userId);
        if (StringUtils.isNotBlank(userId)) {
            result.put("userCount", (int) userFocusService.count(Wrappers.lambdaQuery(UserFocus.class).eq(UserFocus::getFocusUserId, userId)));
        } else {
            result.put("userCount", (int) userInfoService.count());
        }
        return result;
    }

    @Override
    public List<StatisticsInfo> getWeeklyStatisticsInfo(String userId, List<String> beforeDateList, Integer dataType) {
        LambdaQueryWrapper<StatisticsInfo> queryWrapper = Wrappers.lambdaQuery(StatisticsInfo.class)
                .eq(StatisticsInfo::getUserId, userId)
                .eq(StatisticsInfo::getDataType, dataType)
                .le(StatisticsInfo::getStatisticsDate, beforeDateList.get(0))
                .ge(StatisticsInfo::getStatisticsDate, beforeDateList.get(beforeDateList.size() - 1));
        List<StatisticsInfo> list = list(queryWrapper);
        return list == null ? new ArrayList<>() : list;
    }

    @Override
    public List<StatisticsInfo> selectListTotalInfo(String preDate) {
        return statisticsInfoMapper.selectListTotalInfo(preDate);
    }

    @Override
    public List<StatisticsInfo> getWeeklyStatisticsTotalInfo(List<String> beforeDateList, Integer dataType) {
        return statisticsInfoMapper.selectListTotalInfoWeekly(beforeDateList.get(0), beforeDateList.get(beforeDateList.size() - 1), dataType);
    }

    /**
     * 查询一周内新增用户数
     * @param beforeDateList
     * @return
     */
    @Override
    public List<StatisticsInfo> getUserCountInfo(List<String> beforeDateList) {
        return statisticsInfoMapper.selectTotalUserInfoWeekly(beforeDateList.get(0), beforeDateList.get(beforeDateList.size() - 1));
    }
}




