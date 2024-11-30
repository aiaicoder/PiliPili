package com.pilipili.manager;


import com.pilipili.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.snapshot.SnapshotRequest;
import com.qcloud.cos.model.ciModel.snapshot.SnapshotResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;


/**
 * Cos 对象存储操作
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;


    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }



    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    //截取视频的第一帧
    public String generateSnapshot(String filePath, String fileName){
        SnapshotRequest request = new SnapshotRequest();
        request.setBucketName(cosClientConfig.getBucket()); // 设置存储桶名称
        request.getInput().setObject(filePath);
        request.getOutput().setBucket(cosClientConfig.getBucket());
        request.getOutput().setRegion(cosClientConfig.getRegion());
        request.setMode("keyframe");
        request.setTime("0");
        request.setHeight("250");
        request.setWidth("200");
        request.getOutput().setObject(fileName);
        SnapshotResponse snapshot = cosClient.generateSnapshot(request);
        return snapshot.getOutput().getObject();
    }

}
