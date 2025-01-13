package com.pilipili.web.Task;

import com.pilipili.service.StatisticsInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/12 10:08
 */
@Component
public class SysTask {

    @Resource
    private StatisticsInfoService statisticsInfoService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void statisticData() {
        statisticsInfoService.statisticData();
    }
}
