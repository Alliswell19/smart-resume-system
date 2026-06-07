package com.smartresume;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
@MapperScan("com.smartresume.mapper")
public class SmartResumeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartResumeSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner testDatabaseConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
                System.out.println("✅ 数据库连接成功: " + result);
            } catch (Exception e) {
                System.err.println("❌ 数据库连接失败: " + e.getMessage());
            }
        };
    }
}