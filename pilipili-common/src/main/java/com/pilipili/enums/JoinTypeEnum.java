package com.pilipili.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
public enum JoinTypeEnum {

    JOIN(0, "直接加入"),

    APPLY(1,"需要审核");

    private final Integer type;

    private final String desc;

    JoinTypeEnum(Integer text, String value) {
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
    public static JoinTypeEnum getEnumByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            return null;
        }
        for (JoinTypeEnum anEnum : JoinTypeEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    public static JoinTypeEnum getEnumByType(Integer type) {
        if (ObjectUtils.isEmpty(type)) {
            return null;
        }
        for (JoinTypeEnum anEnum : JoinTypeEnum.values()) {
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
