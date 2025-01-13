package com.pilipili.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Model.dto.File.UploadFileDto;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.Model.entity.VideoInfoFile;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.Model.entity.VideoInfoPost;
import com.pilipili.enums.VideoFileTransferResultEnum;
import com.pilipili.enums.VideoFileUpdateTypeEnum;

import com.pilipili.common.ErrorCode;
import com.pilipili.config.AppConfig;
import com.pilipili.enums.VideoStatusEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.*;
import com.pilipili.service.VideoInfoFilePostService;
import com.pilipili.service.VideoInfoFileService;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.FFmpegUtils;
import com.pilipili.utils.RedisUtils;
import com.pilipili.utils.SysSettingUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 15712
 * @description 针对表【VideoInfoPost(视频信息)】的数据库操作Service实现
 * @createDate 2024-11-30 20:56:30
 */
@Service
public class VideoInfoPostServiceImpl extends ServiceImpl<VideoInfoPostMapper, VideoInfoPost>
        implements VideoInfoPostService {


    @Resource
    private SysSettingUtil sysSettingUtil;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;


    @Resource
    private VideoInfoFilePostMapper videoInfoFilePostMapper;

    @Resource
    private VideoDanMuMapper videoDanMuMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private VideoInfoMapper videoInfoMapper;


    @Resource
    private VideoInfoFileService videoInfoFileService;

    @Resource
    @Lazy
    private VideoInfoService videoInfoService;


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private AppConfig appConfig;

    @Resource
    private FFmpegUtils ffmpegUtils;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInfoPost(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> upLoadFileList) {
        SysSettingDTO sysSettingDTO = sysSettingUtil.getSysSetting();
        //先判断视频的p数是否超过限制
        if (upLoadFileList.size() >= sysSettingDTO.getVideoPCount()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频P数超过限制");
        }
        //如果视频Id不为空，那么就算更新操作（修改逻辑）
        if (StringUtils.isNotEmpty(videoInfoPost.getVideoId())) {
            VideoInfoPost videoPostDb = getById(videoInfoPost.getVideoId());
            if (videoPostDb == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频不存在");
            }
            //根据视频审核状态进行判断
            if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()}, videoPostDb.getStatus())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频审核或转码中，不支持修改");
            }
        }
        Date now = new Date();
        String videoId = videoInfoPost.getVideoId();
        String userId = videoInfoPost.getUserId();
        List<VideoInfoFilePost> deleteFileList = new ArrayList<>();
        List<VideoInfoFilePost> addFileList = upLoadFileList;
        //为空直接插入
        if (StringUtils.isEmpty(videoId)) {
            videoId = RandomUtil.randomString(CommonConstant.RANDOM_STRING_LENGTH15);
            VideoInfoPost newVideoInfoPost = new VideoInfoPost();
            newVideoInfoPost.setVideoId(videoId);
            newVideoInfoPost.setCreateTime(now);
            newVideoInfoPost.setLastUpdateTime(now);
            newVideoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            this.save(newVideoInfoPost);
        } else {
            QueryWrapper<VideoInfoFilePost> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("videoId", videoId);
            queryWrapper.eq("userId", userId);
            List<VideoInfoFilePost> dbFileList = videoInfoFilePostService.list(queryWrapper);
            //转成map，变量数据库的内容查看文件是否删除
            Map<String, VideoInfoFilePost> uploadFileMap = upLoadFileList.stream().collect(Collectors.toMap(VideoInfoFilePost::getUploadId,
                    Function.identity(), (oldValue, newValue) -> newValue));
            boolean changeName = false;
            for (VideoInfoFilePost videoInfoFilePost : dbFileList) {
                //如果数据库中的文件id在传过来的上传文件列表中没有找到表示已经删除，添加到删除列表
                VideoInfoFilePost dbFile = uploadFileMap.get(videoInfoFilePost.getUploadId());
                if (dbFile == null) {
                    deleteFileList.add(videoInfoFilePost);
                } else {
                    //如果数据库中的文件id在传过来的上传文件列表中找到，但是文件名不同，则修改文件名
                    if (!dbFile.getFileName().equals(videoInfoFilePost.getFileName())) {
                        changeName = true;
                    }
                }
            }
            videoInfoPost.setLastUpdateTime(now);
            //剩下的就是要添加内容
            addFileList = upLoadFileList.stream().filter(videoInfoFilePost -> videoInfoFilePost.getFileId() == null).collect(Collectors.toList());
            Boolean changeVideoInfo = changeVideoInfo(videoInfoPost);
            //如果不为空表明是新增从新设置状态为转码
            if (!addFileList.isEmpty()) {
                videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            } else if (changeName || changeVideoInfo) {
                //如果只是改动那么就要重新审核
                videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
            }
            this.updateById(videoInfoPost);
        }

        if (!deleteFileList.isEmpty()) {
            List<String> fileIds = deleteFileList.stream().map(VideoInfoFilePost::getFileId).collect(Collectors.toList());
            videoInfoFilePostMapper.deleteBathByFileId(fileIds, userId);
            //获取文件路径通过消息队列进行处理
            List<String> filePath = deleteFileList.stream().map(VideoInfoFilePost::getFilePath).collect(Collectors.toList());
            redisUtils.addFileToDeleteQueue(videoId, filePath);
        }
        //更新视频文件信息
        int index = 1;
        for (VideoInfoFilePost videoInfoFilePost : upLoadFileList) {
            videoInfoFilePost.setFileIndex(index++);
            videoInfoFilePost.setVideoId(videoId);
            videoInfoFilePost.setUserId(userId);
            if (videoInfoFilePost.getFileId() == null) {
                videoInfoFilePost.setFileId(RandomUtil.randomString(CommonConstant.RANDOM_STRING_LENGTH20));
                videoInfoFilePost.setUpdateType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
                videoInfoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            }
        }
        //批量更新
        videoInfoFilePostMapper.insertOrUpdateBatch(upLoadFileList);
        //添加到转码队列
        if (!addFileList.isEmpty()) {
            for (VideoInfoFilePost file : addFileList) {
                file.setUserId(userId);
                file.setVideoId(videoId);
            }
            redisUtils.addFileToTransferQueue(addFileList);
        }
    }

    /**
     * 转码流程
     * @param videoInfoFilePost
     */
    @Override
    public void transferVideoFile(VideoInfoFilePost videoInfoFilePost) {
        VideoInfoFilePost uploadFile = new VideoInfoFilePost();
        try {
            UploadFileDto uploadFileDto = redisUtils.getPreUploadVideoFile(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());
            String tempFilePath = appConfig.folder + CommonConstant.FILE_FOLDER + CommonConstant.FILE_FOLDER_TEMP + uploadFileDto.getFilePath();
            File tempFile = new File(tempFilePath);
            String targetFilePath = appConfig.folder + CommonConstant.FILE_FOLDER + CommonConstant.FILE_VIDEO + uploadFileDto.getFilePath();
            File targetFile = new File(targetFilePath);
            //临时目录复制到目标目录
            FileUtil.copy(tempFile, targetFile, true);
            //删除临时目录
            FileUtil.del(tempFile);
            redisUtils.delPreUploadVideoFile(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());
            //合并文件
            String completeVideo = targetFilePath + CommonConstant.TEMP_VIDEO_NAME;
            union(targetFilePath, completeVideo, true);
            //获取播放时长
            Integer videoDuration = ffmpegUtils.getVideoDuration(completeVideo);
            uploadFile.setDuration(videoDuration);
            uploadFile.setFilePath(completeVideo);
            uploadFile.setFileSize(new File(completeVideo).length());
            uploadFile.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());
            convertVideoToTs(completeVideo);
        } catch (Exception e) {
            uploadFile.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
            log.error("转码文件失败", e);
        } finally {
            UpdateWrapper<VideoInfoFilePost> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("uploadId", videoInfoFilePost.getUploadId());
            updateWrapper.eq("userId", videoInfoFilePost.getUserId());
            videoInfoFilePostMapper.update(uploadFile, updateWrapper);
            //查询是否有转码失败的
            LambdaQueryWrapper<VideoInfoFilePost> lambdaQueryWrapper = Wrappers.lambdaQuery(VideoInfoFilePost.class).
                    eq(VideoInfoFilePost::getVideoId, videoInfoFilePost.getVideoId())
                    .eq(VideoInfoFilePost::getTransferResult, VideoFileTransferResultEnum.FAIL.getStatus());
            if (videoInfoFilePostMapper.selectCount(lambdaQueryWrapper) > 0) {
                VideoInfoPost videoInfoPost = new VideoInfoPost();
                videoInfoPost.setStatus(VideoStatusEnum.STATUS1.getStatus());
                videoInfoPost.setVideoId(videoInfoFilePost.getVideoId());
                this.updateById(videoInfoPost);
                return;
            }
            lambdaQueryWrapper = Wrappers.lambdaQuery(VideoInfoFilePost.class).
                    eq(VideoInfoFilePost::getVideoId, videoInfoFilePost.getVideoId())
                    .eq(VideoInfoFilePost::getTransferResult, VideoFileTransferResultEnum.TRANSFER.getStatus());
            Long transferCount = videoInfoFilePostMapper.selectCount(lambdaQueryWrapper);
            if (transferCount == 0) {
                Integer duration = videoInfoFilePostMapper.getDuration(videoInfoFilePost.getVideoId());
                VideoInfoPost videoUpdate = new VideoInfoPost();
                videoUpdate.setStatus(VideoStatusEnum.STATUS2.getStatus());
                videoUpdate.setVideoId(videoInfoFilePost.getVideoId());
                videoUpdate.setDuration(duration);
                this.updateById(videoUpdate);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditVideo(String videoId, Integer status, String reason) {
        VideoStatusEnum statusEnum = VideoStatusEnum.getByStatus(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<VideoInfoPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("videoId", videoId)
        .eq("status", VideoStatusEnum.STATUS2.getStatus())  // 乐观锁条件：当前状态必须匹配
        .set("status", status);
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "视频审核失败");
        }
        //已经审核通过视频没有进行变动
        UpdateWrapper<VideoInfoFilePost> updateWrapperVideoFile = new UpdateWrapper<>();
        updateWrapper.eq("videoId", videoId)
                .set("updateType", VideoFileUpdateTypeEnum.NO_UPDATE.getStatus());  // 乐观锁条件：当前状态必须匹配
        videoInfoFilePostService.update(updateWrapperVideoFile);
        //如果审核不通过那么就直接进行返回
        if (VideoStatusEnum.STATUS4 == statusEnum){
            return;
        }

        VideoInfoPost infoPost = this.getById(videoId);
        VideoInfo dbInfo = videoInfoService.getById(videoId);
        if (dbInfo == null){
            SysSettingDTO sysSettingDTO = sysSettingUtil.getSysSetting();
            //如果是新视频就加硬币
            userInfoMapper.updateCountInfo(infoPost.getUserId(), sysSettingDTO.getPostVideoCoinCount());
        }
        VideoInfo  videoInfo = BeanUtil.copyProperties(infoPost, VideoInfo.class);
        //更新发布信息到正式表
        videoInfoMapper.insertOrUpdate(videoInfo);
        //更新视频信息到正式表，先删除后添加
        videoInfoFileService.removeById(videoId);
        QueryWrapper<VideoInfoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        List<VideoInfoFilePost> videoInfoFilePosts = videoInfoFilePostMapper.selectList(queryWrapper);
        List<VideoInfoFile> videoInfoFiles = BeanUtil.copyToList(videoInfoFilePosts, VideoInfoFile.class);
        videoInfoFileService.saveBatch(videoInfoFiles);
        /**
         * 删除文件
         */
        List<String> deleteFileList = redisUtils.getDelFileList(videoId);
        if (deleteFileList != null && deleteFileList.size() > 0){
            for (String path : deleteFileList) {
                File file = new File(appConfig.getFolder() + CommonConstant.FILE_FOLDER + path);
                if (file.exists()) {
                    try {
                        FileUtil.del(file);
                    } catch (IORuntimeException e) {
                        log.error("文件删除失败", e);
                    }
                }
            }
        }
        redisUtils.clearDelFileList(videoId);
        //保存信息到es中

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInteraction(VideoInfoPost bean) {
        VideoInfoPost dbInfo = getById(bean.getVideoId());
        if (dbInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        update(Wrappers.lambdaUpdate(VideoInfoPost.class)
                .eq(VideoInfoPost::getVideoId, bean.getVideoId())
                .eq(VideoInfoPost::getUserId, bean.getUserId())
                .set(VideoInfoPost::getInteraction, bean.getInteraction()));
        videoInfoService.update(Wrappers.lambdaUpdate(VideoInfo.class)
                .eq(VideoInfo::getVideoId, bean.getVideoId())
                .set(VideoInfo::getInteraction, bean.getInteraction()));
    }




    /**
     * 转码文件为ts文件
     *
     * @param completeVideo
     */
    private void convertVideoToTs(String completeVideo) {
        File completeVideoFile = new File(completeVideo);
        File tsFolder = completeVideoFile.getParentFile();
        String codec = ffmpegUtils.getVideoCodec(completeVideo);
        if (CommonConstant.VIDEO_CODE_HEVC.equals(codec)) {
            //创建临时的新文件，因为ffmpeg不支持在原有的文件上转码
            String tempFile = completeVideo + CommonConstant.VIDEO_CODE_TEMP_FILE_SUFFIX;
            new File(completeVideo).renameTo(new File(tempFile));
            ffmpegUtils.convertHevcToMp4(tempFile, completeVideo);
            FileUtil.del(tempFile);
        }
        ffmpegUtils.convertVideo2Ts(tsFolder, completeVideo);
        FileUtil.del(completeVideoFile);
    }


    /**
     * 合并文件
     *
     * @param dirPath
     * @param toFilePath
     * @param delSource
     */
    private void union(String dirPath, String toFilePath, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                File chunkFile = new File(dirPath + File.separator + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    int len;
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并文件失败");
                } finally {
                    if (readFile != null) {
                        readFile.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并文件" + dirPath + "出错了");
        } finally {
            if (delSource) {
                for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                    fileList[i].delete();
                }
            }
        }
    }

    private Boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
        VideoInfoPost dbInfo = this.getById(videoInfoPost.getVideoId());
        // 标题，封面，标签，简介
        return !videoInfoPost.getVideoName().equals(dbInfo.getVideoName()) ||
                !videoInfoPost.getVideoCover().equals(dbInfo.getVideoCover()) ||
                !videoInfoPost.getTags().equals(dbInfo.getTags()) ||
                !videoInfoPost.getIntroduction().equals(dbInfo.getIntroduction() == null ? "": dbInfo.getIntroduction());
    }
}




