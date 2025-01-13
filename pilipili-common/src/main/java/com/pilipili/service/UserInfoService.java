package com.pilipili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.Model.Vo.UserInfoVo;
import com.pilipili.Model.dto.user.UserCountInfoDto;
import com.pilipili.Model.dto.user.UserQueryRequest;
import com.pilipili.Model.entity.UserInfo;

import java.util.List;


/**
* @author 15712
* @description 针对表【userInfo(用户信息表)】的数据库操作Service
* @createDate 2024-11-16 15:09:58
*/
public interface UserInfoService extends IService<UserInfo> {
     UserInfo getUserDetailInfo(String currentUserId, String userId);


    /**
     * 用户注册
     *
     * @param email   用户邮箱
     * @param password  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    String userRegister(String email, String password, String checkPassword,String checkCode,String checkCodeKey);


    /**
     * 重置密码
     * @param email
     * @param password
     * @param checkPassword
     * @param checkCode
     * @param checkCodeKey
     * @return
     */
    Boolean restPassword(String email, String password, String checkPassword, String checkCode, String checkCodeKey);


    /**
     * 用户登录
     *
     * @param email  用户账户
     * @param password 用户密码
     * @return 脱敏后的用户信息
     */
    UserInfo userLogin(String email, String password, String checkCode, String checkCodeKey, String ip, Boolean rememberMe);


    public boolean updateUserInfo(UserInfo loginUser);


    /**
     * 获取当前登录用户
     *
     * @return
     */
    UserInfo getLoginUser();

    /**
     * 获取用户信息不报异常
     * @return
     */
    UserInfo getLoginUserNoEx();


    /**
     * 用户登录
     *
     * @return
     */
    boolean userLogout();



    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserInfoVo getUserInfoVo(UserInfo user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserInfoVo> getUserVoList(List<UserInfo> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<UserInfo> getQueryWrapper(UserQueryRequest userQueryRequest);

    UserCountInfoDto getUserCountInfo(String userId);

    void changeUserStatus(String userId, Integer status);
}
