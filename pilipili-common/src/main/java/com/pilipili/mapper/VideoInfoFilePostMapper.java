package com.pilipili.mapper;

import com.pilipili.Model.entity.VideoInfoFilePost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 15712
* @description 针对表【VideoInfoFilePost(视频文件信息)】的数据库操作Mapper
* @createDate 2024-11-30 20:55:57
* @Entity com.pilipili.Model.entity.VideoInfoFilePost
*/
public interface VideoInfoFilePostMapper extends BaseMapper<VideoInfoFilePost> {

    void deleteBathByFileId(@Param("fileIds") List<String> fileIds,@Param("userId") String userId);

    void insertOrUpdateBatch(@Param("fileInfoList") List<VideoInfoFilePost> fileInfoList);

    Integer getDuration(@Param("videoId") String videoId);

}




