package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

@SpringBootTest
class WebApplicationDemoTests {

    @Test
    void contextLoads() {
        System.out.println(StringUtils.trimAllWhitespace("hello wor ld"));
    }
}
