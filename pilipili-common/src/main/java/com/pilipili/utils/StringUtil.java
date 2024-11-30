package com.pilipili.utils;

import com.pilipili.Constant.UserConstant;
import com.pilipili.enums.UserContactEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/10 19:48
 */
public class StringUtil {

    public static String getUserId() {
        return UserContactEnum.USER.getPrefix() + RandomStringUtils.random(UserConstant.ID_LENGTH, false, true);
    }

    public static String getGroupId() {
        return UserContactEnum.GROUP.getPrefix() + RandomStringUtils.random(UserConstant.ID_LENGTH, false, true);
    }

    /**
     * 获取用户之间的会话id
     * @param userIds
     * @return
     */
    public static String getSessionId(String[] userIds) {
        //先对userIds进行排序
        Arrays.sort(userIds);
        //进行md5加密,保证了唯一姓，即使删除了好友重新添加也不会改变
        return DigestUtils.md5DigestAsHex(StringUtils.join(userIds,"").getBytes());
    }

    /**
     * 获取群组的会话id
     *
     * @param groupId
     * @return
     */
    public static String getSessionIdGroup(String groupId) {
        return DigestUtils.md5DigestAsHex(groupId.getBytes());
    }

    public static String htmlEscape(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        content = content.replaceAll("&", "&amp;");
        content = content.replaceAll("<", "&lt;");
        content = content.replaceAll(">", "&gt;");
        content = content.replaceAll("\r\n", "<br>");
        content = content.replaceAll("\n", "<br>");
        return content.replaceAll(" ", "&nbsp;");
    }

    public static boolean pathIsOk(String path){
        if (StringUtils.isEmpty(path)){
            return false;
        }
        if (path.contains("../") || path.contains("..\\")){
            return false;
        }
        return true;
    }
}
