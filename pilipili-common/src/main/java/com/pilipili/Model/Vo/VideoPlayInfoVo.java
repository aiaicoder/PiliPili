package com.pilipili.Model.Vo;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/9 19:59
 */
@Data
public class VideoPlayInfoVo {
    private String videoId;

    /**
     * 观看视频的用户id
     */
    private String userId;



    private Integer fileIndex;
}
