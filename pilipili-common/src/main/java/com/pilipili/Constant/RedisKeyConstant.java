package com.pilipili.Constant;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/9 12:40
 */
public interface RedisKeyConstant {


    String REDIS_KEY_PREFIX = "PiliPili:";

    /**
     *
     */
    String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";

    /**
     * 两分钟过期
     */
    Long CHECK_CODE_EXPIRE_TIME = 3L;

    /**
     * 系统设置
     */
    String REDIS_KEY_SYS_SETTING = REDIS_KEY_PREFIX + "sysSetting:";


    /**
     * 用户联系人
     */
    String REDIS_USER_CONTACT_KEY = REDIS_KEY_PREFIX + "userContact:";


    /**
     * 用户信息缓存
     */
    String REDIS_USER_INFO_KEY = REDIS_KEY_PREFIX + "user_info:";


    String REDIS_AI_KEY = "ai:";

    /**
     * 一天的过期时间
     */
    Long REDIS_FILE_EXPIRE_ONE_DAY = 60 * 60L * 24;


    Long REDIS_FILE_EXPIRE_ONE_SECOND = 1L;


    String LIMIT_KEY_PREFIX = REDIS_KEY_PREFIX + "checkCode:limit:";


    //用户上传文件记录
    String REDIS_USER_UPLOAD_FILE_KEY = REDIS_KEY_PREFIX + "user_upload_file:";

    String REDIS_ADMIN_TOKEN_KEY = REDIS_KEY_PREFIX + "admin_token:";


    //1天的过期时间，分钟
    Long ADMIN_TOKEN_EXPIRE_TIME = 24 * 60L;


    String REDIS_KEY_CATEGORY_INFO = REDIS_KEY_PREFIX + "category_infos:";


    String REDIS_KEY_UPLOAD_FILE = REDIS_KEY_PREFIX + "uploading:";

    String REDIS_KEY_DELETE_FILE = REDIS_KEY_PREFIX + "del:video_file:";


    String REDIS_KEY_TRANSFER_FILE = REDIS_KEY_PREFIX + "trans:queue:";

    //视频在线
    String REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX = REDIS_KEY_PREFIX + "video:play:online:";

    String REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE = REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX + "count:%s";

    String REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX = "user:";

    String REDIS_KEY_VIDEO_PLAY_COUNT_USER = REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX + REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX + "%s:%s";


}
