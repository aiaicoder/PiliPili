package com.pilipili.service;

import com.pilipili.Model.entity.UserAction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 15712
* @description 针对表【UserAction(用户行为 点赞、评论)】的数据库操作Service
* @createDate 2024-12-28 12:47:08
*/
public interface UserActionService extends IService<UserAction> {

    void saveAction(UserAction userAction);
}
