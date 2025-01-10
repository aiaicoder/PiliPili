package com.pilipili.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/12 18:51
 */

@Component
@Data
public class AppConfig {

    /**
     * 管理员邮箱
     */
    @Value("${admin.account:}")
    public String adminAccount;

    /**
     * 管理员密码
     */
    @Value("${admin.password:}")
    public String adminPassword;

    @Value("${sa-token.timeout:}")
    public Long tokenTimeout;

    /**
     * 文件目录
     */
    @Value("${project.folder:}")
    public String folder;


    @Value("${es.host.port:10.101.100.2:9200}")
    public String esHostPort;

    @Value("${es.index.video.name:pilipili_video}")
    public String esIndexVideoName;



}
