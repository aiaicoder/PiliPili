package com.pilipili.common;

/**
 * 自定义错误码
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
     NOT_LOGIN_ERROR(40100, "未登录"),
    BE_REPLACED_MESSAGE(40103,"账号异地登录"),

    TOKEN_TIMEOUT_MESSAGE(40104,"登录过期"),
    KICK_OUT_ERROR(40105,"你已被踢下线"),
    TOKEN_FREEZE_ERROR(40106,"账号已被冻结"),
    USING_ERROR(40106,"用户已登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    AI_ERROR(50002, "AI 错误"),
    INVALID_TOKEN_ERROR(40102,"登录失效或未登录"),
    TOO_MANY_REQUEST(50011,"请求超过限制");



    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
