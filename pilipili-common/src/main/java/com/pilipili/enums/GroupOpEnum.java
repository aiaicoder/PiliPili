package com.pilipili.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum GroupOpEnum {

    REMOVE(0, "移除"),

    ADD(1,"添加");

    private final Integer type;

    private final String desc;

    GroupOpEnum(Integer text, String value) {
        this.type = text;
        this.desc = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.desc).collect(Collectors.toList());
    }

    /**
     * 根据 枚举名称 获取枚举
     *
     * @param name
     * @return
     */
    public static GroupOpEnum getEnumByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            return null;
        }
        for (GroupOpEnum anEnum : GroupOpEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    public static GroupOpEnum getEnumByType(Integer type) {
        if (ObjectUtils.isEmpty(type)) {
            return null;
        }
        for (GroupOpEnum anEnum : GroupOpEnum.values()) {
            if (anEnum.type.equals(type)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getType() {
        return type;
    }
}
