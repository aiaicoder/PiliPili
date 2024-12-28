package com.pilipili.Constant;

/**
 * 通用常量
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "asc";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "desc";

    /**
     * 大小单位
     */
    Long MB_SIZE = 1024 * 1024L;

    /**
     * 随机字符长度
     */
    int RANDOM_STRING_LENGTH15 = 15;
    int RANDOM_STRING_LENGTH20 = 20;
    int RANDOM_STRING_LENGTH30 = 30;


    String TOKEN_ADMIN = "adminToken";

    /**
     * 文件目录
     */
    String FILE_FOLDER = "file/";

    /**
     * 缩略图目录
     */
    String FILE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";

    /**
     * 封面
     */
    String FILE_COVER = "cover/";

    /**
     * 视频
     */
    String FILE_VIDEO = "video/";

    /**
     * 临时文件目录
     */
    String FILE_FOLDER_TEMP = "temp/";


    /**
     * 临时mp4文件
     */
    String TEMP_VIDEO_NAME = "/temp.mp4";

    /**
     * 视频编码格式
     */
    String VIDEO_CODE_HEVC = "hevc";

    /**
     * 临时文件后缀
     */
    String VIDEO_CODE_TEMP_FILE_SUFFIX = "_temp";


    /**
     * ts文件后缀
     */
    String TS_NAME = "index.ts";


    /**
     * m3u8
     */
    String M3U8_NAME = "index.m3u8";



}
