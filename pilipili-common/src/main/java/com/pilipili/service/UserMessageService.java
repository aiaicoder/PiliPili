package com.pilipili.service;

import com.pilipili.Model.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.enums.MessageTypeEnum;

/**
* @author 15712
* @description 针对表【UserMessage(用户消息表)】的数据库操作Service
* @createDate 2025-01-09 21:44:58
*/
public interface UserMessageService extends IService<UserMessage> {

    void saveMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replayCommentId);
}
