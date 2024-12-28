package com.pilipili.utils;

import cn.hutool.core.io.FileUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/30 14:52
 */
@Component
public class FFmpegUtils {
    @Resource
    private AppConfig appConfig;

    public void createImageThumbnail(String filePath) {
        String CMD = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        String suffix = "." + FileUtil.getSuffix(filePath);
        CMD = String.format(CMD, filePath, filePath.replace(suffix, CommonConstant.FILE_THUMBNAIL_SUFFIX));
        ProcessUtils.executeCommand(CMD, true);
    }


    public Integer getVideoDuration(String completeVideo) {
        final String CMD_GET_CODE = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"";
        String cmd = String.format(CMD_GET_CODE, completeVideo);
        String result = ProcessUtils.executeCommand(cmd, true);
        if (StringUtils.isBlank(result)) {
            return 0;
        }
        result = result.replace("\n", "");
        return new BigDecimal(result).intValue();
    }

    /**
     * 获取文件编码
     *
     * @param videoFilePath
     * @return
     */
    public String getVideoCodec(String videoFilePath) {
        final String CMD_GET_CODE = "ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE, videoFilePath);
        String result = ProcessUtils.executeCommand(cmd, true);
        result = result.replace("\n", "");
        result = result.substring(result.indexOf("=") + 1);
        result = result.substring(0, result.indexOf("["));
        return result;
    }

    /**
     * 转成mp4格式
     *
     * @param newFileName
     * @param filePath
     */
    public void convertHevcToMp4(String newFileName, String filePath) {
        String CMD = "ffmpeg -i \"%s\" -c:v libx264 -crf 20 \"%s\" -y";
        CMD = String.format(CMD, newFileName, filePath);
        ProcessUtils.executeCommand(CMD, true);
    }

    /**
     * 转成ts
     */
    public void convertVideo2Ts(File tsFolder, String videoFilePath) {
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i \"%s\" -vcodec copy -acodec copy -bsf:v h264_mp4toannexb \"%s\"";
        final String CMD_CUT_TS = "ffmpeg -i \"%s\" -c copy -map 0 -f segment -segment_list \"%s\" -segment_time 10 %s/%%4d.ts";
        String tsPath = tsFolder + "/" + CommonConstant.TS_NAME;
        // 生成.ts
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, true);
        // 生成索引文件.m3u8 和切片.ts
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + CommonConstant.M3U8_NAME, tsFolder.getPath());
        ProcessUtils.executeCommand(cmd, true);
        // 删除index.ts
        FileUtil.del(tsPath);
    }
}

