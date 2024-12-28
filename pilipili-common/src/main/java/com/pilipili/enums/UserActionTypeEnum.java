package com.pilipili.enums;

/**
 * 用户行为枚举
 * @author 15712
 */
public enum UserActionTypeEnum {
    COMMENT_LIKE(0, "likeCount", "评论喜欢点赞"),
    COMMENT_HATE(1, "hateCount", "评论讨厌"),
    VIDEO_LIKE(2, "likeCount", "视频点赞"),
    VIDEO_COLLECT(3, "collectCount", "视频收藏"),
    VIDEO_COIN(4, "coinCount", "视频投币"),
    VIDEO_COMMENT(5, "commentCount", "视频评论数"),
    VIDEO_DANMU(6, "danMuCount", "弹幕数量"),
    VIDEO_PLAY(7, "playCount", "视频播放数");

    private Integer type;
    private String field;
    private String desc;

    UserActionTypeEnum(Integer type, String field, String desc) {
        this.type = type;
        this.field = field;
        this.desc = desc;
    }

    public static UserActionTypeEnum getByType(Integer type) {
        for (UserActionTypeEnum item : UserActionTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getField() {
        return field;
    }
}