package com.example.demo.schedule;

import com.example.demo.service.CronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiScheduler {
    @Autowired
    CronJobService cronJobService;

    // Runs every 5 minutes
    @Scheduled(cron = "0 0 18 * * ?")
    public void callApi() {
        cronJobService.performTask();
    }
}
