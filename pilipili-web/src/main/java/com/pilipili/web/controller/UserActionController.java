package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.pilipili.Model.dto.user.UserActionRequest;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.annotation.RecordUserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.MessageTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.service.UserActionService;
import com.pilipili.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/28 18:53
 */
@RestController
@RequestMapping("/userAction")
public class UserActionController {
    @Resource
    private UserActionService userActionService;

    @Resource
    private UserInfoService userInfoService;

    @PostMapping("/doAction")
    @ApiOperation(value = "用户行为")
    @SaCheckLogin
    @RecordUserMessage(messageType = MessageTypeEnum.LIKE)
    public BaseResponse<Boolean> doAction(@RequestBody UserActionRequest userActionRequest) {
        UserInfo loginUser = userInfoService.getLoginUser();
        if (userActionRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String videoId = userActionRequest.getVideoId();
        Integer actionType = userActionRequest.getActionType();
        Integer actionCount = userActionRequest.getActionCount();
        actionCount = actionCount == null ? 1 : actionCount;
        if (videoId == null || actionType == null || actionCount > 2 || actionCount <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userActionRequest.setActionCount(actionCount);
        UserAction userAction = BeanUtil.copyProperties(userActionRequest, UserAction.class);
        userAction.setUserId(loginUser.getUserId());
        userActionService.saveAction(userAction);
        return ResultUtils.success(null);
    }
}
