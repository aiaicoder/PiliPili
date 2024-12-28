package com.pilipili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.VideoInfoPostVo;
import com.pilipili.Model.Vo.VideoInfoVo;
import com.pilipili.Model.dto.video.VideoInfoQueryRequest;
import com.pilipili.Model.entity.VideoInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 15712
 * @description 针对表【VideoInfo(视频信息)】的数据库操作Mapper
 * @createDate 2024-11-30 20:51:27
 * @Entity generator.domain.VideoInfo
 */
public interface VideoInfoMapper extends BaseMapper<VideoInfo> {
    int insertOrUpdate(@Param("record") VideoInfo record);

    List<VideoInfoVo> getRecommendVideoList(@Param("recommendType") Integer recommendType);

    Page<VideoInfoVo> loadVideo(Page<VideoInfoVo> page,
            @Param("videoInfoQueryRequest") VideoInfoQueryRequest videoInfoQueryRequest
            , @Param("recommendType") Integer recommendType
    );

    void updateCountInfo( @Param("videoId") String videoId,  @Param("field") String field, @Param("changeCount") Integer changeCount);
}




