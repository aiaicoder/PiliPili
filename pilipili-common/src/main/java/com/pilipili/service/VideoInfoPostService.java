package com.pilipili.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.VideoDanMu;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.Model.entity.VideoInfoPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 15712
* @description 针对表【VideoInfoPost(视频信息)】的数据库操作Service
* @createDate 2024-11-30 20:56:30
*/
public interface VideoInfoPostService extends IService<VideoInfoPost> {

    void saveVideoInfoPost(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> videoInfoFilePosts);
    void transferVideoFile(VideoInfoFilePost videoInfoFilePost);

    void auditVideo(String videoId, Integer status, String reason);

    void saveVideoInteraction(VideoInfoPost bean);


}
