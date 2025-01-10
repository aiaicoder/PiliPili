package com.pilipili.config.es;

import com.pilipili.config.AppConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/7 13:22
 */
@Configuration
public class EsConfiguration extends AbstractElasticsearchConfiguration implements DisposableBean {

    @Resource
    private AppConfig appConfig;

    private RestHighLevelClient client;

    @Override
    public void destroy() throws Exception {
        if (client != null){
            client.close();
        }
    }



    @NotNull
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(appConfig.getEsHostPort())
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
