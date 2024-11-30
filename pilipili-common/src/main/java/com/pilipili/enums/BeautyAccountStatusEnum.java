package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/12 18:16
 */
public enum BeautyAccountStatusEnum {
    NO_USE(0,"未使用"),
    USING(1,"使用中");
    private Integer status;
    private String desc;
    BeautyAccountStatusEnum(Integer status,String desc)
    {
        this.status = status;
        this.desc = desc;
    }
    public static BeautyAccountStatusEnum getEnumByStatus(Integer status)
    {
        for (BeautyAccountStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
    public Integer getStatus()
    {
        return status;
    }

    public String getDesc()
    {
        return desc;
    }
}
