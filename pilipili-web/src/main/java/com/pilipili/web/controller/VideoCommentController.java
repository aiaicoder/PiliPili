package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.VideoCommentResultVo;
import com.pilipili.Model.dto.Comment.CommentQuery;
import com.pilipili.Model.dto.video.VideoCommentPostRequest;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.annotation.RecordUserMessage;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.enums.CommentTopTypeEnum;
import com.pilipili.enums.MessageTypeEnum;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoCommentMapper;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.UserActionService;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoCommentService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/29 16:18
 */
@RestController
@RequestMapping("/videoComment")
public class VideoCommentController {

    @Resource
    private VideoCommentService videoCommentService;


    @Resource
    private UserActionService userActionService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserInfoService userInfoService;


    @PostMapping("/postVideoComment")
    @ApiOperation("发布评论")
    @SaCheckLogin
    @RecordUserMessage(messageType = MessageTypeEnum.COMMENT)
    public BaseResponse<VideoComment> postVideoComment(@RequestBody VideoCommentPostRequest videoCommentPostRequest) {
        if (videoCommentPostRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(videoCommentPostRequest.getVideoId()) || StringUtils.isEmpty(videoCommentPostRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String videoId = videoCommentPostRequest.getVideoId();
        Integer replayCommentId = videoCommentPostRequest.getReplayCommentId();
        String content = videoCommentPostRequest.getContent();
        String imgPath = videoCommentPostRequest.getImgPath();
        VideoComment videoComment = new VideoComment();
        videoComment.setVideoId(videoId);
        videoComment.setContent(content);
        videoComment.setImgPath(imgPath);
        videoComment.setUserId(loginUser.getUserId());
        videoComment.setNikeName(loginUser.getNickName());
        videoComment.setUserAvatar(loginUser.getUserAvatar());
        videoCommentService.postComment(videoComment, replayCommentId);
        return ResultUtils.success(videoComment);
    }



    @GetMapping("/loadVideoComment")
    @ApiOperation("加载评论")
    public BaseResponse<VideoCommentResultVo> loadVideoComment(@NotEmpty String videoId,
                                                               Integer orderType) {
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains("1")){
            return null;
        }
        UserInfo loginUser = userInfoService.getLoginUserNoEx();
        CommentQuery commentQuery = new CommentQuery();
        commentQuery.setVideoId(videoId);
        commentQuery.setPCommentId(0);
        commentQuery.setLoadChildren(true);
        String orderBy = orderType == null ? "likeCount desc,commentId desc":"commentId desc";
        commentQuery.setOrderBy(orderBy);
        Page<VideoComment> page = new Page<>(commentQuery.getCurrent(), commentQuery.getPageSize());
        Page<VideoComment> videoCommentPage = videoCommentService.findListByPage(page, commentQuery);
        //置顶逻辑
        if (commentQuery.getCurrent() == 1){
            List<VideoComment> topComment = topComment(videoId);
            if (ArrayUtil.isNotEmpty(topComment)){
                List<VideoComment> exTopComment = videoCommentPage.getRecords().stream().
                        filter(videoComment ->
                                !videoComment.getCommentId().equals(topComment.get(0).getCommentId())).collect(Collectors.toList());
                exTopComment.addAll(0,topComment);
                videoCommentPage.setRecords(exTopComment);
            }
        }
        VideoCommentResultVo videoCommentResultVo = new VideoCommentResultVo();
        videoCommentResultVo.setVideoComment(videoCommentPage);
        List<UserAction> userActionList = null;
        //如果用户登录之后就获取用户的登录行为
        if (loginUser != null) {
            QueryWrapper<UserAction> queryWrapperU = new QueryWrapper<>();
            queryWrapperU.eq("userId", loginUser.getUserId());
            queryWrapperU.eq("videoId", videoId);
            queryWrapperU.in("actionType", (Object) new Integer[]{UserActionTypeEnum.COMMENT_HATE.getType(),
                    UserActionTypeEnum.COMMENT_LIKE.getType()});
            userActionList = userActionService.list(queryWrapperU);
        }
        videoCommentResultVo.setUserActionList(userActionList);
        return ResultUtils.success(videoCommentResultVo);
    }

    @GetMapping("/topComment")
    @ApiOperation("置顶评论")
    @SaCheckLogin
    public BaseResponse<VideoCommentResultVo> topVideoComment(@NotNull Integer commentId){
        UserInfo loginUser = userInfoService.getLoginUser();
        videoCommentService.topComment(loginUser.getUserId(),commentId);
        return ResultUtils.success(null);
    }

    @GetMapping("/cancelTopComment")
    @ApiOperation("取消置顶评论")
    @SaCheckLogin
    public BaseResponse<VideoCommentResultVo> cancelTopComment(@NotNull Integer commentId){
        UserInfo loginUser = userInfoService.getLoginUser();
        videoCommentService.cancelTopComment(loginUser.getUserId(),commentId);
        return ResultUtils.success(null);
    }

    @GetMapping("/deleteComment")
    @ApiOperation("删除评论")
    @SaCheckLogin
    public BaseResponse<Boolean> deleteComment(@NotNull Integer commentId){
        UserInfo loginUser = userInfoService.getLoginUser();
        videoCommentService.deleteComment(loginUser,commentId);
        return ResultUtils.success(true);
    }




    private List<VideoComment> topComment(String videoId){
        CommentQuery commentQuery = new CommentQuery();
        commentQuery.setVideoId(videoId);
        commentQuery.setTopType(CommentTopTypeEnum.TOP.getType());
        Page<VideoComment> page = new Page<>(commentQuery.getCurrent(), commentQuery.getPageSize());
        Page<VideoComment> videoCommentPage = videoCommentService.findListByPage(page, commentQuery);
        return videoCommentPage.getRecords();
    }


}
