package com.pilipili.admin.controller;

import cn.hutool.core.lang.UUID;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Model.dto.user.UserLoginRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.config.AppConfig;
import com.pilipili.exception.BusinessException;
import com.pilipili.manager.RedisLimiterManager;
import com.pilipili.service.impl.UserInfoServiceImpl;
import com.pilipili.utils.NetUtils;
import com.pilipili.utils.RedisUtils;
import com.pilipili.utils.SysSettingUtil;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.pilipili.Constant.CommonConstant.TOKEN_ADMIN;
import static com.pilipili.Constant.RedisKeyConstant.LIMIT_KEY_PREFIX;


/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@RestController
@RequestMapping("/account")
@Slf4j
public class AdminController {


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    @Resource
    private UserInfoServiceImpl userService;

    @Resource
    private SysSettingUtil sysSettingUtil;


    @Resource
    private AppConfig appConfig;

    @GetMapping("/checkCode")
    @ApiOperation(value = "图片验证码")
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
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("admin/login")
    @ApiOperation("管理员登录")
    public BaseResponse<UserInfo> adminUserLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String account = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        String checkCode = userLoginRequest.getCheckCode();
        String checkCodeKey = userLoginRequest.getCheckCodeKey();
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        if (!checkCode.equals(redisUtils.get(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
//            log.error("checkCodeKey:{}", checkCodeKey);
//            redisUtils.delete(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey);
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
//        }
        UserInfo loginUserVo = userService.userLogin(account, password, checkCode, checkCodeKey, null, false);
        return ResultUtils.success(loginUserVo);
    }


    /**
     * 用户登录
     *
     * @param userLoginRequest 没有用框架的写法
     * @return
     */
    /*
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        try {
            String account = userLoginRequest.getEmail();
            String password = userLoginRequest.getPassword();
            String checkCode = userLoginRequest.getCheckCode();
            String checkCodeKey = userLoginRequest.getCheckCodeKey();
            if (StringUtils.isAnyBlank(account, password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            String enCodePassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + password).getBytes());
            if (!checkCode.equals(redisUtils.get(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
                log.error("checkCodeKey:{}", checkCodeKey);
                redisUtils.delete(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
            }
            if (!account.equals(appConfig.getAdminAccount()) || !enCodePassword.equals(DigestUtils.md5DigestAsHex((UserConstant.SALT + appConfig.getAdminPassword()).getBytes())))
            {
                log.error("account:{}", DigestUtils.md5DigestAsHex((UserConstant.SALT + appConfig.getAdminPassword()).getBytes()));
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
            }
            String token = UUID.fastUUID().toString();
            redisUtils.setAdminTokenInfo(token, account, RedisKeyConstant.ADMIN_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
            saveTokenToCookie(token, response);
            return ResultUtils.success(token);
        } finally {
            Cookie[] cookies = request.getCookies();
            String token = null;
            if (cookies!= null){
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(TOKEN_ADMIN)) {
                        token = cookie.getValue();
                    }
                }
                if (StringUtils.isNotBlank(token)) {
                    redisUtils.delAdminToken(token);
                }
            }
        }
    }
     */


    /**
     * 用户注销(使用框架实现的用户注销)
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public BaseResponse<Boolean> userLogout(HttpServletResponse response) {
        deleteTokenFromCookie(response);
        return ResultUtils.success(true);
    }



    private void saveTokenToCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_ADMIN, token);
        cookie.setPath("/");
        //关闭浏览器就失效
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    private void deleteTokenFromCookie(HttpServletResponse response) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_ADMIN)) {
                token = cookie.getValue();
                redisUtils.delAdminToken(token);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                return;
            }
        }
    }

}
