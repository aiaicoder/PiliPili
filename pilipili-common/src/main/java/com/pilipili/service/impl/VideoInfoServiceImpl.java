package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.common.ErrorCode;
import com.pilipili.config.AppConfig;
import com.pilipili.enums.UserRoleEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.*;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.SysSettingUtil;
import jodd.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 15712
 * @description 针对表【VideoInfo(视频信息)】的数据库操作Service实现
 * @createDate 2024-11-30 20:51:27
 */
@Service
@Slf4j
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo>
        implements VideoInfoService {


    @Resource
    private VideoInfoPostService videoInfoPostService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoDanMuService videoDanMuService;

    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @Resource
    private AppConfig appConfig;

    @Resource
    private SysSettingUtil sysSettingUtil;

    @Override
    public void deleteVideo(UserInfo loginUser, String videoId) {
        VideoInfo dbInfo = getById(videoId);
        if (dbInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue()) || !dbInfo.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        removeById(videoId);
        videoInfoPostService.removeById(videoId);
        SysSettingDTO sysSetting = sysSettingUtil.getSysSetting();
        //todo 删除用户硬币
        //todo 删除es信息
        executorService.execute(() -> {
            //删除视频分p文件
            videoInfoFileService.removeById(videoId);

            //删除视频分片上传信息
            List<VideoInfoFilePost> videoInfoFilePosts = videoInfoFilePostService.list(Wrappers.lambdaQuery(VideoInfoFilePost.class).eq(VideoInfoFilePost::getVideoId, videoId));
            videoInfoFilePostService.removeById(videoId);

            //删除弹幕信息
            videoDanMuService.removeById(videoId);
            //删除评论信息
            videoCommentService.removeById(videoId);

            for (VideoInfoFilePost videoInfoFilePost : videoInfoFilePosts) {
                String path = videoInfoFilePost.getFilePath();
                try {
                    FileUtil.deleteFile(appConfig.folder + path);
                } catch (IOException e) {
                    log.error("文件删除失败{}", appConfig.folder + path);
                }
            }
        });
    }
}




