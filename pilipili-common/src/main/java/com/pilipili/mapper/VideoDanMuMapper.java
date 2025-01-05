package com.pilipili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.VideoDanMu;
import org.apache.ibatis.annotations.Param;

/**
* @author 15712
* @description 针对表【VideoDanMu(视频弹幕)】的数据库操作Mapper
* @createDate 2024-12-28 12:46:30
* @Entity com.pilipili.Model.entity.VideoDanMu
*/
public interface VideoDanMuMapper extends BaseMapper<VideoDanMu> {

    Page<VideoDanMu> getDanMuList(Page<VideoDanMu> danMuPage, @Param("videoId") String videoId, @Param("userId") String userId);
}




