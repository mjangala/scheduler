package com.job.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataBaseJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String jobName = jobExecutionContext.getTrigger().getJobKey().getName();
        Date nextFireTime = jobExecutionContext.getTrigger().getNextFireTime();
        System.out.println("DataBase Operation for the job" + jobName + " next fire time is at " + nextFireTime);
    }
}
