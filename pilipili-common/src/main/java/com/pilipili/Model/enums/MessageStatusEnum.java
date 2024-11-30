package com.pilipili.Model.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/18 19:43
 */
public enum MessageStatusEnum {
    SENDING(0, "发送中"),
    SENDED(1, "已发送"),
    RECALLED(2, "已撤回");


    private Integer status;
    private String desc;

    MessageStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static MessageStatusEnum getEnumByStatus(int status) {
        for (MessageStatusEnum messageStatusEnum : MessageStatusEnum.values()) {
            if (messageStatusEnum.getStatus() == status) {
                return messageStatusEnum;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }


}
