package com.pilipili.Model.Vo;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/28 20:04
 */

import com.pilipili.Model.entity.UserAction;
import lombok.Data;

import java.util.List;

/**
 * 返回视频信息和用户行为
 * @author 15712
 */
@Data
public class VideoInfoAndUserActionVo {

    private VideoInfoVo videoInfoVo;

    private List<UserAction> userAction;
}
