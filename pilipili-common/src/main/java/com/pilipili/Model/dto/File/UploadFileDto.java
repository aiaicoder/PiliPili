package com.pilipili.Model.dto.File;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/30 21:05
 */
@Data
public class UploadFileDto implements Serializable {
    private static final long serialVersionUID = -7995792558136622504L;
    /**
     * 上传id
     */
    private String uploadId;

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 分片索引
     */
    private Integer chunkIndex;
    /**
     * 分片总数
     */
    private Integer chunks;
    /**
     * 文件大小
     */
    private Long fileSize = 0L;
    /**
     * 文件路径
     */
    private String filePath;
}
