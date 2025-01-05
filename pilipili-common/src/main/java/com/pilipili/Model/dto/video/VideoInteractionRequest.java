package com.pilipili.Model.dto.video;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/5 12:52
 */
@Data
public class VideoInteractionRequest {
    private String videoId;
    private String interaction;
}
