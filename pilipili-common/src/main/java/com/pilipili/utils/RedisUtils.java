package com.pilipili.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Model.Vo.VideoPlayInfoVo;
import com.pilipili.Model.dto.File.UploadFileDto;
import com.pilipili.Model.entity.CategoryInfo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.enums.DateTimePatternEnum;
import com.pilipili.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pilipili.Constant.RedisKeyConstant.*;


/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/25 19:54
 */
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private AppConfig appConfig;


    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 有过期时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     */
    public void set(String key, String value, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * 不设置过期时间
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }


    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }


    /**
     * 删除联系人列表
     *
     * @param userId
     */
    public void delUserContact(String userId) {
        stringRedisTemplate.delete(REDIS_USER_CONTACT_KEY + userId);
    }

    /**
     * 删除联系人
     */
    public void delUserContactInfo(String userId, String contactId) {
        stringRedisTemplate.opsForList().remove(REDIS_USER_CONTACT_KEY + userId, 1, contactId);
    }


    /**
     * 批量插入联系人信息
     */
    public void addUserContactBatch(String userId, List<String> userContactList, Long expireTime, TimeUnit timeUnit) {
        Long aLong = stringRedisTemplate.opsForList().leftPushAll(REDIS_USER_CONTACT_KEY + userId, userContactList);
        stringRedisTemplate.expire(REDIS_USER_CONTACT_KEY + userId, expireTime, timeUnit);
    }

    /**
     * 加入联系人信息
     *
     * @param userId
     */
    public void addUserContact(String userId, String contactId, Long expireTime, TimeUnit timeUnit) {
        List<String> contactList = getContactList(userId);
        if (contactList.contains(contactId)) {
            return;
        }
        stringRedisTemplate.opsForList().leftPush(REDIS_USER_CONTACT_KEY + userId, contactId);
        stringRedisTemplate.expire(REDIS_USER_CONTACT_KEY + userId, expireTime, timeUnit);
    }


    public List<String> getContactList(String userId) {
        List<String> userContact = stringRedisTemplate.opsForList().range(REDIS_USER_CONTACT_KEY + userId, 0, -1);
        return userContact == null ? ListUtil.empty() : userContact;
    }

    /**
     * 设置用户信息缓存
     */
    public void setUserInfo(String userId, String userInfo, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(REDIS_USER_INFO_KEY + userId, userInfo, expireTime, timeUnit);
    }

    /**
     * 获取用户信息
     */
    public String getUserInfo(String userId) {
        return stringRedisTemplate.opsForValue().get(REDIS_USER_INFO_KEY + userId);
    }


    /**
     * 设置管理员token
     */
    public void setAdminTokenInfo(String token, String account, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(REDIS_ADMIN_TOKEN_KEY + token, account, expireTime, timeUnit);
    }


    /**
     * 获取管理员
     *
     * @param token 管理员令牌
     * @return
     */
    public String getAdminTokenInfo(String token) {
        return stringRedisTemplate.opsForValue().get(REDIS_ADMIN_TOKEN_KEY + token);
    }

    /**
     * 清除管理员token
     */
    public void delAdminToken(String token) {
        stringRedisTemplate.delete(REDIS_ADMIN_TOKEN_KEY + token);
    }

    /**
     * 保存分类信息
     *
     * @param categoryInfos 分类信息
     */
    public void saveCategoryInfo(List<CategoryInfo> categoryInfos) {
        //存入列表中
        stringRedisTemplate.opsForValue().set(REDIS_KEY_CATEGORY_INFO, JSONUtil.toJsonStr(categoryInfos));
    }

    /**
     * 获取分类信息
     *
     * @return 分类
     */
    public List<CategoryInfo> getCategoryInfo() {
        String categoryInfo = stringRedisTemplate.opsForValue().get(REDIS_KEY_CATEGORY_INFO);
        return JSONUtil.toList(categoryInfo, CategoryInfo.class);
    }


    /**
     * 预存储视频上传文件
     */
    public String preUploadVideoFile(String userId, String fileName, Integer chunks) {
        String uploadId = RandomUtil.randomString(CommonConstant.RANDOM_STRING_LENGTH15);
        UploadFileDto uploadFileDto = new UploadFileDto();
        uploadFileDto.setUploadId(uploadId);
        uploadFileDto.setChunks(chunks);
        uploadFileDto.setChunkIndex(0);
        String day = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String filePath = day + "/" + userId + uploadId;
        //预上传的文件路径，放到临时文件，后期定期删除
        String folder = appConfig.getFolder() + CommonConstant.FILE_FOLDER + CommonConstant.FILE_FOLDER_TEMP + filePath;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        uploadFileDto.setFilePath(filePath);
        uploadFileDto.setFilePath(CommonConstant.FILE_VIDEO + userId + "/" + fileName);
        stringRedisTemplate.opsForValue().set(REDIS_KEY_UPLOAD_FILE + userId + uploadId, JSONUtil.toJsonStr(uploadFileDto), RedisKeyConstant.REDIS_FILE_EXPIRE_ONE_DAY, TimeUnit.SECONDS);
        return uploadId;
    }

    /**
     * 获取预上传的文件信息
     */
    public UploadFileDto getPreUploadVideoFile(String userId, String uploadId) {
        String uploadFileDtoJson = stringRedisTemplate.opsForValue().get(REDIS_KEY_UPLOAD_FILE + userId + uploadId);
        return JSONUtil.toBean(uploadFileDtoJson, UploadFileDto.class);
    }

    /**
     * 更新文件上传信息
     */
    public void updatePreUploadVideoFile(String userId, UploadFileDto uploadFileDto) {
        stringRedisTemplate.opsForValue().set(REDIS_KEY_UPLOAD_FILE + userId + uploadFileDto.getUploadId(), JSONUtil.toJsonStr(uploadFileDto), RedisKeyConstant.REDIS_FILE_EXPIRE_ONE_DAY, TimeUnit.SECONDS);
    }

    public void delPreUploadVideoFile(String userId, String uploadId) {
        stringRedisTemplate.delete(REDIS_KEY_UPLOAD_FILE + userId + uploadId);
    }

    /**
     * 视频文件删除的消息队列
     * @param videoId
     * @param filePath
     */
    public void addFileToDeleteQueue(String videoId, List<String> filePath) {
        stringRedisTemplate.opsForList().leftPushAll(REDIS_KEY_DELETE_FILE + videoId,filePath);
        //设置过期时间
        stringRedisTemplate.expire(REDIS_KEY_DELETE_FILE + videoId,RedisKeyConstant.REDIS_FILE_EXPIRE_ONE_DAY * 7,TimeUnit.SECONDS);
    }


    /**
     * 获取视频文件删除列表
     * @param videoId
     * @return
     */
    public List<String> getDelFileList(String videoId) {
        String key = REDIS_KEY_DELETE_FILE + videoId;
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 清除视频文件删除列表的缓存
     * @param videoId
     */
    public void clearDelFileList(String videoId) {
        String key = REDIS_KEY_DELETE_FILE + videoId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 添加文件转码任务
     * @param addFileList
     */
    public void addFileToTransferQueue(List<VideoInfoFilePost> addFileList) {
        stringRedisTemplate.opsForList().leftPushAll(REDIS_KEY_TRANSFER_FILE ,JSONUtil.toJsonStr(addFileList));
    }

    /**
     * 获取转码任务
     *
     * @return
     */
    public VideoInfoFilePost getFileFromTransferQueue() {
        String s = stringRedisTemplate.opsForList().rightPop(REDIS_KEY_TRANSFER_FILE);
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        return JSONUtil.toBean(s, VideoInfoFilePost.class);
    }

    public Integer reportVideoPlayOnline(String fileId, String deviceId) {
        String userPlayOnlineKey = String.format(REDIS_KEY_VIDEO_PLAY_COUNT_USER, fileId, deviceId);
        String playOnlineKey = String.format(REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId);
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(userPlayOnlineKey))) {
            stringRedisTemplate.opsForValue().setIfAbsent(userPlayOnlineKey, fileId, REDIS_FILE_EXPIRE_ONE_SECOND * 8, TimeUnit.SECONDS);
            Long newCount = stringRedisTemplate.opsForValue().increment(playOnlineKey);
            // 设置过期时间
            stringRedisTemplate.expire(playOnlineKey, REDIS_FILE_EXPIRE_ONE_SECOND * 10, TimeUnit.SECONDS);
            return Objects.requireNonNull(newCount).intValue();
        }
        //有就续期
        stringRedisTemplate.expire(userPlayOnlineKey, REDIS_FILE_EXPIRE_ONE_SECOND * 8, TimeUnit.SECONDS);
        stringRedisTemplate.expire(playOnlineKey, REDIS_FILE_EXPIRE_ONE_SECOND * 10, TimeUnit.SECONDS);
        return Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(playOnlineKey)));
    }

    /**
     * 减少播放量
     *
     * @param key 播放量key
     */
    public void decrement(String key) {
        stringRedisTemplate.opsForValue().decrement(key);
    }


    /**
     * 增加搜索关键词的计数
     *
     * @param keyword 搜索关键词
     */
    public void addKeyWordCount(String keyword) {
        stringRedisTemplate.opsForZSet().incrementScore(REDIS_KEY_VIDEO_SEARCH_COUNT, keyword, 1);
    }

    public List<String> getKeyWord(Integer top) {
        Set<String> range = stringRedisTemplate.opsForZSet().range(REDIS_KEY_VIDEO_SEARCH_COUNT, 0, top - 1);
        if (range != null) {
            return new ArrayList<>(range);
        }
        return new ArrayList<>();
    }


    /**
     * 添加播放信息
     *
     * @param videoPlayInfoVo
     */
    public void addVideoPlayInfo(VideoPlayInfoVo videoPlayInfoVo) {
        stringRedisTemplate.opsForList().leftPush(REDIS_KEY_QUEUE_VIDEO_PLAY, JSONUtil.toJsonStr(videoPlayInfoVo));
    }


    /**
     * 获取播放信息
     */
    public VideoPlayInfoVo getVideoPlayInfo() {
        String playInfoStr = stringRedisTemplate.opsForList().rightPop(REDIS_KEY_QUEUE_VIDEO_PLAY);
        if (StringUtils.isEmpty(playInfoStr)) {
            return null;
        }
        return JSONUtil.toBean(playInfoStr, VideoPlayInfoVo.class);
    }


    public void recordVideoPlayCount(String videoId) {
        String date = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        stringRedisTemplate.opsForValue().increment(REDIS_KEY_VIDEO_PLAY_COUNT + videoId, 1);
        stringRedisTemplate.expire(REDIS_KEY_VIDEO_PLAY_COUNT + date + ":" + videoId, REDIS_FILE_EXPIRE_ONE_DAY, TimeUnit.SECONDS);
    }

    public Map<String, Integer> getVideoPlayCount(String date) {
        String keyPrefix = REDIS_KEY_VIDEO_PLAY_COUNT + date;
        Map<String, String> batch = getBatch(keyPrefix);
        return batch.entrySet().stream().collect(
                Collectors.toMap(
                        entry -> entry.getKey().substring(entry.getKey().lastIndexOf(":") + 1),
                        entry -> Integer.valueOf(entry.getValue())
                )
        );
    }


    private Map<String, String> getBatch(String keyPrefix) {
        String keys = keyPrefix + "*";
        Set<String> keysSet = stringRedisTemplate.keys(keys);
        if (keysSet == null || keysSet.isEmpty()) {
            return new HashMap<>();
        }
        List<String> keysList = new ArrayList<>(keysSet);
        List<String> keyValueList = stringRedisTemplate.opsForValue().multiGet(keysList);
        return keysList.stream().collect(
                Collectors.toMap(key -> key, value -> keyValueList.get(keysList.indexOf(value)))
        );
    }
}
