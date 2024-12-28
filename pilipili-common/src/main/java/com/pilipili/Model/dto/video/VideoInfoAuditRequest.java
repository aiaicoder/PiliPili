package com.pilipili.Model.dto.video;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 13:02
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 审核对象
 * @author 15712
 */
@Data
public class VideoInfoAuditRequest implements Serializable {

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 原因
     */
    private String reason;
}
