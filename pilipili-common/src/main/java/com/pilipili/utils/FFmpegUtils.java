package com.pilipili.utils;

import cn.hutool.core.io.FileUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.config.AppConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

}
