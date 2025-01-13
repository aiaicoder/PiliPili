package com.pilipili.service;

import com.pilipili.Model.entity.UserFocus;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 15712
* @description 针对表【UserFocus】的数据库操作Service
* @createDate 2025-01-01 13:16:30
*/
public interface UserFocusService extends IService<UserFocus> {

    void focusUser(String userId, String focusUserId);
    void cancelFocus(String userId, String focusUserId);

}
