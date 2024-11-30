package com.pilipili.web.controller;


import com.pilipili.Model.dto.Category.CategoryQueryRequest;
import com.pilipili.Model.entity.CategoryInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ResultUtils;
import com.pilipili.service.CategoryInfoService;
import com.pilipili.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/Category")
@Slf4j
public class CategoryController {


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private CategoryInfoService categoryInfoService;


    @GetMapping("/LoadCategory")
    @ApiOperation("加载所有分类")
    public BaseResponse<List<CategoryInfo>> loadAllCategory() {
        return ResultUtils.success(categoryInfoService.getCategoryListWeb());
    }


}
