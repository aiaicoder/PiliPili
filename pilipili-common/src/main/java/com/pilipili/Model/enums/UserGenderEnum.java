package com.pilipili.Model.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/11 20:10
 */

public enum UserGenderEnum {
    F(0, "女"),
    M(1, "男"),
    U(2, "未知");

    private Integer gender;
    private String desc;

    UserGenderEnum(Integer gender, String desc) {
        this.gender = gender;
        this.desc = desc;
    }

    public static String getDescByStatus(Integer gender) {
        for (UserGenderEnum value : UserGenderEnum.values()) {
            if (value.getGenders().equals(gender)) {
                return value.getDesc();
            }
        }
        return null;
    }

    public static UserGenderEnum getStatus(Integer gender) {
        for (UserGenderEnum value : UserGenderEnum.values()) {
            if (value.getGenders().equals(gender)) {
                return value;
            }
        }
        return null;
    }

    public Integer getGenders() {
        return gender;
    }

    public String getDesc() {
        return desc;
    }

}
