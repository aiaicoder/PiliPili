package com.pilipili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.VideoInfoPostVo;
import com.pilipili.Model.dto.video.VideoInfoPostListRequest;
import com.pilipili.Model.entity.VideoInfoPost;
import org.apache.ibatis.annotations.Param;

/**
 * @author 15712
 * @description 针对表【VideoInfoPost(视频信息)】的数据库操作Mapper
 * @createDate 2024-11-30 20:56:30
 * @Entity com.pilipili.Model.entity.VideoInfoPost
 */
public interface VideoInfoPostMapper extends BaseMapper<VideoInfoPost> {
    Page<VideoInfoPostVo> getVideoInfoPostVoList(
            Page<VideoInfoPostVo> page,
            @Param("userId") String userId,
            @Param("excludeStatus") Integer[] excludeStatus,
            @Param("videoInfoPostListRequest") VideoInfoPostListRequest videoInfoPostListRequest);
}




