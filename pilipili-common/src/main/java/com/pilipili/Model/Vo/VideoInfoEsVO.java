package com.pilipili.Model.Vo;

import lombok.Data;

import java.util.Date;

/**
 * 视频信息ES存储对象
 * @author 15712
 */
@Data
public class VideoInfoEsVO {
    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 视频封面
     */
    private String videoCover;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 视频标签，逗号分隔
     */
    private String tags;

    /**
     * 播放数
     */
    private Integer playCount;

    /**
     * 弹幕数
     */
    private Integer danMuCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private Date createTime;
}