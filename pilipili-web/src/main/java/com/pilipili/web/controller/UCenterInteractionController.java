package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoDanMu;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoPostMapper;
import com.pilipili.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/3 22:37
 */
@RequestMapping("/uCenter")
@RestController
@Validated
public class UCenterInteractionController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoDanMuService videoDanMuService;



    @GetMapping("/loadAllVideo")
    @ApiOperation("获取用户上传视频")
    @SaCheckLogin
    public BaseResponse<List<VideoInfo>> loadAllVideo() {
        UserInfo loginUser = userInfoService.getLoginUser();
        return ResultUtils.success(videoInfoService.list(Wrappers.<VideoInfo>lambdaQuery().
                eq(VideoInfo::getUserId, loginUser.getUserId())
        ));
    }


    @GetMapping("/loadComment")
    @ApiOperation("获取视频用户评论信息")
    @SaCheckLogin
    public BaseResponse<Page<VideoComment>> loadComment(@NotEmpty String videoId, PageRequest pageRequest) {
        Page<VideoComment> page = videoCommentService.getVideoCommentList(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), videoId);
        return ResultUtils.success(page);
    }


    @PostMapping("/deleteComment")
    @ApiOperation("删除评论")
    @SaCheckLogin
    public BaseResponse<Boolean> deleteComment(@NotNull Integer commentId) {
        VideoComment videoComment = videoCommentService.getById(commentId);
        if (videoComment == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        videoCommentService.removeById(commentId);
        return ResultUtils.success(true);
    }


    @GetMapping("/loadDanMu")
    @ApiOperation("获取弹幕")
    public BaseResponse<Page<VideoDanMu>> loadDanMu(@NotEmpty String videoId,PageRequest pageRequest) {
        UserInfo loginUser = userInfoService.getLoginUser();
        Page<VideoDanMu> page = videoDanMuService.getDanMuList(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), videoId,loginUser.getUserId());
        return ResultUtils.success(page);
    }


    @PostMapping("/loadDanMu")
    @ApiOperation("删除弹幕")
    public BaseResponse<Boolean> loadDanMu(@NotEmpty Integer danMuId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        videoDanMuService.deleteDanMu(danMuId,loginUser);
        return ResultUtils.success(null);
    }



}
