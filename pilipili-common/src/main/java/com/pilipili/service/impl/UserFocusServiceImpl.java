package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserFocus;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.exception.BusinessException;
import com.pilipili.service.UserFocusService;
import com.pilipili.mapper.UserFocusMapper;
import com.pilipili.service.UserInfoService;
import org.apache.catalina.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.Date;

import static java.nio.file.Files.delete;

/**
* @author 15712
* @description 针对表【UserFocus】的数据库操作Service实现
* @createDate 2025-01-01 13:16:30
*/
@Service
public class UserFocusServiceImpl extends ServiceImpl<UserFocusMapper, UserFocus>
    implements UserFocusService{

    @Resource
    @Lazy
    private UserInfoService userInfoService;

    @Resource
    private UserFocusMapper userFocusMapper;

    @Override
    public void focusUser(String userId, String focusUserId) {
        if (userId.equals(focusUserId)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能关注自己");
        }
        LambdaQueryWrapper<UserFocus> query = Wrappers.lambdaQuery(UserFocus.class).
                eq(UserFocus::getUserId, userId)
                .eq(UserFocus::getFocusUserId, focusUserId);
        if (this.getOne(query) != null){
            return;
        }
        UserFocus userFocus = new UserFocus();
        userFocus.setUserId(userId);
        userFocus.setFocusUserId(focusUserId);
        userFocus.setFocusTime(new Date());
        this.save(userFocus);
    }

    @Override
    public void cancelFocus(String userId, String focusUserId) {
        remove(Wrappers.lambdaQuery(UserFocus.class).
                eq(UserFocus::getUserId, userId)
                .eq(UserFocus::getFocusUserId, focusUserId));
    }


}




