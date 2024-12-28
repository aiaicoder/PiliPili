package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.UserActionService;
import com.pilipili.mapper.UserActionMapper;
import com.pilipili.service.VideoInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author 15712
* @description 针对表【UserAction(用户行为 点赞、评论)】的数据库操作Service实现
* @createDate 2024-12-28 12:47:08
*/
@Service
public class UserActionServiceImpl extends ServiceImpl<UserActionMapper, UserAction>
    implements UserActionService{

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Override
    public void saveAction(UserAction userAction) {
        String videoId = userAction.getVideoId();
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        userAction.setVideoUserId(videoInfo.getUserId());
        Integer actionType = userAction.getActionType();
        UserActionTypeEnum actionTypeEnum = UserActionTypeEnum.getByType(actionType);
        if (actionTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        queryWrapper.eq("userId", userAction.getUserId());
        queryWrapper.eq("actionType", userAction.getActionType());
        if (userAction.getCommentId() != null){
            queryWrapper.eq("commentId", userAction.getCommentId());
        }
        UserAction dbUserAction = this.getOne(queryWrapper);
        userAction.setActionTime(new Date());
        switch (actionTypeEnum){
            case VIDEO_LIKE:
            case VIDEO_COLLECT:
                if(dbUserAction != null){
                    this.removeById(dbUserAction.getActionId());
                }else{
                    this.save(userAction);
                }
                Integer changeCount = dbUserAction == null ? 1 : -1;
                videoInfoMapper.updateCountInfo(videoId, actionTypeEnum.getField(), changeCount);
                if (actionTypeEnum == UserActionTypeEnum.VIDEO_COLLECT){
                    //todo 更新es的收藏数量
                }
                break;

        }


    }
}




