package com.pilipili.Model.Vo;

import com.pilipili.Model.entity.UserVideoSeries;
import com.pilipili.Model.entity.UserVideoSeriesVideo;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/4 16:22
 */
@Data
public class UserVideoSeriesDetailVo {
    private UserVideoSeries userVideoSeries;

    private List<UserVideoSeriesVideo> userVideoSeriesVideos;
}
