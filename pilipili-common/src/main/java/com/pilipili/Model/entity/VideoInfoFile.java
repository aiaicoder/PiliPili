package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 视频文件信息
 * @author 15712
 * @TableName VideoInfoFile
 */
@TableName(value ="VideoInfoFile")
@Data
public class VideoInfoFile implements Serializable {
    /**
     * 唯一ID
     */
    @TableId
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件索引
     */
    private Integer fileIndex;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 持续时间（秒）
     */
    private Integer duration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}