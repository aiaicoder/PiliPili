package com.pilipili.web;

import com.pilipili.component.EsSearchComponent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/7 14:30
 */
@Component
public class InitialEs implements ApplicationRunner {

    @Resource
    private EsSearchComponent esSearchComponent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        esSearchComponent.createIndex();
    }
}
