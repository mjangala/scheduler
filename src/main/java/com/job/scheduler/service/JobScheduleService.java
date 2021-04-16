package com.job.scheduler.service;

import com.job.scheduler.bean.*;
import com.job.scheduler.entity.Schedule;
import com.job.scheduler.job.JobsFactory;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class JobScheduleService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private JobsFactory jobsFactory;

    public ScheduledJobResponse schedule(ScheduleJobDescriptor scheduleJobDescriptor) {
        String jobName = scheduleJobDescriptor.getJobName();
        boolean isValidExpression = isValidCronExpression(scheduleJobDescriptor.getCron());
        if (!isValidExpression)
            return createResponse(scheduleJobDescriptor, "Not a valid Cron Expression");

        Schedule existingSchedule = schedulerService.fetchJob(jobName);
        return Optional.ofNullable(existingSchedule)
                .filter(i -> !i.isPause())
                .map(i -> {
                    i.setCron(scheduleJobDescriptor.getCron());
                    schedulerService.save(i);
                    return createResponse(scheduleJobDescriptor, "Successfully rescheduled");
                })
                .orElseGet(() -> createNewSchedule(scheduleJobDescriptor));
    }

    private ScheduledJobResponse createResponse(Object jobDescriptor, String message) {
        if (jobDescriptor instanceof ScheduleJobDescriptor) {
            ScheduleJobDescriptor scheduleJobDescriptor = (ScheduleJobDescriptor) jobDescriptor;
            return new ScheduledJobResponse(scheduleJobDescriptor.getJobName(),
                    scheduleJobDescriptor.getJobGroup(),
                    scheduleJobDescriptor.getCron(),
                    null,
                    scheduleJobDescriptor.getPriority(),
                    message);
        } else if (jobDescriptor instanceof ImmediateJobDescriptor) {
            ImmediateJobDescriptor immediateJobDescriptor = (ImmediateJobDescriptor) jobDescriptor;
            return new ScheduledJobResponse(immediateJobDescriptor.getJobName(), immediateJobDescriptor.getJobGroup(),
                    null, null, 0, message);
        } else if (jobDescriptor instanceof FixedScheduleJobDescriptor) {
            FixedScheduleJobDescriptor fixedScheduleJobDescriptor = (FixedScheduleJobDescriptor) jobDescriptor;
            return new ScheduledJobResponse(fixedScheduleJobDescriptor.getJobName(),
                    fixedScheduleJobDescriptor.getJobGroup(),
                    null, fixedScheduleJobDescriptor.getIntervalInSeconds(),
                    fixedScheduleJobDescriptor.getPriority(), message);
        }
        return null;
    }

    private boolean isValidCronExpression(String cron) {
        return CronExpression.isValidExpression(cron);
    }

    private ScheduledJobResponse createNewSchedule(ScheduleJobDescriptor scheduleJobDescriptor) {
        Schedule schedule = new Schedule(scheduleJobDescriptor.getJobName(),
                scheduleJobDescriptor.getJobGroup(),
                scheduleJobDescriptor.getCron(),
                null,
                scheduleJobDescriptor.getPriority(),
                false,
                false);
        schedulerService.save(schedule);
        return createResponse(scheduleJobDescriptor, "Successfully Scheduled");
    }

    public ScheduledJobResponse schedule(ImmediateJobDescriptor immediateJobDescriptor) {
        String jobName = immediateJobDescriptor.getJobName();
        String jobGroup = immediateJobDescriptor.getJobGroup();
        Schedule schedule = new Schedule(jobName, jobGroup, null, null, 0, true, true);
        try {
            schedulerService.save(schedule);
            JobDetail jobDetail = buildJobDetail(jobName, jobGroup);
            scheduler.addJob(jobDetail, true);
            scheduler.triggerJob(new JobKey(jobName, jobGroup));
            return createResponse(immediateJobDescriptor, "success");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return createResponse(immediateJobDescriptor, e.getMessage());
        }
    }

    public ScheduledJobResponse schedule(FixedScheduleJobDescriptor jobDescriptor) {
        String jobName = jobDescriptor.getJobName();
        String jobGroup = jobDescriptor.getJobGroup();
        Schedule schedule = new Schedule(jobName, jobGroup, null,
                jobDescriptor.getIntervalInSeconds(), 0,
                false,
                false);
        schedulerService.save(schedule);
        return createResponse(jobDescriptor, "success");
    }

    public JobDetail buildJobDetail(String jobName, String jobGroup) {
        return JobBuilder.newJob(jobsFactory.getJob(jobGroup).getClass())
                .withIdentity(new JobKey(jobName, jobGroup))
                .usingJobData(new JobDataMap())
                .storeDurably()
                .build();
    }

    public static Trigger buildJobTrigger(Schedule schedule) {
        if (!StringUtils.isEmpty(schedule.getCron())) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(schedule.getJobName(), schedule.getJobGroup())
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(schedule.getCron()))
                    .usingJobData("cron", schedule.getCron())
                    .withPriority(schedule.getPriority())
                    .build();
        } else {
            return TriggerBuilder.newTrigger()
                    .withIdentity(schedule.getJobName(), schedule.getJobGroup())
                    .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(
                            Optional.ofNullable(schedule.getFixedInterval())
                                    .orElse(0)))
                    .withPriority(schedule.getPriority())
                    .build();
        }
    }

    public String fetchSchedule(String jobName, String jobGroup) {
        try {
            return Optional.ofNullable(scheduler.getTrigger(TriggerKey.triggerKey(jobName, jobGroup)))
                    .map(i -> {
                        try {
                            return "Running cron at " +
                                    i.getJobDataMap().getString("cron") +
                                    " and the status is " +
                                    scheduler.getTriggerState(i.getKey()).toString();
                        } catch (SchedulerException e) {
                            e.printStackTrace();
                        }
                        return "No Job Found";
                    })
                    .orElseGet(() -> fetchScheduledJobStatus(jobName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }

    private String fetchScheduledJobStatus(String jobName) {
        Schedule schedule = schedulerService.fetchJob(jobName);
        return Optional.of(schedule)
                .filter(Schedule::isPause)
                .map(i -> "Job - " + jobName + " is Paused")
                .orElse("No Job Found");
    }

    public String toggleSchedule(String jobName, boolean pause) {
        Schedule schedule = schedulerService.fetchJob(jobName);
        String result = jobName + "- ";
        if (schedule == null) return result + "No such Job Found";
        boolean pauseARunningJob = !schedule.isPause() && pause;
        boolean runAPausedJob = schedule.isPause() && !pause;
        if (pauseARunningJob) {
            schedule.setPause(true);
            schedulerService.save(schedule);
            result += "Job is paused";
        } else if (runAPausedJob) {
            schedule.setPause(false);
            schedulerService.save(schedule);
            result += "Job is set to run";
        } else {
            result += "Cannot run/pause a running/paused Job";
        }
        return result;
    }
}
