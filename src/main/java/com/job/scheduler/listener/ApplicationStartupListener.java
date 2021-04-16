package com.job.scheduler.listener;

import com.job.scheduler.entity.Schedule;
import com.job.scheduler.service.JobScheduleService;
import com.job.scheduler.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    JobScheduleService jobScheduleService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Starting Active Jobs....");
        List<Schedule> schedules = schedulerService.fetchActiveJobs();
        schedules.forEach(i -> {
            try {
                    scheduler.scheduleJob(jobScheduleService.buildJobDetail(i.getJobName(), i.getJobGroup()), JobScheduleService.buildJobTrigger(i));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

    }
}
