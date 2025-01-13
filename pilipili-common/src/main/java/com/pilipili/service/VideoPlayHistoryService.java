package com.pilipili.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.VideoPlayHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.common.PageRequest;

/**
* @author 15712
* @description 针对表【VideoPlayHistory(视频播放历史)】的数据库操作Service
* @createDate 2025-01-09 21:44:41
*/
public interface VideoPlayHistoryService extends IService<VideoPlayHistory> {

    void savePlayHistory(String videoId, String userId, Integer fileIndex);

    Page<VideoPlayHistory> getVideoPlayHistoryList(String userId, PageRequest pageRequest);

    void deleteVideoPlayHistory(String userId, String videoId);
}
