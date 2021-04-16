package com.job.scheduler.api;

import com.job.scheduler.bean.FixedScheduleJobDescriptor;
import com.job.scheduler.bean.ImmediateJobDescriptor;
import com.job.scheduler.bean.ScheduleJobDescriptor;
import com.job.scheduler.bean.ScheduledJobResponse;
import com.job.scheduler.service.JobScheduleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "job-scheduler")
public class JobSchedulerAPI {

    @Autowired
    JobScheduleService jobScheduleService;

    @PostMapping(value = "/schedule/cron")
    @ApiOperation("Cron Job Scheduling [Add, Reschedule]")
    @ApiResponses(@ApiResponse(code = 200, message = "Scheduled/Rescheduled the Job Successfully",
            response = ScheduledJobResponse.class))
    public ScheduledJobResponse dynamicSchedule(@RequestBody ScheduleJobDescriptor
                                                        scheduleJobDescriptor) {

        return jobScheduleService.schedule(scheduleJobDescriptor);
    }

    @PostMapping(value = "/schedule/now")
    @ApiOperation("Run a Job Now")
    @ApiResponses(@ApiResponse(code = 200, message = "Executed the Job Successfully",
            response = ScheduledJobResponse.class))
    public ScheduledJobResponse staticSchedule(@RequestBody ImmediateJobDescriptor
                                                       immediateJobDescriptor) {

        return jobScheduleService.schedule(immediateJobDescriptor);
    }

    @PostMapping(value = "/schedule/fixed/interval")
    @ApiOperation("Run a Job at Fixed Intervals of Time")
    @ApiResponses(@ApiResponse(code = 200,
            message = "Scheduled the Job successfully at Fixed Intervals of Time",
            response = ScheduledJobResponse.class))
    public ScheduledJobResponse fixedIntervalSchedule(@RequestBody FixedScheduleJobDescriptor
                                                              jobDescriptor) {

        return jobScheduleService.schedule(jobDescriptor);
    }

    @GetMapping(value = "/schedule/status")
    @ApiOperation("Check the Status of a Running/Paused Job")
    @ApiResponses(@ApiResponse(code = 200, message = "Success", response = String.class))
    public String fetchSchedule(@ApiParam(name = "job_name",
            type = "String", value = "Name of the Job",
            example = "Job Example-1", required = true)
                                @RequestParam("job_name") String jobName,
                                @ApiParam(name = "job_group",
                                        type = "String", value = "Name of the Job Group",
                                        example = "EMAIL_JOB", required = true)
                                @RequestParam("job_group") String jobGroup) {
        return jobScheduleService.fetchSchedule(jobName, jobGroup);
    }

    @PostMapping(value = "schedule/toggle")
    @ApiOperation("Pause/Run a Run/Pause Job")
    @ApiResponses(@ApiResponse(code = 200, message = "Paused/Run the Job Successfully",
            response = String.class))
    public String ToggleJobSchedule(@ApiParam(name = "job_name",
            type = "String", value = "Name of the Job",
            example = "Job Example-1", required = true)
                                    @RequestParam("job_name") String jobName,
                                    @ApiParam(name = "pause",
                                            type = "boolean", value = "Flag to toggle the Job",
                                            example = "true", required = true)
                                    @RequestParam("pause") boolean pause) {
        return jobScheduleService.toggleSchedule(jobName, pause);
    }
}
