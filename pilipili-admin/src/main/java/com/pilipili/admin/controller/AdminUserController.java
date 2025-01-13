package com.pilipili.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.UserInfoVo;
import com.pilipili.Model.dto.user.UserQueryRequest;
import com.pilipili.Model.dto.user.UserUpdateRequest;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import com.pilipili.exception.BusinessException;
import com.pilipili.exception.ThrowUtils;
import com.pilipili.service.UserInfoService;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.SysSettingUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/13 16:13
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private UserInfoService userService;



    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取用户封装列表")
    public BaseResponse<Page<UserInfoVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInfo> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserInfoVo> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserInfoVo> userVo = userService.getUserVoList(userPage.getRecords());
        userVoPage.setRecords(userVo);
        return ResultUtils.success(userVoPage);
    }

    @PostMapping("/changeUserStatus")
    public BaseResponse<Boolean> changeUserStatus(@RequestBody UserUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userId = updateRequest.getUserId();
        Integer status = updateRequest.getStatus();
        userService.changeUserStatus(userId, status);
        return ResultUtils.success(null);
    }




}
