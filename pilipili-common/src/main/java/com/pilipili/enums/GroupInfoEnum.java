package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/29 20:38
 */
public enum GroupInfoEnum {
    NORMAL(0, "正常"),
    DISMISSAL(1, "解散");

    private final Integer status;
    private final String desc;

    GroupInfoEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static GroupInfoEnum getEnumByStatus(Integer status) {
        for (GroupInfoEnum value : values()) {
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
