package com.pilipili.web.Task;

import com.pilipili.Model.Vo.VideoPlayInfoVo;
import com.pilipili.Model.entity.VideoInfoFilePost;
import com.pilipili.component.EsSearchComponent;
import com.pilipili.enums.UserActionTypeEnum;
import com.pilipili.service.VideoInfoPostService;
import com.pilipili.service.VideoInfoService;
import com.pilipili.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/12/7 14:39
 */
//@Component
@Slf4j
public class ExecuteQueueTask {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);


    @Resource
    private RedisUtils redisUtils;


    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoService videoInfoService;


    @Resource
    private EsSearchComponent esSearchComponent;

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

                    //执行文件转码任务
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);

                } catch (Exception e) {
                    log.error("执行文件转码任务失败", e);
                }
            }
        });
    }


    @PostConstruct
    private void consumeVideoPlayQueue() {
        executorService.execute(() -> {
            while (true) {
                try {
                    VideoPlayInfoVo videoPlayInfoVo = redisUtils.getVideoPlayInfo();
                    if (videoPlayInfoVo == null) {
                        Thread.sleep(1500);
                        continue;
                    }
                    //更新播放数
                    videoInfoService.addReadCount(videoPlayInfoVo.getVideoId());
                    if (StringUtils.isNotEmpty(videoPlayInfoVo.getUserId())) {
                        //todo 记录播放历史
                    }
                    //记录当日的播放数量
                    redisUtils.recordVideoPlayCount(videoPlayInfoVo.getVideoId());
                    //更新es的播放数
                    esSearchComponent.updateDocCount(videoPlayInfoVo.getVideoId(), UserActionTypeEnum.VIDEO_PLAY.getField(), 1);

                } catch (Exception e) {
                    log.error("执行文件转码任务失败", e);
                }
            }
        });
    }
}
