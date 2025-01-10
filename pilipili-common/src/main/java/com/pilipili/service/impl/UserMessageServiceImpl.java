package com.pilipili.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.dto.Comment.UserMessageExtendDto;
import com.pilipili.Model.entity.UserMessage;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoPost;
import com.pilipili.enums.MessageTypeEnum;
import com.pilipili.mapper.UserMessageMapper;
import com.pilipili.service.UserMessageService;
import com.pilipili.service.VideoCommentService;
import com.pilipili.service.VideoInfoPostService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 15712
 * @description 针对表【UserMessage(用户消息表)】的数据库操作Service实现
 * @createDate 2025-01-09 21:44:58
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage>
        implements UserMessageService {

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Override
    public void saveMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replayCommentId) {
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            return;
        }
        UserMessageExtendDto extendDto = new UserMessageExtendDto();
        String userId = videoInfo.getUserId();
        if (ArrayUtils.contains(new Integer[]{MessageTypeEnum.LIKE.getType(), MessageTypeEnum.COLLECTION.getType()}, messageTypeEnum.getType())) {
            int count = (int) this.count(Wrappers.<UserMessage>lambdaQuery().eq(UserMessage::getUserId, userId).
                    eq(UserMessage::getVideoId, videoId).
                    eq(UserMessage::getMessageType, messageTypeEnum.getType()));
            if (count > 0) {
                return;
            }
        }
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId(userId);
        userMessage.setVideoId(videoId);
        userMessage.setMessageType(messageTypeEnum.getType());
        userMessage.setSendUserId(sendUserId);
        if (replayCommentId != null) {
            VideoComment comment = videoCommentService.getById(replayCommentId);
            // 回复评论,回复的id
            userId = comment.getUserId();
            extendDto.setMessageContentReplay(comment.getContent());
        }
        if (userId.equals(sendUserId)) {
            return;
        }
        //系统消息特殊处理
        if(MessageTypeEnum.SYS.getType().equals(messageTypeEnum.getType())){
            VideoInfoPost videoInfoPost = videoInfoPostService.getById(videoId);
            extendDto.setAuditStatus(videoInfoPost.getStatus());
        }
        userMessage.setUserId(userId);
        userMessage.setExtendJson(JSONUtil.toJsonStr(extendDto));
        this.save(userMessage);


    }
}




