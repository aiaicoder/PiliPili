package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/11 20:10
 */

public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDescByStatus(Integer status) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value.getDesc();
            }
        }
        return null;
    }

    public static UserStatusEnum getStatus(Integer status) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value;
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
