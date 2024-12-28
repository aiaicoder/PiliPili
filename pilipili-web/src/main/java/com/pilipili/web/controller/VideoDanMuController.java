package com.pilipili.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pilipili.Model.dto.video.VideoDanMuPostRequest;
import com.pilipili.Model.entity.VideoDanMu;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.VideoDanMuService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/28 13:11
 */
@RestController
@RequestMapping("/videoDanMu")
public class VideoDanMuController {
    @Resource
    private VideoDanMuService videoDanMuService;

    @Resource
    private VideoInfoService videoInfoService;


    @Resource
    private VideoInfoMapper videoInfoMapper;


    @PostMapping("/postDanMu")
    @ApiOperation(value = "发送弹幕")
    public BaseResponse<Boolean> save(@RequestBody VideoDanMuPostRequest videoDanMuPostRequest) {
        if (videoDanMuPostRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (videoDanMuPostRequest.getVideoId() == null ||
                (videoDanMuPostRequest.getText() == null || videoDanMuPostRequest.getText().length() > 200) ||
                videoDanMuPostRequest.getMode() == null ||
                videoDanMuPostRequest.getColor() == null || videoDanMuPostRequest.getTime() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        VideoDanMu videoDanMu = BeanUtil.copyProperties(videoDanMuPostRequest, VideoDanMu.class);
        videoDanMu.setPostTime(new Date());
        videoDanMuService.saveVideoDanMu(videoDanMu);
        return ResultUtils.success(true);
    }


    @GetMapping("/loadDanMu")
    @ApiOperation(value = "加载弹幕")
    public BaseResponse<List<VideoDanMu>> loadDanMu(@NotEmpty String videoId, @NotEmpty String fileId) {

        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains("1")) {
            return ResultUtils.success(new ArrayList<>());
        }
        QueryWrapper<VideoDanMu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fileId", fileId);
        queryWrapper.orderByAsc("DanMuId");
        List<VideoDanMu> videoDanMuList = videoDanMuService.list(queryWrapper);
        return ResultUtils.success(videoDanMuList);
    }
}
