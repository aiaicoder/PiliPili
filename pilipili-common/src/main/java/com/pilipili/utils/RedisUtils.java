package com.pilipili.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Model.dto.File.UploadFileDto;
import com.pilipili.Model.entity.CategoryInfo;
import com.pilipili.Model.enums.DateTimePatternEnum;
import com.pilipili.config.AppConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
}
