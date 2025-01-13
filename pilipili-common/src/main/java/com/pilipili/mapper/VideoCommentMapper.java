package com.pilipili.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.dto.Comment.CommentQuery;
import com.pilipili.Model.entity.VideoComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 15712
* @description 针对表【VideoComment(评论)】的数据库操作Mapper
* @createDate 2024-12-28 12:46:54
* @Entity com.pilipili.Model.entity.VideoComment
*/
public interface VideoCommentMapper extends BaseMapper<VideoComment> {

    Page<VideoComment> findListByPageWithChildren(Page<VideoComment> page, @Param("commentQuery") CommentQuery commentQuery);

    Page<VideoComment> findListByPage(Page<VideoComment> page, @Param("commentQuery") CommentQuery commentQuery);

    void updateCountInfo(@Param("commentId") Integer commentId, @Param("field") String field, @Param("changeCount")Integer changeCount, @Param("oppositeField")String oppositeField, @Param("opChangeCount")Integer opChangeCount);

    Page<VideoComment> getVideoCommentList(Page<VideoComment> commentPage, @Param("commentQuery") CommentQuery commentQuery);
}




