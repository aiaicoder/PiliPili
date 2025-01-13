package com.pilipili.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Constant.UserConstant;
import com.pilipili.Model.dto.Comment.CommentQuery;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoDanMu;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoCommentService;
import com.pilipili.service.VideoDanMuService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 11:47
 */
@RestController
@RequestMapping(value = "/interaction")
public class InteractionController {
    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoCommentService videoCommentService;
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoDanMuService videoDanMuService;


    @GetMapping("/loadAllComment")
    @ApiOperation("获取评论")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<VideoComment>> getVideoInfoPostList(PageRequest pageRequest, String videoNameFuzzy) {
        Page<VideoComment> result = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        CommentQuery commentQuery = new CommentQuery();
        commentQuery.setOrderBy("commentId");
        commentQuery.setVideoNameFuzzy(videoNameFuzzy);
        result = videoCommentService.findListByPage(result, commentQuery);
        return ResultUtils.success(result);
    }


    @GetMapping("/deleteComment")
    @ApiOperation("获取评论")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteComment(@NotNull Integer commentId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        videoCommentService.deleteComment(loginUser, commentId);
        return ResultUtils.success(null);
    }


    @GetMapping("/loadDanMu")
    @ApiOperation("获取弹幕")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<VideoDanMu>> loadDanMu(@NotEmpty String videoNameFuzzy, PageRequest pageRequest) {
        Page<VideoDanMu> page = videoDanMuService.getDanMuListByFuzzy(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), videoNameFuzzy);
        return ResultUtils.success(page);
    }

    @PostMapping("/deleteDanMu")
    @ApiOperation("获取弹幕")
    @SaCheckLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDanMu(@NotNull Integer danMuId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        videoDanMuService.deleteDanMu(danMuId,loginUser);
        return ResultUtils.success(null);
    }


}
