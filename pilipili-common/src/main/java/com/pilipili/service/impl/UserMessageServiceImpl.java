package com.pilipili.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.dto.Comment.UserMessageExtendDto;
import com.pilipili.Model.dto.UserMessage.UserMessageCountDto;
import com.pilipili.Model.entity.UserMessage;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoPost;
import com.pilipili.common.PageRequest;
import com.pilipili.enums.MessageReadTypeEnum;
import com.pilipili.enums.MessageTypeEnum;
import com.pilipili.mapper.UserMessageMapper;
import com.pilipili.service.UserMessageService;
import com.pilipili.service.VideoCommentService;
import com.pilipili.service.VideoInfoPostService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private UserMessageMapper userMessageMapper;

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
        if (MessageTypeEnum.SYS.getType().equals(messageTypeEnum.getType())) {
            VideoInfoPost videoInfoPost = videoInfoPostService.getById(videoId);
            extendDto.setAuditStatus(videoInfoPost.getStatus());
        }
        userMessage.setUserId(userId);
        userMessage.setExtendJson(JSONUtil.toJsonStr(extendDto));
        this.save(userMessage);


    }

    @Override
    public List<UserMessageCountDto> getNoReadMessageType(String userId) {
        LambdaQueryWrapper<UserMessage> wrapper = Wrappers.<UserMessage>lambdaQuery()
                .eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getReadType, MessageReadTypeEnum.NO_READ.getType());

        List<UserMessage> userMessages = this.list(wrapper);

        // 使用groupingBy按照messageType分组，counting()计算每组的数量
        Map<Integer, Long> messageTypeCountMap = userMessages.stream()
                .collect(Collectors.groupingBy(
                        UserMessage::getMessageType,
                        Collectors.counting()
                ));

        // 将Map转换为所需的DTO列表
        return messageTypeCountMap.entrySet().stream()
                .map(entry -> {
                    UserMessageCountDto dto = new UserMessageCountDto();
                    dto.setMessageType(entry.getKey());
                    dto.setCount(entry.getValue().intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void readAll(String userId, Integer messageType) {
        LambdaUpdateWrapper<UserMessage> updateWrapper = Wrappers.lambdaUpdate(UserMessage.class).eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getMessageType, messageType)
                .set(UserMessage::getReadType, MessageReadTypeEnum.READ.getType());
        this.update(updateWrapper);
    }

    @Override
    public void delMessage(String userId, Integer messageId, Integer messageType) {
        LambdaQueryWrapper<UserMessage> lambdaQueryWrapper = Wrappers.lambdaQuery(UserMessage.class).eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getMessageType, messageType)
                .eq(UserMessage::getMessageId, messageId);
        this.remove(lambdaQueryWrapper);
    }

    @Override
    public Page<UserMessage> loadMessageByType(String userId, Integer messageType, PageRequest pageRequest) {
        Page<UserMessage> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        return userMessageMapper.loadMessageByType(page, userId, messageType);
    }

}




