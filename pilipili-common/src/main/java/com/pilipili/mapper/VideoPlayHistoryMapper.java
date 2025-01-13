package com.pilipili.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.VideoPlayHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 15712
* @description 针对表【VideoPlayHistory(视频播放历史)】的数据库操作Mapper
* @createDate 2025-01-09 21:44:41
* @Entity com.pilipili.Model.entity.VideoPlayHistory
*/
public interface VideoPlayHistoryMapper extends BaseMapper<VideoPlayHistory> {

    Page<VideoPlayHistory> getVideoPlayHistoryList(Page<VideoPlayHistory> page, @Param("userId") String userId);
}




