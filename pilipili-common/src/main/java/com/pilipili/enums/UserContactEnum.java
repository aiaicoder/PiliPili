package com.pilipili.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/10 19:55
 */
public enum UserContactEnum {
    USER(0,"U","好友"),
    GROUP(1,"G","群组");

    private Integer type;
    private String prefix;
    private String desc;

    UserContactEnum(Integer type, String prefix, String desc) {
        this.type = type;
        this.prefix = prefix;
        this.desc = desc;
    }
    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getTypes() {
        return Arrays.stream(values()).map(item -> item.type).collect(Collectors.toList());
    }

    /**
     * 根据 枚举名 获取枚举
     *
     * @param name
     * @return
     */
    public static UserContactEnum getEnumByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            return null;
        }
        for (UserContactEnum anEnum : UserContactEnum.values()) {
            if (anEnum.name().equals(name.toUpperCase())) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据前缀获取枚举
     * @return
     */
    public static UserContactEnum getEnumByPrefix(String prefix) {
        //去除前后空格
        if (ObjectUtils.isEmpty(prefix) || prefix.trim().length() == 0) {
            return null;
        }
        prefix = prefix.substring(0,1);//只获取前面的一个字母
        for (UserContactEnum anEnum : UserContactEnum.values()) {
            if (anEnum.prefix.equals(prefix.toUpperCase())) {
                return anEnum;
            }
        }
        return null;
    }



    public Integer getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDesc() {
        return desc;
    }
}
