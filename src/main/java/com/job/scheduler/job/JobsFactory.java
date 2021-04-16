package com.job.scheduler.job;

import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobsFactory {

    private Map<String, Job> job;

    @Autowired
    ApplicationContext context;

    @PostConstruct
    private void createFactory() {
        job = new HashMap<>();
        job.put("EMAIL_JOB", context.getBean(EmailJob.class));
        job.put("DB_JOB", context.getBean(DataBaseJob.class));
    }

    public Job getJob(String jobGroup) {
        return job.getOrDefault(jobGroup, context.getBean(NoOpJob.class));
    }
}
