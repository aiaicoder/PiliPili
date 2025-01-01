package com.pilipili.Model.dto.video;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/29 16:19
 */

@Data
public class VideoCommentPostRequest {

    private String videoId;

    private Integer replayCommentId;

    private String content;

    private String imgPath;

}
