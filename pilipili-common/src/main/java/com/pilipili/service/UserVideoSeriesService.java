package com.pilipili.service;

import com.pilipili.Model.entity.UserVideoSeries;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 15712
* @description 针对表【UserVideoSeries(用户视频序列归档)】的数据库操作Service
* @createDate 2025-01-01 13:16:26
*/
@Service
public interface UserVideoSeriesService extends IService<UserVideoSeries> {
    void saveUserVideoSeries(UserVideoSeries videoSeries, String videoIds);

    List<UserVideoSeries> getUserVideoSeries(String userId);

    void saveUserVideoSeriesVideo(String userId, Integer seriesId, String videoIds);

    void delSeriesVideo(String userId, Integer seriesId, String videoId);

    void delSeries(String userId, Integer seriesId);

    void changeVideoSeriesSort(String userId, String seriesId);

    List<UserVideoSeries> selectSeriesWithVideo(String userId);
}
