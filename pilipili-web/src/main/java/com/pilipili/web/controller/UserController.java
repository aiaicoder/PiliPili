package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Constant.UserConstant;
import com.pilipili.Model.dto.user.*;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.exception.ThrowUtils;
import com.pilipili.utils.NetUtils;
import com.pilipili.utils.RedisUtils;
import com.pilipili.utils.SysSettingUtil;
import com.pilipili.Model.Vo.UserInfoVo;

import com.pilipili.Model.entity.UserInfo;
import com.pilipili.config.AppConfig;
import com.pilipili.manager.RedisLimiterManager;
import com.pilipili.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.pilipili.Constant.RedisKeyConstant.LIMIT_KEY_PREFIX;
import static com.pilipili.Constant.UserConstant.SALT;


/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserInfoService userService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    @Resource
    private SysSettingUtil sysSettingUtil;


    @Resource
    private AppConfig appConfig;

    @GetMapping("/checkCode")
    @ApiOperation(value ="图片验证码")
    public BaseResponse<Map<String, String>> checkCode(HttpServletRequest request) {
        String ipAddress = NetUtils.getIpAddress(request);
        ipAddress = ipAddress.replaceAll(":", ".");
        boolean rateLimit = redisLimiterManager.doRateLimit(LIMIT_KEY_PREFIX + ipAddress);
        if (!rateLimit) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "验证码获取过于频繁");
        }
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 42);
        String checkCodeKey = UUID.fastUUID().toString();
        String code = captcha.text();
        log.info("验证码是：{}", code);
        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        //设置验证码到redis，并且设置过期时间
        redisUtils.set(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey, code, RedisKeyConstant.CHECK_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        return ResultUtils.success(result);
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String checkCode = userRegisterRequest.getCheckCode();
        String checkCodeKey = userRegisterRequest.getCheckCodeKey();
        if (StringUtils.isAnyBlank(email, password, checkPassword, checkCode, checkCodeKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数缺失");
        }
        String result = userService.userRegister(email, password, checkPassword, checkCode, checkCodeKey);
        return ResultUtils.success(result);
    }

    @PostMapping("/resetPassword")
    @ApiOperation("重置密码")
    public BaseResponse<Boolean> resetPassword(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String checkCode = userRegisterRequest.getCheckCode();
        String checkCodeKey = userRegisterRequest.getCheckCodeKey();
        if (StringUtils.isAnyBlank(email, password, checkPassword, checkCode, checkCodeKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数缺失");
        }
        Boolean result = userService.restPassword(email, password, checkPassword, checkCode, checkCodeKey);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败请检查邮箱是否正确");
        }
        //修改密码后重新登录
        userService.userLogout();
        return ResultUtils.success(true);
    }


    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<UserInfo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        String checkCode = userLoginRequest.getCheckCode();
        String checkCodeKey = userLoginRequest.getCheckCodeKey();
        Boolean rememberMe = userLoginRequest.getRememberMe();
        String ipAddress = NetUtils.getIpAddress(request);
        ipAddress = ipAddress.replaceAll(":", ".");
        if (StringUtils.isAnyBlank(email, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUserVo = userService.userLogin(email, password, checkCode, checkCodeKey,ipAddress, rememberMe);
        //todo 设置粉丝数，关注数，硬币数
        return ResultUtils.success(loginUserVo);
    }


    /**
     * 用户注销(使用框架实现的用户注销)
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public BaseResponse<Boolean> userLogout() {
        boolean result = userService.userLogout();
        return ResultUtils.success(result);
    }


    @PostMapping("/updatePassword")
    @ApiOperation("更新密码")
    @SaCheckLogin
    public BaseResponse<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String password = updatePasswordRequest.getPassword();
        String checkPassword = updatePasswordRequest.getCheckPassword();
        if (StringUtils.isBlank(password) || StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入新密码并确认密码");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }
        String newEncryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        UserInfo loginUser = userService.getLoginUser();
        loginUser.setPassword(newEncryptPassword);
        userService.updateById(loginUser);
        //修改密码后重新登录
        userService.userLogout();
        return ResultUtils.success("修改成功");
    }


    /**
     * 获取当前登录用户
     *
     * @param
     * @return
     */
    @GetMapping("/get/login")
    @ApiOperation("获取用户登录信息")
    public BaseResponse<UserInfoVo> getLoginUser() {
        UserInfo user = userService.getLoginUser();
        return ResultUtils.success(userService.getUserInfoVo(user));
    }


    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @return
     */
    @PostMapping("/update/my")
    @ApiOperation("更新用户信息")
    @SaCheckLogin
    public BaseResponse<UserInfoVo> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userService.getLoginUser();
        BeanUtils.copyProperties(userUpdateMyRequest, loginUser);
        String userInfo = redisUtils.getUserInfo(loginUser.getUserId());
        if (StringUtils.isNotBlank(userInfo)) {
            UserInfo oldUserInfo = JSONUtil.toBean(userInfo, UserInfo.class);
            if (!oldUserInfo.getEmail().equals(loginUser.getEmail())){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持修改邮箱");
            }
        }
        System.out.println("更新用户" + loginUser);
        boolean result = userService.updateUserInfo(loginUser);
        //更新完用户状态重新修改用户信息
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE, loginUser);
        redisUtils.setUserInfo(loginUser.getUserId(), JSONUtil.toJsonStr(loginUser),appConfig.tokenTimeout,TimeUnit.SECONDS);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        UserInfo newLoginUser = userService.getLoginUser();
        //只返回最新的用户信息
        UserInfoVo loginUserVo = userService.getUserInfoVo(newLoginUser);
        return ResultUtils.success(loginUserVo);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("获取用户列表信息")
    @SaCheckRole
    public BaseResponse<Page<UserInfoVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInfo> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserInfoVo> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserInfoVo> userVo = userService.getUserVoList(userPage.getRecords());
        userVoPage.setRecords(userVo);
        return ResultUtils.success(userVoPage);
    }

}
