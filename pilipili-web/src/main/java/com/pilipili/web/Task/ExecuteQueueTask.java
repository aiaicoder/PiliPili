package com.pilipili.web.Task;

import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.service.VideoInfoFilePostService;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/7 14:39
 */
@Component
@Slf4j
public class ExecuteQueueTask {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);


    @Resource
    private RedisUtils redisUtils;


    @Resource
    private VideoInfoPostService videoInfoPostService;

    @PostConstruct
    private void consumeTransferQueue() {
        executorService.execute(() -> {
            while (true) {
                try {
                    VideoInfoFilePost videoInfoFilePost = redisUtils.getFileFromTransferQueue();
                    if (videoInfoFilePost == null) {
                      Thread.sleep(1500);
                      continue;
                    }
                    log.info("开始执行文件转码任务");
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);
                    //TODO 执行文件转码任务
                } catch (Exception e) {
                    log.error("执行文件转码任务失败", e);
                }
            }
        });
    }
}
