package com.pilipili.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Constant.UserConstant;
import com.pilipili.Model.Vo.VideoInfoPostVo;
import com.pilipili.Model.dto.video.VideoInfoAuditRequest;
import com.pilipili.Model.dto.video.VideoInfoPostListRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.annotation.RecordUserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.MessageTypeEnum;
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
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
    @SaCheckRole(UserConstant.ADMIN_ROLE)
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
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public BaseResponse<Boolean> auditVideo(VideoInfoAuditRequest videoInfoAuditRequest) {
        if (videoInfoAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String videoId = videoInfoAuditRequest.getVideoId();
        Integer status = videoInfoAuditRequest.getStatus();
        String reason = videoInfoAuditRequest.getReason();
        if (videoId == null || status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        videoInfoPostService.auditVideo(videoId, status, reason);
        return ResultUtils.success(null);
    }


    @PostMapping("/recommendVideo")
    @ApiOperation("推荐视频")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public BaseResponse<Boolean> recommendVideo(@NotEmpty String videoId) {
        videoInfoService.recommendVideo(videoId);
        return ResultUtils.success(null);
    }

    @PostMapping("/deleteVideo")
    @ApiOperation("删除视频")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public BaseResponse<Boolean> deleteVideo(@NotEmpty String videoId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        videoInfoService.deleteVideo(loginUser, videoId);
        return ResultUtils.success(null);
    }

    @GetMapping("/loadVideoInfoFile")
    @ApiOperation("展示分批视频")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public BaseResponse<List<VideoInfoFilePost>> loadVideoInfoFile(@NotEmpty String videoId) {
        return ResultUtils.success(videoInfoFilePostService.list(Wrappers.lambdaQuery(VideoInfoFilePost.class).eq(VideoInfoFilePost::getVideoId, videoId).orderByAsc(VideoInfoFilePost::getFileIndex)));
    }


}
