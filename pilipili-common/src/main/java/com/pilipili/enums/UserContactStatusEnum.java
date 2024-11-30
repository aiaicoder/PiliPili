package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/25 20:15
 */
public enum UserContactStatusEnum {
    NOT_FRIEND(0, "未好友"),
    FRIEND(1, "好友"),
    DEL(2, "已删除好友或退出群聊"),
    DEL_BE(3, "被删除"),
    BLACKLIST(4, "黑名单"),
    BLACKLIST_BE(5, "被拉黑"),
    BLACKLIST_BE_FIRST(6, "首次被拉黑");


    private final Integer status;
    private final String desc;

    UserContactStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }




    public static UserContactStatusEnum getEnumByName(String name) {
        for (UserContactStatusEnum value : UserContactStatusEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static UserContactStatusEnum getByDesc(String desc) {
        for (UserContactStatusEnum value : UserContactStatusEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }

    public static UserContactStatusEnum getEnumByStatus(Integer status) {
        for (UserContactStatusEnum value : UserContactStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
