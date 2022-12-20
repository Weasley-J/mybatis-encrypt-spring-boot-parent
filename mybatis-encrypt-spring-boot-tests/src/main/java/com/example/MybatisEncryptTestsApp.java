package com.example;

import cn.alphahub.dtt.plus.framework.annotations.EnableDtt;
import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mybatis encrypt main class of Tests
 */

@EnableDtt(scanBasePackages = {"com.example.demain"})
@SpringBootApplication
@EnableMybatisEncryption
public class MybatisEncryptTestsApp {

    public static void main(String[] args) {
        SpringApplication.run(MybatisEncryptTestsApp.class, args);
    }

}
