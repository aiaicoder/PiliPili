package com.pilipili.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.VideoInfoAndUserActionVo;
import com.pilipili.Model.Vo.VideoInfoVo;
import com.pilipili.Model.dto.video.VideoInfoQueryRequest;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoFile;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.component.EsSearchComponent;
import com.pilipili.enums.SearchOrderTypeEnum;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.enums.VideoRecommendTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.UserActionService;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoFileService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 11:47
 */
@RestController
@RequestMapping(value = "/videoInfo")
public class VideoInfoController {
    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Resource
    private UserActionService userActionService;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private EsSearchComponent esSearchComponent;


    @GetMapping("/loadRecommendVideo")
    @ApiOperation("获取推荐视频列表")
    public BaseResponse<List<VideoInfoVo>> getVideoInfoPostList() {
        List<VideoInfoVo> recommendVideoList = videoInfoMapper.getRecommendVideoList(VideoRecommendTypeEnum.RECOMMEND.getType());
        return ResultUtils.success(recommendVideoList);
    }


    @PostMapping("/loadVide")
    @ApiOperation("展示视频")
    public BaseResponse<Page<VideoInfoVo>> auditVideo(VideoInfoQueryRequest videoInfoQueryRequest) {
        if (videoInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<VideoInfoVo> page = new Page<>(videoInfoQueryRequest.getCurrent(), videoInfoQueryRequest.getPageSize());
        Page<VideoInfoVo> videoInfoVoPage = videoInfoMapper.loadVideo(page, videoInfoQueryRequest, VideoRecommendTypeEnum.NO_RECOMMEND.getType());
        return ResultUtils.success(videoInfoVoPage);
    }


    @GetMapping("/getVideoInfo")
    @ApiOperation("获取视频详情")
    public BaseResponse<VideoInfoAndUserActionVo> getVideoInfo(String videoId) {
        if (StringUtils.isBlank(videoId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        VideoInfoAndUserActionVo videoInfoAndUserActionVo = new VideoInfoAndUserActionVo();
        List<UserAction> userActionList = new ArrayList<>();
        UserInfo loginUser = userInfoService.getLoginUserNoEx();
        //如果用户登录之后就获取用户的登录行为
        if (loginUser != null) {
            QueryWrapper<UserAction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", loginUser.getUserId());
            queryWrapper.eq("videoId", videoId);
            queryWrapper.in("actionType", (Object) new Integer[]{UserActionTypeEnum.VIDEO_COLLECT.getType(),
                    UserActionTypeEnum.VIDEO_LIKE.getType(), UserActionTypeEnum.VIDEO_COIN.getType()});
            userActionList = userActionService.list(queryWrapper);
        }
        VideoInfoVo videoInfoVo = BeanUtil.copyProperties(videoInfo, VideoInfoVo.class);
        videoInfoAndUserActionVo.setVideoInfoVo(videoInfoVo);
        videoInfoAndUserActionVo.setUserAction(userActionList);
        return ResultUtils.success(videoInfoAndUserActionVo);
    }

    @GetMapping("/getVideoPList")
    @ApiOperation("获取视频P数")
    public BaseResponse<List<VideoInfoFile>> loadVideoPList(String videoId) {
        if (StringUtils.isEmpty(videoId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<VideoInfoFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        queryWrapper.orderByAsc("fileIndex");
        List<VideoInfoFile> fileList = videoInfoFileService.list(queryWrapper);
        return ResultUtils.success(fileList);
    }


    @GetMapping("/reportVideoPlayOnline")
    @ApiOperation("上报视频播放在线人数")
    public BaseResponse<Integer> reportVideoPlayOnline(@NotEmpty String fileId, @NotEmpty String deviceId) {
        return ResultUtils.success(redisUtils.reportVideoPlayOnline(fileId, deviceId));
    }


    @GetMapping("/recommend")
    @ApiOperation("获取推荐视频")
    public BaseResponse<List<VideoInfoVo>> getVideoRecommend(@NotEmpty String keyword, @NotEmpty String videoId) {
        List<VideoInfoVo> videoInfoVoList = esSearchComponent.searchDoc(false, keyword, SearchOrderTypeEnum.VIDEO_PLAY.getType(), 1, 20).getRecords();
        return ResultUtils.success(videoInfoVoList.stream().filter(videoInfoVo -> !videoInfoVo.getVideoId().equals(videoId)).collect(Collectors.toList()));
    }


    @GetMapping("/getSearchKeyTop")
    @ApiOperation("获取搜索热词")
    public BaseResponse<List<String>> getSearchKeyTop() {
        List<String> searchKeyTop = redisUtils.getKeyWord(10);
        return ResultUtils.success(searchKeyTop);
    }

    @GetMapping("/searchVideo")
    @ApiOperation("搜索视频")
    public BaseResponse<Page<VideoInfoVo>> searchVideo(@NotEmpty String keyword, Integer orderType, PageRequest pageRequest) {
        redisUtils.addKeyWordCount(keyword);
        return ResultUtils.success(esSearchComponent.searchDoc(true, keyword, orderType, pageRequest.getCurrent(), pageRequest.getPageSize()));
    }


    @GetMapping("/loadHotVideoList")
    @ApiOperation("搜索视频")
    public BaseResponse<Page<VideoInfoVo>> loadHotVideoList(PageRequest pageRequest) {
        Page<VideoInfoVo> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        Page<VideoInfoVo> videoInfoVoPage = videoInfoMapper.load24HoursHotVideo(page);
        return ResultUtils.success(videoInfoVoPage);
    }
}
