package com.pilipili.Model.dto.Comment;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/10 13:33
 */
@Data
public class UserMessageExtendDto {

    private String messageContent;

    private String messageContentReplay;

    private Integer auditStatus;
}
