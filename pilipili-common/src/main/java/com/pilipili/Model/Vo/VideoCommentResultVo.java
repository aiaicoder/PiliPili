package com.pilipili.Model.Vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.entity.UserAction;
import com.pilipili.Model.entity.VideoComment;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/29 17:10
 */
@Data
public class VideoCommentResultVo {

    private Page<VideoComment> videoComment;

    private List<UserAction> userActionList;
}
