package com.pilipili.Model.dto.video;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/28 13:17
 */
@Data
public class VideoDanMuPostRequest implements Serializable {
    /**
     * 视频id
     */
    private String videoId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 弹幕内容
     */
    private String text;

    /**
     * 弹幕模式
     */
    private Integer mode;

    /**
     * 弹幕颜色
     */
    private String color;

    /**
     * 弹幕时间
     */
    private Integer time;
}
