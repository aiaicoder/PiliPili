package com.pilipili.enums;

public enum StatisticsTypeEnum {
    
    PLAY(0, "播放量"),
    FANS(1, "粉丝"),
    LIKE(2, "点赞"),
    COLLECTION(3, "收藏"),
    COIN(4, "投币"),
    COMMENT(5, "评论"),
    DANMU(6, "弹幕");

    private final Integer type;
    private final String desc;

    StatisticsTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static StatisticsTypeEnum getByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (StatisticsTypeEnum value : StatisticsTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
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
}