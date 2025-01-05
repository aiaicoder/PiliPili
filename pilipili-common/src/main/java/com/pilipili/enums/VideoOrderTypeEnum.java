package com.pilipili.enums;

public enum VideoOrderTypeEnum {
    CREATE_TIME(0, "createTime", "最新发布"),
    PLAY_COUNT(1, "playCount", "最多播放"),
    COLLECT_COUNT(2, "collectCount", "最多收藏");

    private Integer type;
    private String field;
    private String desc;

    VideoOrderTypeEnum(Integer type, String field, String desc) {
        this.type = type;
        this.field = field;
        this.desc = desc;
    }

    public static VideoOrderTypeEnum getByType(Integer type) {
        for (VideoOrderTypeEnum item : VideoOrderTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() { return type; }
    public String getDesc() { return desc; }
    public String getField() { return field; }
}