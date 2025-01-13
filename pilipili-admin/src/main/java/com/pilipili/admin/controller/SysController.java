package com.pilipili.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.pilipili.Constant.UserConstant;
import com.pilipili.common.BaseResponse;
import com.pilipili.common.ResultUtils;
import com.pilipili.system.SysSettingDTO;
import com.pilipili.utils.SysSettingUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/13 16:31
 */
@RestController
@RequestMapping("/sys")
public class SysController {


    @Resource
    private SysSettingUtil sysSettingUtil;

    @GetMapping("/getSysSetting")
    public BaseResponse<SysSettingDTO> getSysSetting() {
        SysSettingDTO sysSettingDTO = sysSettingUtil.getSysSetting();
        return ResultUtils.success(sysSettingDTO);
    }

    @PostMapping("/saveSysSetting")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSysSetting(@RequestBody SysSettingDTO sysSettingDTO) {
        sysSettingUtil.saveSysSetting(sysSettingDTO);
        return ResultUtils.success(true);
    }
}
