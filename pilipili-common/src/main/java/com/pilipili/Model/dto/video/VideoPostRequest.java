package com.pilipili.Model.dto.video;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/3 22:33
 */
@Data
public class VideoPostRequest implements Serializable {

    private static final long serialVersionUID = 7576857654355851289L;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 分类父级id
     */
    private String pCategoryId;

    /**
     * 分类id
     */
    private String categoryId;

    /**
     * 发布类型
     */
    private String postType;


    /**
     * 视频标签
     */
    private String tags;

    /**
     * 视频简介
     */
    private String introduction;

    /**
     * 互动方式
     */
    private String interaction;

    /**
     * 上传文件列表
     */
    private String uploadFileList;
}
