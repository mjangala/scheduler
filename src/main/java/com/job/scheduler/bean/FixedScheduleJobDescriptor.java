package com.job.scheduler.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixedScheduleJobDescriptor {
    @ApiModelProperty(required = true, value = "Job Name", example = "Job Example-3")
    private String jobName;
    @ApiModelProperty(required = true, value = "Job Group", example = "EMAIL_JOB")
    private String jobGroup;
    @ApiModelProperty(required = true, value = "Fixed interval in seconds", example = "5")
    private int intervalInSeconds;
    @ApiModelProperty(required = true, value = "Priority of the Job. 0 - Least Priority", example = "10")
    private int priority;
}
