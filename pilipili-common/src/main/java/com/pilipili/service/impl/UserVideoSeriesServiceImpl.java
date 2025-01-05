package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserVideoSeries;
import com.pilipili.Model.entity.UserVideoSeriesVideo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.UserVideoSeriesMapper;
import com.pilipili.mapper.UserVideoSeriesVideoMapper;
import com.pilipili.service.UserVideoSeriesService;
import com.pilipili.service.UserVideoSeriesVideoService;
import com.pilipili.service.VideoInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 15712
 * @description 针对表【UserVideoSeries(用户视频序列归档)】的数据库操作Service实现
 * @createDate 2025-01-01 13:16:26
 */
@Service
public class UserVideoSeriesServiceImpl extends ServiceImpl<UserVideoSeriesMapper, UserVideoSeries>
        implements UserVideoSeriesService {


    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserVideoSeriesMapper userVideoSeriesMapper;

    @Resource
    private UserVideoSeriesVideoMapper userVideoSeriesVideoMapper;

    @Resource
    private UserVideoSeriesVideoService userVideoSeriesVideoService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserVideoSeries(UserVideoSeries bean, String videoIds) {
        //创建视频夹必须得带视频
        if (bean.getSeriesId() == null && StringUtils.isEmpty(videoIds)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (bean.getSeriesId() == null) {
            checkVideoIds(bean.getUserId(), videoIds);
            bean.setSort(userVideoSeriesMapper.selectMaxSort(bean.getUserId()) + 1);
            bean.setUpdateTime(new Date());
            this.save(bean);
            this.saveUserVideoSeriesVideo(bean.getUserId(), bean.getSeriesId(), videoIds);
        } else {
            this.update(bean, Wrappers.lambdaUpdate(UserVideoSeries.class).
                    eq(UserVideoSeries::getSeriesId, bean.getSeriesId())
                    .eq(UserVideoSeries::getUserId, bean.getUserId()));
        }

    }

    @Override
    public List<UserVideoSeries> getUserVideoSeries(String userId) {
        return userVideoSeriesMapper.getUserVideoSeries(userId);
    }

    @Override
    public void saveUserVideoSeriesVideo(String userId, Integer seriesId, String videoIds) {
        String[] videos = videoIds.split(",");
        checkVideoIds(userId, videoIds);
        Integer sort = userVideoSeriesVideoMapper.selectMaxSort(userId);
        List<UserVideoSeriesVideo> userVideoSeriesVideos = new ArrayList<>();
        for (String video : videos) {
            UserVideoSeriesVideo userVideoSeriesVideo = new UserVideoSeriesVideo();
            userVideoSeriesVideo.setUserId(userId);
            userVideoSeriesVideo.setSeriesId(seriesId);
            userVideoSeriesVideo.setVideoId(video);
            userVideoSeriesVideo.setSort(++sort);
            userVideoSeriesVideos.add(userVideoSeriesVideo);
        }
        userVideoSeriesVideoService.saveOrUpdateBatch(userVideoSeriesVideos);

    }

    @Override
    public void delSeriesVideo(String userId, Integer seriesId, String videoId) {
        userVideoSeriesVideoService.remove(Wrappers.lambdaQuery(UserVideoSeriesVideo.class)
                .eq(UserVideoSeriesVideo::getUserId, userId)
                .eq(UserVideoSeriesVideo::getSeriesId, seriesId)
                .eq(UserVideoSeriesVideo::getVideoId, videoId));
    }

    @Override
    public void delSeries(String userId, Integer seriesId) {
        boolean remove = userVideoSeriesVideoService.remove(Wrappers.lambdaQuery(UserVideoSeriesVideo.class)
                .eq(UserVideoSeriesVideo::getUserId, userId)
                .eq(UserVideoSeriesVideo::getSeriesId, seriesId));
        if (!remove) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

    }

    @Override
    public void changeVideoSeriesSort(String userId, String seriesIds) {
        String[] seriesIs = seriesIds.split(",");
        List<UserVideoSeries> seriesIdList = new ArrayList<>();
        int sort = 0;
        for (String seriesId : seriesIs) {
            UserVideoSeries userVideoSeries = new UserVideoSeries();
            userVideoSeries.setUserId(userId);
            userVideoSeries.setSeriesId(Integer.parseInt(seriesId));
            userVideoSeries.setSort(++sort);
            seriesIdList.add(userVideoSeries);
        }
        userVideoSeriesMapper.changeVideoSeriesSort(seriesIdList);
    }

    @Override
    public List<UserVideoSeries> selectSeriesWithVideo(String userId) {
        return userVideoSeriesMapper.selectSeriesWithVideo(userId);
    }

    private void checkVideoIds(String userId, String videoIds) {
        String[] videos = videoIds.split(",");
        QueryWrapper<VideoInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("videoId", (Object) videos);
        queryWrapper.eq("userId", userId);
        if (videoInfoService.count(queryWrapper) != videos.length) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }


}




