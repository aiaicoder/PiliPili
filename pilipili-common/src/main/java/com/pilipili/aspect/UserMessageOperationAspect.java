package com.pilipili.aspect;

import com.pilipili.Model.dto.user.UserActionRequest;
import com.pilipili.Model.dto.video.VideoCommentPostRequest;
import com.pilipili.Model.dto.video.VideoInfoAuditRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.annotation.RecordUserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.enums.MessageTypeEnum;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.UserMessageService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/10 11:06
 */
public class UserMessageOperationAspect {

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private UserInfoService userInfoService;

    final String PARAMETER_VIDEO_AUDIT = "videoInfoAuditRequest";

    final String PARAMETER_VIDEO_ACTION = "userActionRequest";

    final String PARAMETER_COMMENT_ACTION = "videoCommentPostRequest";


    @Around("@annotation(com.pilipili.annotation.RecordUserMessage)")
    public BaseResponse recordUserMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseResponse proceed = (BaseResponse) joinPoint.proceed();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RecordUserMessage recordUserMessage = method.getAnnotation(RecordUserMessage.class);
        if (recordUserMessage != null) {
            saveMessage(recordUserMessage, joinPoint.getArgs(), method.getParameters());
        }
        return proceed;
    }

    private void saveMessage(RecordUserMessage recordUserMessage, Object[] args, Parameter[] parameters) {
        UserInfo loginUserNoEx = userInfoService.getLoginUserNoEx();
        MessageTypeEnum messageTypeEnum = recordUserMessage.messageType();
        if (PARAMETER_VIDEO_AUDIT.equals(parameters[0].getName())) {
            VideoInfoAuditRequest videoInfoAuditRequest = (VideoInfoAuditRequest) args[0];
            String videoId = videoInfoAuditRequest.getVideoId();
            String reason = videoInfoAuditRequest.getReason();
            userMessageService.saveMessage(videoId, loginUserNoEx == null ? null : loginUserNoEx.getUserId(), messageTypeEnum,reason, null);
        } else if (PARAMETER_VIDEO_ACTION.equals(parameters[0].getName())) {
            UserActionRequest userActionRequest = (UserActionRequest) args[0];
            String videoId = userActionRequest.getVideoId();
            Integer actionType = userActionRequest.getActionType();
            if (UserActionTypeEnum.VIDEO_COLLECT.getType().equals(actionType)) {
                messageTypeEnum = MessageTypeEnum.COLLECTION;
            }
            userMessageService.saveMessage(videoId, loginUserNoEx == null ? null : loginUserNoEx.getUserId(), messageTypeEnum, null, null);
        }else if (PARAMETER_COMMENT_ACTION.equals(parameters[0].getName())) {
            VideoCommentPostRequest videoCommentPostRequest = (VideoCommentPostRequest) args[0];
            String videoId = videoCommentPostRequest.getVideoId();
            Integer replayCommentId = videoCommentPostRequest.getReplayCommentId();
            String content = videoCommentPostRequest.getContent();
            userMessageService.saveMessage(videoId, loginUserNoEx == null ? null : loginUserNoEx.getUserId(), messageTypeEnum, content, replayCommentId);
        }
    }


}
