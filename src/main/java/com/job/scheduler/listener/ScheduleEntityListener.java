package com.job.scheduler.listener;

import com.job.scheduler.entity.Schedule;
import com.job.scheduler.service.JobScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@Slf4j
@Component
public class ScheduleEntityListener {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobScheduleService jobScheduleService;

    @PostPersist
    @PostUpdate
    private void scheduleCron(Schedule schedule) {
        log.info("creating/modifying the Job....");
        try {
            String jobName = schedule.getJobName();
            String jobGroup = schedule.getJobGroup();
            scheduler.deleteJob(new JobKey(jobName, jobGroup));
            scheduler.unscheduleJob(new TriggerKey(jobName, jobGroup));
            if (!schedule.isPause())
                scheduler.scheduleJob(jobScheduleService.buildJobDetail(jobName, jobGroup),
                        JobScheduleService.buildJobTrigger(schedule));
        } catch (SchedulerException e) {
            log.error("Error: " ,e);
        }
    }
}