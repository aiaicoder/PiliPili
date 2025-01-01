package com.pilipili.Model.dto.Comment;

import com.pilipili.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/30 13:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQuery extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String videoId;
    private Boolean loadChildren;
    private Integer pCommentId;
    private String orderBy;
    private Integer topType;
}
