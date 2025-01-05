package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pilipili.Model.Vo.UserVideoSeriesDetailVo;
import com.pilipili.Model.dto.video.VideoSeriesRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.UserVideoSeries;
import com.pilipili.Model.entity.UserVideoSeriesVideo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.exception.ThrowUtils;
import com.pilipili.mapper.UserVideoSeriesVideoMapper;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.UserVideoSeriesService;
import com.pilipili.service.UserVideoSeriesVideoService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/1 13:40
 */
@RestController
@RequestMapping("/UHome/series")
@Validated
public class UHomeVideoSeriesController {
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserVideoSeriesService userVideoSeriesService;

    @Resource
    private UserVideoSeriesVideoService userVideoSeriesVideoService;

    @Resource
    private UserVideoSeriesVideoMapper userVideoSeriesVideoMapper;


    @Resource
    private VideoInfoService videoInfoService;


    @GetMapping("/loadVideoSeriesList")
    @ApiOperation("加载用户归档")
    public BaseResponse<List<UserVideoSeries>> loadSeriesList(@RequestParam String userId) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userVideoSeriesService.getUserVideoSeries(userId));
    }

    @PostMapping("/saveVideoSeries")
    @ApiOperation("创建视频归档")
    @SaCheckLogin
    public BaseResponse<Boolean> saveVideoSeries(@RequestBody VideoSeriesRequest videoSeriesRequest) {
        ThrowUtils.throwIf(StringUtils.isEmpty(videoSeriesRequest.getSeriesName()), ErrorCode.PARAMS_ERROR);
        String seriesName = videoSeriesRequest.getSeriesName();
        String seriesDescription = videoSeriesRequest.getSeriesDescription();
        String videoIds = videoSeriesRequest.getVideoIds();
        if (seriesName.length() > 100 || seriesDescription.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVideoSeries videoSeries = new UserVideoSeries();
        BeanUtils.copyProperties(videoSeriesRequest, videoSeries);
        userVideoSeriesService.saveUserVideoSeries(videoSeries, videoIds);
        return ResultUtils.success(true);
    }


    @GetMapping("/loadAllVideo")
    @ApiOperation("加载用户归档视频")
    public BaseResponse<List<VideoInfo>> loadAllVideo(Integer seriesId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        LambdaQueryWrapper<VideoInfo> videoInfoLambdaQueryWrapper = Wrappers.lambdaQuery(VideoInfo.class);
        if (seriesId != null) {
            QueryWrapper<UserVideoSeriesVideo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", loginUser.getUserId());
            queryWrapper.eq("seriesId", seriesId);
            List<UserVideoSeriesVideo> videoSeriesList = userVideoSeriesVideoService.list(queryWrapper);
            List<String> excludeVideoIds = videoSeriesList.stream().map(UserVideoSeriesVideo::getVideoId).collect(Collectors.toList());
            videoInfoLambdaQueryWrapper.eq(VideoInfo::getUserId, loginUser.getUserId()).notIn(VideoInfo::getVideoId, excludeVideoIds);

        }
        videoInfoLambdaQueryWrapper.eq(VideoInfo::getUserId, loginUser.getUserId());
        List<VideoInfo> videoInfos = videoInfoService.list(videoInfoLambdaQueryWrapper);
        return ResultUtils.success(videoInfos);
    }


    @GetMapping("/getDetailSeriesVideo")
    @ApiOperation("加载用户视频归档详情")
    public BaseResponse<UserVideoSeriesDetailVo> getDetailSeriesVideo(@NotNull Integer seriesId) {
        UserVideoSeries userVideoSeries = userVideoSeriesService.getById(seriesId);
        ThrowUtils.throwIf(userVideoSeries == null, ErrorCode.NOT_FOUND_ERROR);
        List<UserVideoSeriesVideo> userVideoSeriesVideos = userVideoSeriesVideoMapper.getDetailSeriesVideo(seriesId);
        UserVideoSeriesDetailVo userVideoSeriesDetailVo = new UserVideoSeriesDetailVo();
        userVideoSeriesDetailVo.setUserVideoSeries(userVideoSeries);
        userVideoSeriesDetailVo.setUserVideoSeriesVideos(userVideoSeriesVideos);
        return ResultUtils.success(userVideoSeriesDetailVo);
    }


    @PostMapping("/saveSeriesVideo")
    @ApiOperation("视频夹添加视频")
    @SaCheckLogin
    public BaseResponse<Boolean> saveSeriesVideo(@NotNull Integer seriesId, @NotEmpty String videoIds) {
        UserInfo loginUser = userInfoService.getLoginUser();
        userVideoSeriesService.saveUserVideoSeriesVideo(loginUser.getUserId(), seriesId, videoIds);
        return ResultUtils.success(true);
    }



    @PostMapping("/delSeriesVideo")
    @ApiOperation("删除视频夹中视频")
    @SaCheckLogin
    public BaseResponse<Boolean> getDetailSeriesVideo(@NotNull Integer seriesId, @NotEmpty String videoId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        userVideoSeriesService.delSeriesVideo(loginUser.getUserId(), seriesId, videoId);
        return ResultUtils.success(true);
    }


    @PostMapping("/delSeries")
    @ApiOperation("删除视频夹")
    @SaCheckLogin
    public BaseResponse<Boolean> getDetailSeries(@NotNull Integer seriesId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        userVideoSeriesService.delSeries(loginUser.getUserId(), seriesId);
        return ResultUtils.success(true);
    }


    @PostMapping("/changeVideoSeriesSort")
    @ApiOperation("修改视频归档排序")
    @SaCheckLogin
    public BaseResponse<Boolean> changeVideoSeriesSort(@NotNull String seriesIds) {
        UserInfo loginUser = userInfoService.getLoginUser();
        userVideoSeriesService.changeVideoSeriesSort(loginUser.getUserId(), seriesIds);
        return ResultUtils.success(true);
    }

    @GetMapping("/loadSeriesWithVideo")
    @ApiOperation("修改视频归档排序")
    public BaseResponse<List<UserVideoSeries>> loadSeriesWithVideo(String userId) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR);
        List<UserVideoSeries> userVideoSeries = userVideoSeriesService.selectSeriesWithVideo(userId);
        return ResultUtils.success(userVideoSeries);
    }




}
