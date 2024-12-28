package com.pilipili.Model.dto.video;

import com.pilipili.common.PageRequest;
import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/27 15:07
 */
@Data
public class VideoInfoQueryRequest extends PageRequest {


    /**
     * 父类目id
     */
    private String pCategoryId;

    /**
     * 类目id
     */
    private String categoryId;


}
