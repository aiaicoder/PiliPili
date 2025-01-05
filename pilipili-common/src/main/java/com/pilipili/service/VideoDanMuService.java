package com.pilipili.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoDanMu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 15712
* @description 针对表【VideoDanMu(视频弹幕)】的数据库操作Service
* @createDate 2024-12-28 12:46:30
*/
public interface VideoDanMuService extends IService<VideoDanMu> {

    void saveVideoDanMu(VideoDanMu videoDanMu);

    Page<VideoDanMu> getDanMuList(Page<VideoDanMu> objectPage, String videoId, String userId);

    void deleteDanMu(Integer danMuId, UserInfo loginUser);
}
