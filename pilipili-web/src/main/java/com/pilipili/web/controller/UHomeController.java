package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.UserInfoVo;
import com.pilipili.Model.dto.user.UserFocusFanRequest;
import com.pilipili.Model.dto.user.UserFocusRequest;
import com.pilipili.Model.dto.user.UserUpdateMyRequest;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.UserFocus;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.enums.VideoOrderTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.exception.ThrowUtils;
import com.pilipili.mapper.UserFocusMapper;
import com.pilipili.service.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/1 13:40
 */
@RestController
@RequestMapping("/UHome")
@Validated
public class UHomeController {
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserFocusService userFocusService;

    @Resource
    private UserVideoSeriesService userVideoSeriesService;

    @Resource
    private UserActionService userActionService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserFocusMapper userFocusMapper;


    @GetMapping("/getUserInfo")
    @ApiOperation("获取用户信息")
    public BaseResponse<UserInfoVo> getUserInfo(@RequestParam @NotEmpty String userId) {
        UserInfo loginUser = userInfoService.getLoginUserNoEx();
        UserInfo userInfo = userInfoService.getUserDetailInfo(loginUser == null ? null : loginUser.getUserId(), userId);
        UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class);
        return ResultUtils.success(userInfoVo);
    }

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @return
     */
    @PostMapping("/updateUserInfo")
    @ApiOperation("修改用户信息")
    @SaCheckLogin
    public BaseResponse<Boolean> updateUserInfo(@RequestBody UserUpdateMyRequest userUpdateMyRequest) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String nickName = userUpdateMyRequest.getNickName();
        String userAvatar = userUpdateMyRequest.getUserAvatar();
        String userProfile = userUpdateMyRequest.getUserProfile();
        Integer sex = userUpdateMyRequest.getSex();
        String birthday = userUpdateMyRequest.getBirthday();
        String school = userUpdateMyRequest.getSchool();
        String noticeInfo = userUpdateMyRequest.getNoticeInfo();
        if (StringUtils.isEmpty(nickName) || StringUtils.isEmpty(userAvatar) || sex != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (nickName.length() > 20 || userAvatar.length() > 100 || userProfile.length() > 80 ||
                school.length() > 100 || noticeInfo.length() > 300
                || birthday.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(loginUser.getUserId());
        BeanUtils.copyProperties(userUpdateMyRequest, userInfo);
        userInfoService.updateUserInfo(userInfo);
        return ResultUtils.success(null);
    }


    /**
     * 更新主题
     *
     * @return
     */
    @PostMapping("/updateTheme")
    @SaCheckLogin
    public BaseResponse<UserInfoVo> updateTheme(@RequestBody @NotNull @Max(10) @Min(1) Integer theme) {
        UserInfo loginUserNoEx = userInfoService.getLoginUserNoEx();
        loginUserNoEx.setTheme(theme);
        userInfoService.updateUserInfo(loginUserNoEx);
        return ResultUtils.success(null);
    }


    @PostMapping("/focusUser")
    @SaCheckLogin
    @ApiOperation("关注用户")
    public BaseResponse<Boolean> focusUser(@RequestBody UserFocusRequest userFocusRequest) {
        ThrowUtils.throwIf(userFocusRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(userFocusRequest.getFocusUserId()), ErrorCode.PARAMS_ERROR);
        UserInfo loginUser = userInfoService.getLoginUser();
        userFocusService.focusUser(loginUser.getUserId(), userFocusRequest.getFocusUserId());
        return ResultUtils.success(null);
    }

    @PostMapping("/cancelFocusUser")
    @SaCheckLogin
    @ApiOperation("取消关注")
    public BaseResponse<Boolean> cancelUser(@RequestBody UserFocusRequest userFocusRequest) {
        ThrowUtils.throwIf(userFocusRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(userFocusRequest.getFocusUserId()), ErrorCode.PARAMS_ERROR);
        UserInfo loginUser = userInfoService.getLoginUser();
        userFocusService.focusUser(loginUser.getUserId(), userFocusRequest.getFocusUserId());
        return ResultUtils.success(null);
    }


    @GetMapping("/relation/fans")
    @ApiOperation("获取被关注用户列表")
    @SaCheckLogin
    public BaseResponse<Page<UserFocus>> getFansList(UserFocusFanRequest userFocusFanRequest) {
        ThrowUtils.throwIf(userFocusFanRequest == null, ErrorCode.PARAMS_ERROR);
        UserInfo loginUser = userInfoService.getLoginUser();
        Page<UserFocus> userFocusPage = new Page<>(userFocusFanRequest.getCurrent(), userFocusFanRequest.getPageSize());
        return ResultUtils.success(userFocusMapper.getFansList(userFocusPage, loginUser.getUserId(), userFocusFanRequest.getQueryType()));
    }


    @GetMapping("relation/followings")
    @ApiOperation("获取关注用户列表")
    @SaCheckLogin
    public BaseResponse<Page<UserFocus>> getFocusList(UserFocusFanRequest userFocusFanRequest) {
        ThrowUtils.throwIf(userFocusFanRequest == null, ErrorCode.PARAMS_ERROR);
        UserInfo loginUser = userInfoService.getLoginUser();
        Page<UserFocus> userFocusPage = new Page<>(userFocusFanRequest.getCurrent(), userFocusFanRequest.getPageSize());
        return ResultUtils.success(userFocusMapper.getFansList(userFocusPage, loginUser.getUserId(), userFocusFanRequest.getQueryType()));
    }


    @GetMapping("/loadVideoList")
    @ApiOperation("获取用户发布视频")
    public BaseResponse<Page<VideoInfo>> loadVideoList(String userId, PageRequest pageRequest, Integer type, String videoName, Integer orderType) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR);
        QueryWrapper<VideoInfo> queryWrapper = new QueryWrapper<>();
        if (type != null) {
            pageRequest.setPageSize(15);
        }
        VideoOrderTypeEnum videoOrderTypeEnum = VideoOrderTypeEnum.getByType(orderType);
        if (videoOrderTypeEnum == null) {
            videoOrderTypeEnum = VideoOrderTypeEnum.CREATE_TIME;
        }
        queryWrapper.orderByDesc(videoOrderTypeEnum.getField());
        queryWrapper.like("videoName", videoName);
        queryWrapper.eq("userId", userId);
        return ResultUtils.success(videoInfoService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), queryWrapper));
    }


    @GetMapping("/loadUserCollection")
    @ApiOperation("获取用户收藏视频")
    public BaseResponse<Page<UserAction>> loadUserCollection(String userId, PageRequest pageRequest) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR);
        List<VideoInfo> videoInfos = videoInfoService.list();
        LambdaQueryWrapper<UserAction> queryWrapper = Wrappers.lambdaQuery(UserAction.class)
                .eq(UserAction::getUserId, userId)
                .eq(UserAction::getActionType, UserActionTypeEnum.VIDEO_COLLECT.getType());
        Page<UserAction> page = userActionService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), queryWrapper);
        List<UserAction> records = page.getRecords();
        Map<String, VideoInfo> videoInfoMap = videoInfos.stream().collect(Collectors.toMap(VideoInfo::getVideoId, Function.identity()));
        records.forEach(userAction -> {
            userAction.setVideoCover(videoInfoMap.get(userAction.getVideoId()).getVideoCover());
            userAction.setVideoName(videoInfoMap.get(userAction.getVideoId()).getVideoName());
            userAction.setVideoCreateTime(videoInfoMap.get(userAction.getVideoId()).getCreateTime());
        });
        return ResultUtils.success(page);
    }



}
