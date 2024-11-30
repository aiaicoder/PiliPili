package com.pilipili.Model.dto.search;

import lombok.Data;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/7/2 20:21
 */
@Data
public class UserSearchRequest{
    //搜索人id
    String userId;
    //搜索的id
    String contactId;
}
