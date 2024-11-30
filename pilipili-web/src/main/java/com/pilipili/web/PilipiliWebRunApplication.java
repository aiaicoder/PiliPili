package com.pilipili.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.swing.*;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/9 23:05
 */
@SpringBootApplication(scanBasePackages = {"com.pilipili"})
@MapperScan("com.pilipili.mapper")
@EnableTransactionManagement
@EnableScheduling
public class PilipiliWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(PilipiliWebRunApplication.class,args);
    }
}
