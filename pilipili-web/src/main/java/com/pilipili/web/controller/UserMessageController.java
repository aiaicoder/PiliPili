package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.UserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.MessageReadTypeEnum;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.UserMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/10 23:17
 */
@RestController
@RequestMapping("/userMessage")
public class UserMessageController {

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/getUserMessage")
    @ApiOperation("获取用户提示消息")
    @SaCheckLogin
    public BaseResponse<Integer> getUserMessage() {
        UserInfo loginUser = userInfoService.getLoginUser();
        int count = (int) userMessageService.count(Wrappers.lambdaQuery(UserMessage.class).
                eq(UserMessage::getUserId, loginUser.getUserId())
                .eq(UserMessage::getMessageType, MessageReadTypeEnum.NO_READ.getType()));
        return ResultUtils.success(count);
    }


}
