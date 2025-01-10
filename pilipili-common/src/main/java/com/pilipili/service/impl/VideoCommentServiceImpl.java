package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.dto.Comment.CommentQuery;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.enums.CommentTopTypeEnum;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.enums.UserRoleEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoCommentMapper;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoCommentService;
import com.pilipili.service.VideoInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author 15712
* @description 针对表【VideoComment(评论)】的数据库操作Service实现
* @createDate 2024-12-28 12:46:54
*/
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment>
    implements VideoCommentService{
    @Resource
    private VideoInfoService videoInfoService;


    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Resource
    private VideoCommentMapper videoCommentMapper;


    @Override
    public void postComment(VideoComment videoComment, Integer replayCommentId) {
        String videoId = videoComment.getVideoId();
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains("0")){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR,"UP主以关闭评论区");
        }
        if (replayCommentId != null){
            VideoComment replayComment = this.getById(replayCommentId);
            if (replayComment == null || !videoId.equals(replayComment.getVideoId())){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            //表面当前是一级评论
            if (replayComment.getPCommentId() == 0){
                videoComment.setPCommentId(replayCommentId);
            }else {
                videoComment.setPCommentId(replayComment.getPCommentId());
                videoComment.setReplyUserId(replayComment.getUserId());
            }
            UserInfo replayUser = userInfoService.getById(replayComment.getUserId());
            videoComment.setReplyNikeName(replayUser.getNickName());
            videoComment.setReplyUserAvatar(replayUser.getUserAvatar());
        }else {
            videoComment.setPCommentId(0);
        }
        videoComment.setPostTime(new Date());
        videoComment.setVideoUserId(videoInfo.getUserId());
        this.save(videoComment);
        videoInfoMapper.updateCountInfo(videoId, UserActionTypeEnum.VIDEO_COMMENT.getField(),1);
    }

    @Override
    public Page<VideoComment> findListByPage(Page<VideoComment> page, CommentQuery commentQuery) {
        if (commentQuery.getLoadChildren()) {
            return videoCommentMapper.findListByPageWithChildren(page, commentQuery);
        }
        return videoCommentMapper.findListByPage(page, commentQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void topComment(String userId, Integer commentId) {
        this.cancelTopComment(userId,commentId);
        VideoComment comment = new VideoComment();
        comment.setCommentId(commentId);
        comment.setTopType(CommentTopTypeEnum.TOP.getType());
        updateById(comment);
    }

    @Override
    public void cancelTopComment(String userId, Integer commentId) {
        VideoComment comment = getById(commentId);
        if (comment == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        VideoInfo videoInfo = videoInfoService.getById(comment.getVideoId());
        if (videoInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!userId.equals(comment.getVideoUserId())){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR,"无权限操作");
        }
        UpdateWrapper<VideoComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("videoId",comment.getVideoId())
                .eq("topType",CommentTopTypeEnum.TOP.getType())
                .set("topType",CommentTopTypeEnum.NO_TOP.getType());
        update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(UserInfo loginUser, Integer commentId) {
        VideoComment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!loginUser.getUserId().equals(comment.getVideoUserId()) || !loginUser.getUserId().equals(comment.getUserId())
                || !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())
        ) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }
        this.removeById(commentId);
        videoInfoMapper.updateCountInfo(comment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), -1);
        //删除二级评论
        if (comment.getPCommentId() != 0) {
            QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pCommentId", commentId);
            long count = count(queryWrapper);
            remove(queryWrapper);
            videoInfoMapper.updateCountInfo(comment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), (int) -count);
        }
       
    }

    @Override
    public Page<VideoComment> getVideoCommentList(Page<VideoComment> commentPage, String videoId) {
        return videoCommentMapper.getVideoCommentList(commentPage,videoId);
    }




}




