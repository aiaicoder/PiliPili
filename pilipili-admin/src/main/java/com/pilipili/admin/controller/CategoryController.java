package com.pilipili.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.pilipili.Model.dto.Category.CategoryDeleteRequest;
import com.pilipili.Model.dto.Category.CategoryQueryRequest;
import com.pilipili.Model.dto.Category.CategorySaveRequest;
import com.pilipili.Model.entity.CategoryInfo;
import com.pilipili.service.CategoryInfoService;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@RestController
@RequestMapping("/Category")
@Slf4j
public class CategoryController {


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private CategoryInfoService categoryInfoService;


    @PostMapping("/LoadCategory")
    @ApiOperation(value = "分类管理")
    public BaseResponse<List<CategoryInfo>> loadCategory(@RequestBody CategoryQueryRequest categoryQueryRequest, HttpServletRequest request) {
        List<CategoryInfo> categoryLists = categoryInfoService.getCategoryList(categoryQueryRequest);
        return ResultUtils.success(categoryLists);
    }


    @PostMapping("/addCategory")
    @ApiOperation(value = "添加分类")
    public BaseResponse<Boolean> addOrUpdateCategory(@RequestBody CategorySaveRequest categorySaveRequest, HttpServletRequest request) {
        if (categorySaveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String categoryName = categorySaveRequest.getCategoryName();
        String categoryCode = categorySaveRequest.getCategoryCode();
        if (StringUtils.isAnyBlank(categoryName, categoryCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CategoryInfo categoryInfo = BeanUtil.copyProperties(categorySaveRequest, CategoryInfo.class);
        Boolean result = categoryInfoService.saveOrUpdateCategory(categoryInfo);
        return ResultUtils.success(result);
    }

    /**
     * 传入的分类id或父级id
     *
     * @param categoryDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/deleteCategory")
    @ApiOperation(value = "删除分类")
    public BaseResponse<Boolean> deleteCategory(@RequestBody CategoryDeleteRequest categoryDeleteRequest, HttpServletRequest request) {
        if (categoryDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer categoryIdOrPid = categoryDeleteRequest.getCategoryIdOrPid();
        if (categoryIdOrPid == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = categoryInfoService.deleteCategory(categoryIdOrPid);
        return ResultUtils.success(result);
    }


    @GetMapping
    @ApiOperation(value = "重新排序")
    public BaseResponse<Boolean> reorderCategory(Integer pCategoryId,String categoryIds) {
        Boolean result = categoryInfoService.reorderCategory(pCategoryId,categoryIds);
        return ResultUtils.success(result);
    }


}
