package com.pilipili.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Model.Vo.VideoInfoPostVo;
import com.pilipili.Model.dto.video.VideoInfoAuditRequest;
import com.pilipili.Model.dto.video.VideoInfoPostListRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoPostMapper;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoFilePostService;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoPostMapper videoInfoPostMapper;

    @Resource
    private UserInfoService userInfoService;


    @GetMapping("/getVideoInfoPostList")
    @ApiOperation("获取视频列表")
    @SaCheckLogin
    public BaseResponse<Page<VideoInfoPostVo>> getVideoInfoPostList(VideoInfoPostListRequest videoInfoPostListRequest) {
        if (videoInfoPostListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        videoInfoPostListRequest.setSortField("lastUpdateTime");
        videoInfoPostListRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        Page<VideoInfoPostVo> page = new Page<>(videoInfoPostListRequest.getCurrent(), videoInfoPostListRequest.getPageSize());
        Page<VideoInfoPostVo> result = videoInfoPostMapper.getVideoInfoPostVoList(page, loginUser.getUserId(), null, videoInfoPostListRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/auditVideo")
    @ApiOperation("审核视频")
    @SaCheckLogin
    public BaseResponse<Boolean> auditVideo(VideoInfoAuditRequest videoInfoAuditRequest) {
        if (videoInfoAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String videoId = videoInfoAuditRequest.getVideoId();
        Integer status = videoInfoAuditRequest.getStatus();
        String reason = videoInfoAuditRequest.getReason();
        if (videoId == null || status == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        videoInfoPostService.auditVideo(videoId, status, reason);
        return ResultUtils.success(true);
    }
}
