package com.pilipili.admin.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Model.enums.DateTimePatternEnum;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.config.AppConfig;
import com.pilipili.exception.BusinessException;
import com.pilipili.utils.DateUtils;
import com.pilipili.utils.FFmpegUtils;
import com.pilipili.utils.StringUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    @PostMapping("/uploadImage")
    @ApiOperation("上传封面图")
    public BaseResponse<String> uploadImage(@NotNull MultipartFile file, Boolean createThumbnail) {
        String month = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String folder = appConfig.getFolder() + CommonConstant.FILE_FOLDER + CommonConstant.FILE_COVER + month;
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
        return ResultUtils.success(CommonConstant.FILE_COVER + month + "/" + newFileName);
    }


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

}
