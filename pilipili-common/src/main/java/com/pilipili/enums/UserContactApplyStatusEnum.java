package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/4 19:45
 */
public enum UserContactApplyStatusEnum {
    INIT(0, "待处理"),
    AGREE(1, "已同意"),
    REFUSE(2, "已拒绝"),
    BLACKLIST(3, "已拉黑");

    private final Integer status;
    private final String desc;

    UserContactApplyStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static UserContactApplyStatusEnum getEnumByStatus(Integer status) {
        for (UserContactApplyStatusEnum value : values()) {
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
