package com.pilipili.Model.dto.video;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/2 15:08
 */
@Data
public class VideoUpLoadRequest {
    private String uploadId;
    private Integer chunkIndex;
}
