package com.pilipili.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;

/**
* @author 15712
* @description 针对表【VideoInfo(视频信息)】的数据库操作Service
* @createDate 2024-11-30 20:51:27
*/
public interface VideoInfoService extends IService<VideoInfo> {


    void deleteVideo(UserInfo loginUser, String videoId);

    void addReadCount(String videoId);
}
