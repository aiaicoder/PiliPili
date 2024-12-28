package com.pilipili.Model.Vo;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 11:11
 */
@Data
public class VideoStatusCountInfoVO {
    /**
     * 已审核通过
     */
    private Long auditPassCount;

    /**
     * 未审核通过
     */
    private Long auditFailCount;

    /**
     * 待审核
     */
    private Long inProgress;
}
