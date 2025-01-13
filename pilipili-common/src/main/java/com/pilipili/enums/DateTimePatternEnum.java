package com.pilipili.enums;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/30 14:05
 */
public enum DateTimePatternEnum {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYYMMDD("yyyy-MM-dd"),
    _YYYYMMDD("yyyy/MM/dd"),
    YYYYMM("yyyyMM");
    private String pattern;
    DateTimePatternEnum(String pattern) {
        this.pattern = pattern;
    }
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
