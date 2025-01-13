package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.VideoPlayHistory;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.PageRequest;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoPlayHistoryMapper;
import com.pilipili.service.VideoPlayHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 15712
 * @description 针对表【VideoPlayHistory(视频播放历史)】的数据库操作Service实现
 * @createDate 2025-01-09 21:44:41
 */
@Service
public class VideoPlayHistoryServiceImpl extends ServiceImpl<VideoPlayHistoryMapper, VideoPlayHistory>
        implements VideoPlayHistoryService {

    @Resource
    private VideoPlayHistoryMapper videoPlayHistoryMapper;

    @Override
    public void savePlayHistory(String videoId, String userId, Integer fileIndex) {
        LambdaQueryWrapper<VideoPlayHistory> queryWrapper = Wrappers.lambdaQuery(VideoPlayHistory.class)
                .eq(VideoPlayHistory::getVideoId, videoId)
                .eq(VideoPlayHistory::getUserId, userId);
        VideoPlayHistory existingHistory = this.getOne(queryWrapper);
        if (existingHistory != null) {
            // 使用update()方法配合条件更新
            VideoPlayHistory updateHistory = new VideoPlayHistory();
            updateHistory.setFileIndex(fileIndex);
            updateHistory.setLastUpdateTime(new Date());
            this.update(updateHistory, queryWrapper);
        } else {
            VideoPlayHistory videoPlayHistory = new VideoPlayHistory();
            videoPlayHistory.setVideoId(videoId);
            videoPlayHistory.setUserId(userId);
            videoPlayHistory.setFileIndex(fileIndex);
            this.save(videoPlayHistory);
        }
    }

    @Override
    public Page<VideoPlayHistory> getVideoPlayHistoryList(String userId, PageRequest pageRequest) {
        Page<VideoPlayHistory> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        return videoPlayHistoryMapper.getVideoPlayHistoryList(page, userId);
    }

    @Override
    public void deleteVideoPlayHistory(String userId, String videoId) {
        LambdaQueryWrapper<VideoPlayHistory> lambdaQueryWrapper = Wrappers.lambdaQuery(VideoPlayHistory.class)
                .eq(VideoPlayHistory::getVideoId, videoId)
                .eq(VideoPlayHistory::getUserId, userId);
        VideoPlayHistory videoPlayHistory = this.getOne(lambdaQueryWrapper);
        if (videoPlayHistory == null){
           throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败");
        }
        this.remove(lambdaQueryWrapper);
    }
}




