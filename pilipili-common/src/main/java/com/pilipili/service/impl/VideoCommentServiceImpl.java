package com.pilipili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.VideoComment;
import com.pilipili.service.VideoCommentService;
import com.pilipili.mapper.VideoCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 15712
* @description 针对表【VideoComment(评论)】的数据库操作Service实现
* @createDate 2024-12-28 12:46:54
*/
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment>
    implements VideoCommentService{

}




