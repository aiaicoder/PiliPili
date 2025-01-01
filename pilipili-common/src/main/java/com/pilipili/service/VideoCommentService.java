package com.pilipili.service;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.dto.Comment.CommentQuery;
import com.pilipili.Model.entity.VideoComment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 15712
* @description 针对表【VideoComment(评论)】的数据库操作Service
* @createDate 2024-12-28 12:46:54
*/
public interface VideoCommentService extends IService<VideoComment> {

    void postComment(VideoComment videoComment, Integer replayCommentId);

    Page<VideoComment> findListByPage(Page<VideoComment> page, CommentQuery commentQuery);

    void topComment(String userId, Integer commentId);

    void cancelTopComment(String userId, Integer commentId);

    void deleteComment(String userId, Integer commentId);
}
