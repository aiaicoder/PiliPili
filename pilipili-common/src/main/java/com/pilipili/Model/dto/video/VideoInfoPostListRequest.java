package com.pilipili.Model.dto.video;

import com.pilipili.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/23 9:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoInfoPostListRequest extends PageRequest {
    /**
     * 状态
     */
    Integer status;

    /**
     * 模糊查询
     */
    String videoNameFuzzy;

    /**
     * 是否统计信息
     */
    Boolean countInfo;

    /**
     * 查看作者信息（后台）
     */
    Boolean userInfo;

}
