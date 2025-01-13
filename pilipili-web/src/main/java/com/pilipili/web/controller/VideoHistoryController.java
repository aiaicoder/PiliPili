package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoPlayHistory;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoPlayHistoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/11 19:41
 */
@RestController
@RequestMapping("/videoHistory")
public class VideoHistoryController {

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/getVideoPlayHistory")
    @ApiOperation("获取播放记录")
    @SaCheckLogin
    public BaseResponse<Page<VideoPlayHistory>> getVideoPlayHistory(PageRequest pageRequest) {
        UserInfo loginUser = userInfoService.getLoginUser();
        Page<VideoPlayHistory> videoPlayHistoryPage = videoPlayHistoryService.getVideoPlayHistoryList(loginUser.getUserId(), pageRequest);
        return ResultUtils.success(videoPlayHistoryPage);
    }


    @PostMapping("/clearPlayHistory")
    @ApiOperation("清空播放历史")
    @SaCheckLogin
    public BaseResponse<Boolean> getVideoPlayHistory() {
        UserInfo loginUser = userInfoService.getLoginUser();
        QueryWrapper<VideoPlayHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getUserId());
        videoPlayHistoryService.remove(queryWrapper);
        return ResultUtils.success(null);
    }


    @PostMapping("/delVideoPlayHistory")
    @ApiOperation("删除指定播放历史")
    @SaCheckLogin
    public BaseResponse<Boolean> getVideoPlayHistory(@NotEmpty String videoId) {
        UserInfo loginUser = userInfoService.getLoginUser();
        videoPlayHistoryService.deleteVideoPlayHistory(loginUser.getUserId(), videoId);
        return ResultUtils.success(null);
    }


}
