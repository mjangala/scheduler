package com.job.scheduler.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ScheduledJobResponse {
    private String jobName;
    private String jobGroup;
    private String cron;
    private Integer fixedIntervalInSeconds;
    private int priority;
    private String status;
}
