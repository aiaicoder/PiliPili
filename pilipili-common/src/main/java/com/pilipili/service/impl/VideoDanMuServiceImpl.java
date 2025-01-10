package com.pilipili.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoDanMu;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.component.EsSearchComponent;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.enums.UserRoleEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.VideoDanMuMapper;
import com.pilipili.mapper.VideoInfoMapper;
import com.pilipili.service.VideoDanMuService;
import com.pilipili.service.VideoInfoService;
import org.elasticsearch.action.get.GetRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 15712
 * @description 针对表【VideoDanMu(视频弹幕)】的数据库操作Service实现
 * @createDate 2024-12-28 12:46:30
 */
@Service
public class VideoDanMuServiceImpl extends ServiceImpl<VideoDanMuMapper, VideoDanMu>
        implements VideoDanMuService {


    @Resource
    private VideoDanMuMapper videoDanMuMapper;

    @Resource
    private VideoInfoService videoInfoService;


    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Resource
    private EsSearchComponent esSearchComponent;



    @Override
    public void saveVideoDanMu(VideoDanMu videoDanMu) {
        //先查询视频是否存在
        VideoInfo videoInfo = videoInfoService.getById(videoDanMu.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains("1")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "up主没有开启弹幕");
        }
        this.save(videoDanMu);
        videoInfoMapper.updateCountInfo(videoDanMu.getVideoId(), UserActionTypeEnum.VIDEO_DANMU.getField(), 1);
        esSearchComponent.saveDoc(videoInfo);
    }


    @Override
    public Page<VideoDanMu> getDanMuList(Page<VideoDanMu> danMuPage, String videoId, String userId) {
        return videoDanMuMapper.getDanMuList(danMuPage, videoId, userId);
    }

    @Override
    public void deleteDanMu(Integer danMuId, UserInfo loginUser) {
        VideoDanMu dbInfo = getById(danMuId);
        if (dbInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        VideoInfo videoInfo = videoInfoService.getById(dbInfo.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!videoInfo.getUserId().equals(loginUser.getUserId()) || !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        removeById(danMuId);
    }

}




