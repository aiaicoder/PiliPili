package com.pilipili.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Constant.RedisKeyConstant;
import com.pilipili.Constant.RexConstant;
import com.pilipili.Constant.UserConstant;
import com.pilipili.common.ErrorCode;
import com.pilipili.exception.BusinessException;
import com.pilipili.utils.RedisUtils;
import com.pilipili.utils.SqlUtils;
import com.pilipili.utils.StringUtil;
import com.pilipili.Model.Vo.UserInfoVo;
import com.pilipili.Model.dto.user.UserQueryRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.enums.UserGenderEnum;
import com.pilipili.Model.enums.UserStatusEnum;
import com.pilipili.mapper.UserInfoMapper;
import com.pilipili.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.pilipili.Constant.UserConstant.*;


/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String userRegister(String email, String password, String checkPassword, String checkCode, String checkCodeKey) {
        // 1. 校验
        if (StringUtils.isAnyBlank(email, password, checkPassword, checkCode, checkCodeKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!checkCode.equals(redisUtils.get(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
            log.error("checkCodeKey:{}", checkCodeKey);
            redisUtils.delete(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
        }
        if (password.length() < 8 || checkPassword.length() < 8 || checkPassword.length() > 32 || password.length() > 32) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //检测账号是否包含特殊字符
        String validPattern = RexConstant.EMAIL_REGEX;
        Matcher matcher = Pattern.compile(validPattern).matcher(email);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法邮箱");
        }
        //生成用户id
        String userId = StringUtil.getUserId();
        synchronized (email.intern()) {
            // 账户不能重复
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 3. 插入数据
            Date curTime = new Date();
            UserInfo user = new UserInfo();
            user.setUserId(userId);
            user.setPassword(encryptPassword);
            //插入默认头像和默认姓名
            user.setUserAvatar(DEFAULT_AVATAR);
            user.setNickName(DEFAULT_USERNAME);
            user.setEmail(email);
            user.setSex(UserGenderEnum.U.getGenders());
            user.setStatus(UserStatusEnum.ENABLE.getStatus());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getUserId();
        }
    }

    @Override
    public UserInfo userLogin(String email, String password, String checkCode, String checkCodeKey,String ip, Boolean rememberMe) {
        // 1. 校验
        if (StringUtils.isAnyBlank(email, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码为空");
        }
        if (email.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        if (!checkCode.equals(redisUtils.get(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
            log.error("checkCodeKey:{}", checkCodeKey);
            redisUtils.delete(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
//         查询用户是否存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("password", encryptPassword);
        UserInfo user = this.getOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, email cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        } else if (user.getStatus().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已被封禁");
        }
        //是否记住我
        if (rememberMe) {
            StpUtil.login(user.getUserId());
        } else {
            StpUtil.login(user.getUserId(), new SaLoginModel()
                    .setIsLastingCookie(false)        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
                    .setToken(UUID.randomUUID().toString()) // 预定此次登录的生成的Token
                    .setIsWriteHeader(false));              // 是否在登录后将 Token 写入到响应头);
        }
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        //更新用户信息
        this.updateById(user);
        // todo 可能要更新redis
        //设置token，返回给前端
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        user.setToken(tokenInfo.getTokenValue());
        // 3. 记录用户的登录态
        StpUtil.getSession().set(USER_LOGIN_STATE, user);
        redisUtils.delete(RedisKeyConstant.REDIS_KEY_CHECK_CODE + checkCodeKey);
        return user;
    }


    @Override
    public Boolean restPassword(String email, String password, String checkPassword, String checkCode, String checkCodeKey) {
        // 1. 校验
        if (StringUtils.isAnyBlank(email, password, checkPassword, checkCode, checkCodeKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (password.length() < 8 || checkPassword.length() < 8 || checkPassword.length() > 32 || password.length() > 32) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //检测账号是否包含特殊字符
        String validPattern = RexConstant.EMAIL_REGEX;
        Matcher matcher = Pattern.compile(validPattern).matcher(email);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法邮箱");
        }
        // 更新密码
        UpdateWrapper<UserInfo> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("email", email);
        userUpdateWrapper.set("password", DigestUtils.md5DigestAsHex((SALT + password).getBytes()));
        return this.update(userUpdateWrapper);
    }


    @Override
    public UserInfo getLoginUser() {
        // 先判断是否已登录
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        UserInfo currentUser = (UserInfo) userObj;
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserInfo getLoginUserNoEx() {
        // 先判断是否已登录
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        return (UserInfo) userObj;
    }


    /**
     * 用户登陆注销(通过框架)
     * @return
     */
    @Override
    public boolean userLogout() {
        if (StpUtil.getLoginIdDefaultNull() == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        UserInfo loginUser = getLoginUser();
        StpUtil.logout();
        return true;
    }


    @Override
    public UserInfoVo getUserInfoVo(UserInfo user) {
        if (user == null) {
            return null;
        }
        UserInfoVo userVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public List<UserInfoVo> getUserVoList(List<UserInfo> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserInfoVo).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<UserInfo> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String email  = userQueryRequest.getEmail();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "NickName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(UserInfo loginUser) {
        boolean b = updateById(loginUser);
        //更新保存的用户登录态
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE,loginUser);
        return b;
    }

}
