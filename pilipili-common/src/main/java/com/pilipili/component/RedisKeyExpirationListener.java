package com.pilipili.component;

import com.pilipili.Constant.CommonConstant;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Constant.UserConstant;
import com.pilipili.utils.RedisUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/1 11:40
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private RedisUtils redisUtils;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (!key.contains(RedisKeyConstant.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX+RedisKeyConstant.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX )){
            return;
        };
        int userIndex = key.indexOf(RedisKeyConstant.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX) + RedisKeyConstant.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX.length();
        String fileId = key.substring(userIndex, userIndex + CommonConstant.RANDOM_STRING_LENGTH20);
        redisUtils.decrement(String.format(RedisKeyConstant.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId));

    }
}
