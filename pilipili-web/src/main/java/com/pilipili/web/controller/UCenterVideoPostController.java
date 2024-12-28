package com.pilipili.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Constant.CommonConstant;
import com.pilipili.Model.Vo.VideoInfoPostVo;
import com.pilipili.Model.Vo.VideoStatusCountInfoVO;
import com.pilipili.Model.dto.video.VideoInfoPostListRequest;
import com.pilipili.Model.dto.video.VideoPostRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.Model.entity.VideoInfoPost;
import com.pilipili.Model.enums.VideoStatusEnum;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoInfoPostMapper;
import com.pilipili.service.UserInfoService;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.service.VideoInfoService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/3 22:37
 */
@RequestMapping("/uCenter")
@RestController
public class UCenterVideoPostController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private UserInfoService userInfoService;


    @Resource
    private VideoInfoPostMapper videoInfoPostMapper;


    @PostMapping("/postVideo")
    @ApiOperation("上传视频")
    @SaCheckLogin
    public void postVideo(@RequestBody VideoPostRequest videoPostRequest) {
        if (videoPostRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String videoId = videoPostRequest.getVideoId();
        String videoName = videoPostRequest.getVideoName();
        String pCategoryId = videoPostRequest.getPCategoryId();
        String categoryId = videoPostRequest.getCategoryId();
        String postType = videoPostRequest.getPostType();
        String tags = videoPostRequest.getTags();
        String introduction = videoPostRequest.getIntroduction();
        String interaction = videoPostRequest.getInteraction();
        String uploadFileList = videoPostRequest.getUploadFileList();
        if (introduction.length() > 2000 || interaction.length() > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(videoId) || (StringUtils.isBlank(videoName) && videoName.length() > 100)
                || StringUtils.isBlank(pCategoryId) ||
                StringUtils.isBlank(categoryId) || StringUtils.isBlank(postType) ||
                (StringUtils.isBlank(tags) && tags.length() > 300) || StringUtils.isBlank(uploadFileList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<VideoInfoFilePost> videoInfoFilePosts = JSONUtil.toList(uploadFileList, VideoInfoFilePost.class);
        VideoInfoPost videoInfoPost = new VideoInfoPost();
        BeanUtil.copyProperties(videoPostRequest, videoInfoPost, true);
        videoInfoPost.setUserId(loginUser.getUserId());
        videoInfoPostService.saveVideoInfoPost(videoInfoPost, videoInfoFilePosts);
    }

    @GetMapping("/getVideoInfoPostList")
    @ApiOperation("获取视频列表")
    @SaCheckLogin
    public BaseResponse<Page<VideoInfoPostVo>> getVideoInfoPostList(VideoInfoPostListRequest videoInfoPostListRequest) {
        if (videoInfoPostListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        Integer status = videoInfoPostListRequest.getStatus();
        Integer[] excludeStatus = null;
        if (status == -1) {
            excludeStatus = new Integer[]{VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus()};
        }
        videoInfoPostListRequest.setSortField("creatTime");
        videoInfoPostListRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        Page<VideoInfoPostVo> page = new Page<>(videoInfoPostListRequest.getCurrent(), videoInfoPostListRequest.getPageSize());
        Page<VideoInfoPostVo> result = videoInfoPostMapper.getVideoInfoPostVoList(page, userId, excludeStatus, videoInfoPostListRequest);
        return ResultUtils.success(result);
    }


    @GetMapping("/getVideoCountInfo")
    @ApiOperation("获取各状态下的视频数量")
    @SaCheckLogin
    public BaseResponse<VideoStatusCountInfoVO> getVideoCountInfo() {
        UserInfo loginUser = userInfoService.getLoginUser();
        String userId = loginUser.getUserId();
        QueryWrapper<VideoInfoPost> queryWrapper = new QueryWrapper<>();

        // 审核通过数量
        queryWrapper
                .eq("userId", userId)
                .eq("status", VideoStatusEnum.STATUS3.getStatus());
        Long auditPassCount = videoInfoPostService.count(queryWrapper);

        // 审核失败数量
        // 清除之前的条件
        queryWrapper.clear();
        queryWrapper.eq("userId", userId)
                .eq("status", VideoStatusEnum.STATUS4.getStatus());
        Long auditFailCount = videoInfoPostService.count(queryWrapper);
        // 进行中数量
        queryWrapper.clear();
        queryWrapper.eq("userId", userId)
                .notIn("status", VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus());
        Long inProgress = videoInfoPostService.count(queryWrapper);
        VideoStatusCountInfoVO videoStatusCountInfoVO = new VideoStatusCountInfoVO();
        videoStatusCountInfoVO.setAuditPassCount(auditPassCount);
        videoStatusCountInfoVO.setAuditFailCount(auditFailCount);
        videoStatusCountInfoVO.setInProgress(inProgress);
        return ResultUtils.success(videoStatusCountInfoVO);
    }


}
