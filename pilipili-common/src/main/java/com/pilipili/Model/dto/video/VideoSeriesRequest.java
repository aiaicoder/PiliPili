package com.pilipili.Model.dto.video;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/3 15:32
 */
@Data
public class VideoSeriesRequest implements Serializable {
    private Integer seriesId;
    private String seriesName;
    private String seriesDescription;
    private String videoIds;
}
