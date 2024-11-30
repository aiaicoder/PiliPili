package com.pilipili.Model.dto.File;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/30 21:55
 */
@Data
public class PreUploadFileRequest implements Serializable {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 分片索引
     */
    private Integer chunks;
}
