package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.UserInfoMapper;
import com.pilipili.mapper.VideoCommentMapper;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.UserActionService;
import com.pilipili.mapper.UserActionMapper;
import com.pilipili.service.VideoCommentService;
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


    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private VideoCommentMapper videoCommentMapper;

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
            case VIDEO_COIN:
                if (dbUserAction != null){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "本稿件的投币次数已用完");
                }
                if(videoInfo.getUserId().equals(userAction.getUserId())){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能给自己的稿件投币");
                }
                //减少自己的币,把锁加到数据库层面防止并发问题
                Integer updated = userInfoMapper.updateCountInfo(userAction.getUserId(), -userAction.getActionCount());
                if (updated == 0){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "币不足");
                }
                //up主加币
                updated = userInfoMapper.updateCountInfo(videoInfo.getUserId(), userAction.getActionCount());
                if (updated == 0){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "投币失败");
                }
                this.save(userAction);
                videoInfoMapper.updateCountInfo(videoId, actionTypeEnum.getField(), userAction.getActionCount());
                break;
            case COMMENT_HATE:
            case COMMENT_LIKE:
                UserActionTypeEnum oppositeActionTypeEnum = actionTypeEnum ==
                        UserActionTypeEnum.COMMENT_LIKE ? UserActionTypeEnum.COMMENT_HATE : UserActionTypeEnum.COMMENT_LIKE;
                LambdaQueryWrapper<UserAction> queryWrapperU = Wrappers.lambdaQuery(UserAction.class).
                        eq(UserAction::getVideoId, videoId)
                        .eq(UserAction::getUserId,userAction.getUserId())
                        .eq(UserAction::getActionType, oppositeActionTypeEnum.getType())
                        .eq(UserAction::getCommentId, userAction.getCommentId());
                UserAction oppositeAction = getOne(queryWrapperU);
                //先找到相反的点赞或踩，如果存在就直接删除
                if (oppositeAction != null){
                    removeById(oppositeAction.getActionId());
                }
                if (dbUserAction != null){
                    removeById(dbUserAction.getActionId());
                }else{
                    this.save(userAction);
                }

                changeCount = dbUserAction == null ? 1 : -1;
                Integer opChangeCount = -changeCount;
                videoCommentMapper.updateCountInfo(userAction.getCommentId(), actionTypeEnum.getField(),changeCount,oppositeAction == null ? null : oppositeActionTypeEnum.getField(),opChangeCount);


        }


    }
}




