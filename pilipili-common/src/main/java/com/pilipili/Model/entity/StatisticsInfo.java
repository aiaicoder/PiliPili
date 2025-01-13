package com.pilipili.Model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据统计
 * @author 15712
 * @TableName StatisticsInfo
 */
@TableName(value ="StatisticsInfo")
@Data
public class StatisticsInfo implements Serializable {
    /**
     * 统计日期
     */
    @TableId
    private String statisticsDate;

    /**
     * 用户ID
     */
    @TableId
    private String userId;

    /**
     * 数据统计类型
     */
    @TableId
    private Integer dataType;

    /**
     * 统计数量
     */
    private Integer statisticsCount;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}