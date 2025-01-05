package com.pilipili.Model.Vo;

import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.Model.entity.VideoInfoPost;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/5 12:37
 */
@Data
public class VideoInfoPostDetailVo {
    private VideoInfoPost videoInfoPost;
    private List<VideoInfoFilePost> videoInfoFilePosts;
}
