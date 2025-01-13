package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Model.Vo.VideoPlayInfoVo;
import com.pilipili.Model.dto.File.PreUploadFileRequest;
import com.pilipili.Model.dto.File.UploadFileDto;
import com.pilipili.Model.dto.video.VideoUpLoadRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfoFile;
import com.pilipili.enums.DateTimePatternEnum;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.config.AppConfig;
import com.pilipili.exception.BusinessException;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoFileService;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/30 12:03
 */
@RestController
@Slf4j
@RequestMapping("/file")
@Validated
public class FileController {

    @Resource
    private AppConfig appConfig;

    @Resource
    private FFmpegUtils fFmpegUtils;

    @Resource
    private UserInfoService userInfoService;


    @Resource
    private SysSettingUtil sysSettingUtil;


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @GetMapping("/getResource")
    @ApiOperation("获取资源")
    public void getResource(HttpServletResponse response, @NotNull String resourceName) {
        if (!StringUtil.pathIsOk(resourceName)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        String suffix = FileUtil.getSuffix(resourceName);
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2590200");
        readFile(response, resourceName);
    }


    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.folder + CommonConstant.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("文件读取失败", e);
        }
    }

    @PostMapping("/preUploadVideo")
    @ApiOperation("预上传视频")
    @SaCheckLogin
    public BaseResponse<String> preUploadVideo(@RequestBody PreUploadFileRequest preUploadFileRequest) {
        if (preUploadFileRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        String fileName = preUploadFileRequest.getFileName();
        Integer chunks = preUploadFileRequest.getChunks();
        String uploadId = redisUtils.preUploadVideoFile(userId, fileName, chunks);
        return ResultUtils.success(uploadId);
    }

    @PostMapping("/uploadVideo")
    @ApiOperation("上传视频")
    @SaCheckLogin
    public BaseResponse<String> uploadVideo(@RequestPart("file") MultipartFile chunkFile, @RequestBody VideoUpLoadRequest videoUpLoadRequest) throws Exception {
        if (videoUpLoadRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String uploadId = videoUpLoadRequest.getUploadId();
        Integer chunkIndex = videoUpLoadRequest.getChunkIndex();
        String userId = loginUser.getUserId();
        UploadFileDto preUploadVideoFile = redisUtils.getPreUploadVideoFile(userId, uploadId);
        if (preUploadVideoFile == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件不存在请重新上传");
        }
        SysSettingDTO sysSetting = sysSettingUtil.getSysSetting();
        if (preUploadVideoFile.getFileSize() > sysSetting.getMaxVideoSize() * CommonConstant.MB_SIZE) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件大小超过限制");
        }
        //判断分片
        if ((chunkIndex - 1) > preUploadVideoFile.getChunks() || chunkIndex > preUploadVideoFile.getChunks() - 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分片错误");
        }
        //获取文件夹
        String folder = appConfig.folder + CommonConstant.FILE_FOLDER + CommonConstant.FILE_FOLDER_TEMP + preUploadVideoFile.getFilePath();
        //上传一个分片就创建一个文件
        File targetFile = new File(folder + "/" + chunkIndex);
        chunkFile.transferTo(targetFile);
        //记录当前文件的信息
        preUploadVideoFile.setChunkIndex(chunkIndex);
        preUploadVideoFile.setFileSize(preUploadVideoFile.getFileSize() + chunkFile.getSize());
        //更新一下文件上传信息
        redisUtils.updatePreUploadVideoFile(userId, preUploadVideoFile);
        return ResultUtils.success(null);
    }


    @PostMapping("/deleteUploadVideo")
    @ApiOperation("删除临时上传的视频")
    @SaCheckLogin
    public BaseResponse<String> deleteUploadVideo(@RequestBody VideoUpLoadRequest videoUpLoadRequest) throws Exception {
        if (videoUpLoadRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String uploadId = videoUpLoadRequest.getUploadId();
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        UploadFileDto preUploadVideoFile = redisUtils.getPreUploadVideoFile(userId, uploadId);
        if (preUploadVideoFile == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件不存在请重新上传");
        }
        redisUtils.delPreUploadVideoFile(userId, uploadId);
        File file = new File(appConfig.folder + CommonConstant.FILE_FOLDER + CommonConstant.FILE_FOLDER_TEMP + preUploadVideoFile.getFilePath());
        if (file.exists()) {
            FileUtil.del(file);
        }
        return ResultUtils.success(uploadId);
    }


    @PostMapping("/uploadImage")
    @ApiOperation("上传视频封面")
    public BaseResponse<String> uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) {
        String day = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String folder = appConfig.getFolder() + CommonConstant.FILE_FOLDER + CommonConstant.FILE_COVER + day;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String suffix = FileUtil.extName(fileName);
        String newFileName = RandomUtil.randomString(CommonConstant.RANDOM_STRING_LENGTH30) + "." + suffix;
        String filePath = folder + File.separator + newFileName;
        try {
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        if (createThumbnail) {
            //生成缩略图
            fFmpegUtils.createImageThumbnail(filePath);
        }
        return ResultUtils.success(CommonConstant.FILE_COVER + day + "/" + newFileName);
    }


    @GetMapping("/videoResource/{fileId}")
    @ApiOperation("获取视频资源")
    public void videoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getById(fileId);
        if (videoInfoFile == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件不存在");
        }
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + CommonConstant.M3U8_NAME);
        //更新视频的阅读信息
        VideoPlayInfoVo videoPlayInfoVo = new VideoPlayInfoVo();
        videoPlayInfoVo.setVideoId(videoInfoFile.getVideoId());
        videoPlayInfoVo.setFileIndex(videoInfoFile.getFileIndex());
        UserInfo loginUserNoEx = userInfoService.getLoginUserNoEx();
        if (loginUserNoEx != null) {
            videoPlayInfoVo.setUserId(loginUserNoEx.getUserId());
        }
        redisUtils.addVideoPlayInfo(videoPlayInfoVo);
    }


    @GetMapping("/videoResource/{fileId}/{ts}")
    @ApiOperation("获取视频资源")
    public void videoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId,@PathVariable @NotEmpty String ts) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getById(fileId);
        if (videoInfoFile == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件不存在");
        }
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + ts);
    }
}
