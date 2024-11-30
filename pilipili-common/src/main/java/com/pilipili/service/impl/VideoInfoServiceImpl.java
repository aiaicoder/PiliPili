package com.pilipili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.VideoInfoService;
import org.springframework.stereotype.Service;

/**
* @author 15712
* @description 针对表【VideoInfo(视频信息)】的数据库操作Service实现
* @createDate 2024-11-30 20:51:27
*/
@Service
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo>
    implements VideoInfoService{

}




