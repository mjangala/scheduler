package com.job.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EmailJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String jobName = jobExecutionContext.getTrigger().getJobKey().getName();
        Date nextFireTime = jobExecutionContext.getTrigger().getNextFireTime();
        System.out.println("Sending Email for the job " + jobName + " next fire time is at " + nextFireTime);
    }
}
