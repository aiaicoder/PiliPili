package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.Model.entity.VideoInfoPost;
import com.pilipili.common.ErrorCode;
import com.pilipili.component.EsSearchComponent;
import com.pilipili.config.AppConfig;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.enums.UserRoleEnum;
import com.pilipili.enums.VideoRecommendTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.UserInfoMapper;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.*;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.SysSettingUtil;
import jodd.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private VideoDanMuService videoDanMuService;

    @Resource
    @Lazy
    private VideoCommentService videoCommentService;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Resource
    private AppConfig appConfig;

    @Resource
    private SysSettingUtil sysSettingUtil;

    @Resource
    private EsSearchComponent esSearchComponent;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void deleteVideo(UserInfo loginUser, String videoId) {
        VideoInfoPost dbInfo = videoInfoPostService.getById(videoId);
        if (dbInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue()) || !dbInfo.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        removeById(videoId);
        videoInfoPostService.removeById(videoId);
        SysSettingDTO sysSetting = sysSettingUtil.getSysSetting();
        //删除用户硬币
        userInfoMapper.updateCountInfo(loginUser.getUserId(), -sysSetting.getPostVideoCoinCount());
        esSearchComponent.deleteDoc(videoId);
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

    @Override
    public void addReadCount(String videoId) {
        videoInfoMapper.updateCountInfo(videoId, UserActionTypeEnum.VIDEO_PLAY.getField(), 1);
    }

    @Override
    public void recommendVideo(String videoId) {
        VideoInfo videoInfo = this.getById(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer recommendType = null;
        if (!VideoRecommendTypeEnum.RECOMMEND.getType().equals(videoInfo.getRecommendType())) {
            recommendType = VideoRecommendTypeEnum.RECOMMEND.getType();
        } else {
            recommendType = VideoRecommendTypeEnum.NO_RECOMMEND.getType();
        }
        videoInfo.setRecommendType(recommendType);
        this.updateById(videoInfo);
    }
}




