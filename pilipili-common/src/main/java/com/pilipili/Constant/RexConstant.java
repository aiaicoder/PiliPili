package com.pilipili.Constant;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/10 19:21
 * 正则表达式常量
 */
public interface RexConstant {

    /**
     * 邮箱正则表达式
     */
    String EMAIL_REGEX = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 检查名称是否含有特殊字符
     */
    String CHECK_NAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    /**
     * 身份证正则表达式
     */
    String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

}
