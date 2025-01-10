package com.pilipili.enums;

/**
 * 消息阅读状态枚举
 */
public enum MessageReadTypeEnum {
    
    NO_READ(0, "未读"),
    READ(1, "已读");

    private Integer type;
    private String desc;

    MessageReadTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MessageReadTypeEnum getByType(Integer type) {
        for (MessageReadTypeEnum item : MessageReadTypeEnum.values()) {
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
}