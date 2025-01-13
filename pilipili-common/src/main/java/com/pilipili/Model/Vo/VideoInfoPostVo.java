package com.pilipili.Model.Vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.pilipili.enums.VideoStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 9:45
 */
@Data
public class VideoInfoPostVo implements Serializable {
    /**
     * 视频ID
     */
    @TableId
    private String videoId;

    /**
     * 视频封面
     */
    private String videoCover;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    /**
     * 父级分类ID
     */
    private Integer pCategoryId;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败
     */
    private Integer status;

    /**
     * 0:自制作 1:转载
     */
    private Integer postType;

    /**
     * 原资源说明
     */
    private String originInfo;

    /**
     * 标签
     */
    private String tags;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 互动设置
     */
    private String interaction;

    /**
     * 持续时间（秒）
     */
    private Integer duration;

    /**
     * 播放数量
     */
    private Integer playCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 弹幕数量
     */
    private Integer danMuCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 投币数量
     */
    private Integer coinCount;

    /**
     * 收藏数量
     */
    private Integer collectCount;

    /**
     * 是否推荐0:未推荐 1:已推荐
     */
    private Integer recommendType;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String nickName;

    @JsonProperty("statusName")  // 指定 JSON 属性名
    public String getStatusName() {
        if (this.status == null) {
            return null;
        }
        VideoStatusEnum statusEnum = VideoStatusEnum.getByStatus(this.status);
        return statusEnum != null ? statusEnum.getDesc() : null;
    }
}
