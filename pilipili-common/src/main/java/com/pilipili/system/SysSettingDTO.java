package com.pilipili.system;


import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/15 20:05
 */

@Data
public class SysSettingDTO implements Serializable {

    private static final long serialVersionUID = 421543029156924860L;

    /**
     * 最大视频大小
     */
    private Integer maxVideoSize = 20;

    /**
     * 发布视频所需硬币数量
     */
    private Integer postVideoCoinCount = 5;

    /**
     * 视频评论数量
     */
    private Integer commentCount = 10;

    /**
     * 视频p数
     */
    private Integer videoPCount = 10;

    /**
     * 视频数量
     */
    private Integer videoCount = 10;

    /**
     * 注册硬币的初始数量
     */
    private Integer registerCoinCount = 10;
}
