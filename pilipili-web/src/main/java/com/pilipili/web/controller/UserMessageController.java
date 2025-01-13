package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.dto.UserMessage.UserMessageCountDto;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.UserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.PageRequest;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.MessageReadTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.UserMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    @ApiOperation("获取消息")
    @SaCheckLogin
    public BaseResponse<Integer> getUserMessage() {
        UserInfo loginUser = userInfoService.getLoginUser();
        int count = (int) userMessageService.count(Wrappers.lambdaQuery(UserMessage.class).
                eq(UserMessage::getUserId, loginUser.getUserId())
                .eq(UserMessage::getMessageType, MessageReadTypeEnum.NO_READ.getType()));
        return ResultUtils.success(count);
    }


    @GetMapping("/getNoReadMessageType")
    @ApiOperation("加载不同类型消息数量")
    @SaCheckLogin
    public BaseResponse<List<UserMessageCountDto>> getNoReadMessageType() {
        UserInfo loginUser = userInfoService.getLoginUser();
        List<UserMessageCountDto> userMessageCountDtoList = userMessageService.getNoReadMessageType(loginUser.getUserId());
        return ResultUtils.success(userMessageCountDtoList);
    }


    @GetMapping("/readAll")
    @ApiOperation("读所有消息")
    @SaCheckLogin
    public BaseResponse<Boolean> readAll(Integer messageType) {
        if (messageType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        userMessageService.readAll(userId, messageType);
        return ResultUtils.success(true);
    }


    @GetMapping("/deleteMessage")
    @ApiOperation("加载不同类型消息数量")
    @SaCheckLogin
    public BaseResponse<Boolean> readAll(@NotNull Integer messageId, @NotNull Integer messageType) {
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        userMessageService.delMessage(userId, messageId, messageType);
        return ResultUtils.success(true);
    }


    @GetMapping("/loadAllMessage")
    @ApiOperation("加载不同类型消息数量")
    @SaCheckLogin
    public BaseResponse<Page<UserMessage>> readAll(@NotNull Integer messageType, @NotNull PageRequest pageRequest) {
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        Page<UserMessage> userMessagePage = userMessageService.loadMessageByType(userId, messageType, pageRequest);
        return ResultUtils.success(userMessagePage);
    }


}
