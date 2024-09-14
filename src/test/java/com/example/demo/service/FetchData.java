package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class FetchData {

    @Autowired
    private CronJobService cronJobService;

    @Test
    public void testPerformTask() {
        cronJobService.performTask();
    }
}
