package com.pilipili.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.Model.dto.UserMessage.UserMessageCountDto;
import com.pilipili.Model.entity.UserMessage;
import com.pilipili.common.PageRequest;
import com.pilipili.enums.MessageTypeEnum;


import java.util.List;

/**
 * @author 15712
 * @description 针对表【UserMessage(用户消息表)】的数据库操作Service
 * @createDate 2025-01-09 21:44:58
 */
public interface UserMessageService extends IService<UserMessage> {

    void saveMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replayCommentId);

    List<UserMessageCountDto> getNoReadMessageType(String userId);

    void readAll(String userId, Integer messageType);

    void delMessage(String userId, Integer messageId, Integer messageType);

    Page<UserMessage> loadMessageByType(String userId, Integer messageType, PageRequest pageRequest);
}
