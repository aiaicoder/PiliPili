package com.pilipili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilipili.Model.dto.Category.CategoryQueryRequest;
import com.pilipili.Model.entity.CategoryInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.exception.BusinessException;
import com.pilipili.mapper.CategoryInfoMapper;
import com.pilipili.service.CategoryInfoService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 15712
 * @description 针对表【CategoryInfo(分类信息)】的数据库操作Service实现
 * @createDate 2024-11-25 22:31:13
 */
@Service
public class CategoryInfoServiceImpl extends ServiceImpl<CategoryInfoMapper, CategoryInfo> implements CategoryInfoService {
    @Resource
    private CategoryInfoMapper categoryInfoMapper;

    @Resource
    private VideoInfoService videoInfoService;


    @Resource
    private RedisUtils redisUtils;

    @Override
    public Boolean saveOrUpdateCategory(CategoryInfo categoryInfo) {
        String categoryCode = categoryInfo.getCategoryCode();
        LambdaQueryWrapper<CategoryInfo> queryWrapper = Wrappers.lambdaQuery(CategoryInfo.class).eq(CategoryInfo::getCategoryCode, categoryCode);
        CategoryInfo dbCategoryInfo = this.getOne(queryWrapper);
        boolean isCategoryIdNull = categoryInfo.getCategoryId() == null;
        boolean isDbCategoryInfoNotNull = dbCategoryInfo != null;
        if (isCategoryIdNull && isDbCategoryInfoNotNull) {
            // 如果 categoryInfo 没有 categoryId 但数据库中已经存在一个分类
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类已存在");
        }
        if (!isCategoryIdNull && isDbCategoryInfoNotNull &&
                !dbCategoryInfo.getCategoryId().equals(categoryInfo.getCategoryId())) {
            // 如果 categoryInfo 有 categoryId 且数据库中存在一个分类，但分类 ID 不一致
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类已存在");
        }
        if (categoryInfo.getCategoryId() == null) {
            Integer maxSortId = categoryInfoMapper.getMaxSortId(categoryInfo.getPCategoryId());
            categoryInfo.setSort(maxSortId + 1);
            this.save(categoryInfo);
        } else {
            this.updateById(categoryInfo);
        }
        save2Redis();
        return true;
    }

    @Override
    public Boolean deleteCategory(Integer categoryIdOrPid) {
        //检查该分类是否够视频，有视频不能删除
        LambdaQueryWrapper<VideoInfo> lambdaQueryWrapper = Wrappers.lambdaQuery(VideoInfo.class).eq(VideoInfo::getCategoryId, categoryIdOrPid).
                or().eq(VideoInfo::getPCategoryId, categoryIdOrPid);
        if (videoInfoService.count(lambdaQueryWrapper) > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该分类下有视频，不能删除");
        }
        LambdaQueryWrapper<CategoryInfo> queryWrapper = Wrappers.lambdaQuery(CategoryInfo.class);
        queryWrapper.eq(CategoryInfo::getCategoryId, categoryIdOrPid).or().eq(CategoryInfo::getPCategoryId, categoryIdOrPid);
        save2Redis();
        return this.remove(queryWrapper);
    }

    @Override
    public List<CategoryInfo> getCategoryList(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<CategoryInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<CategoryInfo> categoryInfos = categoryInfoMapper.selectList(queryWrapper);
        if (categoryQueryRequest.getCovertToTree() != null && categoryQueryRequest.getCovertToTree()) {
            return convert2Tree(categoryInfos, 0);//0表示顶级
        }
        return categoryInfos;
    }

    @Override
    public List<CategoryInfo> getCategoryListWeb() {
        List<CategoryInfo> categoryInfo = redisUtils.getCategoryInfo();
        if (categoryInfo != null){
            save2Redis();
        }
        return redisUtils.getCategoryInfo();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reorderCategory(Integer pCategoryId, String categoryIds) {
        String[] categoryIdArray = categoryIds.split(",");
        List<CategoryInfo> categoryInfos = new ArrayList<>();
        int sort = 0;
        for (String categoryId : categoryIdArray) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setCategoryId(Integer.parseInt(categoryId));
            categoryInfo.setSort(++sort);
            categoryInfo.setPCategoryId(pCategoryId);
            categoryInfos.add(categoryInfo);
        }
        try {
            categoryInfoMapper.updateSortBatch(categoryInfos);
            save2Redis();
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }


    private void save2Redis(){
        QueryWrapper<CategoryInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<CategoryInfo> categoryInfos = categoryInfoMapper.selectList(queryWrapper);
        redisUtils.saveCategoryInfo(categoryInfos);
    }

    /**
     * 递归转换，变成树形
     *
     * @param categoryList
     * @param pCategoryId
     * @return
     */
    private List<CategoryInfo> convert2Tree(List<CategoryInfo> categoryList, Integer pCategoryId) {
        //转换
        List<CategoryInfo> tree = new ArrayList<>();
        for (CategoryInfo category : categoryList) {
            if (category.getCategoryId() != null && category.getPCategoryId() != null
                    && category.getPCategoryId().equals(pCategoryId)) {
                category.setChildren(convert2Tree(categoryList, category.getCategoryId()));
                tree.add(category);
            }
        }
        return tree;
    }


}




