package com.job.scheduler.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.scheduler.bean.FixedScheduleJobDescriptor;
import com.job.scheduler.bean.ImmediateJobDescriptor;
import com.job.scheduler.bean.ScheduleJobDescriptor;
import com.job.scheduler.bean.ScheduledJobResponse;
import com.job.scheduler.service.JobScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class JobSchedulerAPITest {
    private static final String DYNAMIC_SCHEDULE_URI = "http://localhost:8080/schedule/cron";
    private static final String IMMEDIATE_SCHEDULE_URI = "http://localhost:8080/schedule/now";
    private static final String FIXED_SCHEDULE_URI = "http://localhost:8080/schedule/fixed/interval";
    private static final String JOB_STATUS_URI = "http://localhost:8080/schedule/status";
    private static final String JOB_TOGGLE_URI = "http://localhost:8080/schedule/toggle";
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JobSchedulerAPI jobSchedulerAPI;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JobScheduleService jobScheduleService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(JobSchedulerAPI.class);
    }

    @Test
    public void shouldScheduleDynamicJobSuccessfully() throws Exception {

        ScheduleJobDescriptor scheduleJobDescriptor = ScheduleJobDescriptor.builder()
                .jobName("Job Example-1")
                .jobGroup("EMAIL_JOB")
                .cron("*/5 * * * * ?")
                .priority(0)
                .build();
        ScheduledJobResponse scheduledJobResponse = ScheduledJobResponse.builder()
                .jobName("Job Example-1")
                .jobGroup("EMAIL_JOB")
                .cron("*/5 * * * * ?")
                .fixedIntervalInSeconds(null)
                .priority(0)
                .status("success")
                .build();
        when(jobScheduleService.schedule(any(ScheduleJobDescriptor.class)))
                .thenReturn(scheduledJobResponse);

        mockMvc.perform(post(DYNAMIC_SCHEDULE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleJobDescriptor)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobName").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobGroup").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cron").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fixedIntervalInSeconds").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").isNotEmpty());

    }

    @Test
    public void shouldRunJobImmediateSuccessfully() throws Exception {
        ImmediateJobDescriptor immediateJobDescriptor = ImmediateJobDescriptor.builder()
                .jobName("Job Example-1")
                .jobGroup("EMAIL_JOB")
                .runNow(true)
                .build();

        ScheduledJobResponse immediateJobResponse = ScheduledJobResponse.builder()
                .jobName("Job Example-1")
                .cron(null)
                .fixedIntervalInSeconds(null)
                .priority(0)
                .status("success")
                .build();
        when(jobScheduleService.schedule(any(ImmediateJobDescriptor.class)))
                .thenReturn(immediateJobResponse);

        mockMvc.perform(post(IMMEDIATE_SCHEDULE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(immediateJobDescriptor)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobName").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cron").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fixedIntervalInSeconds").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").isNotEmpty());

    }

    @Test
    public void shouldRunFixedIntervalJobSuccessfully() throws Exception {
        FixedScheduleJobDescriptor fixedScheduleJobDescriptor = FixedScheduleJobDescriptor.builder()
                .jobName("Job Example-1")
                .jobGroup("EMAIL_JOB")
                .intervalInSeconds(10)
                .build();

        ScheduledJobResponse fixedJobResponse = ScheduledJobResponse.builder()
                .jobName("Job Example-1")
                .cron(null)
                .fixedIntervalInSeconds(10)
                .priority(0)
                .status("success")
                .build();
        when(jobScheduleService.schedule(any(FixedScheduleJobDescriptor.class)))
                .thenReturn(fixedJobResponse);

        mockMvc.perform(post(FIXED_SCHEDULE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fixedScheduleJobDescriptor)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobName").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cron").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fixedIntervalInSeconds").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").isNotEmpty());

    }

    @Test
    public void shouldGetJobStatusSuccessfully() throws Exception {
        when(jobScheduleService.fetchSchedule("Job Example-1", "EMAIL_JOB"))
                .thenReturn("COMPLETE");

        mockMvc.perform(get(JOB_STATUS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("job_name", "Job Example-1")
                .param("job_group", "EMAIL_JOB")
                .content("COMPLETE"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isString());

    }

    @Test
    public void shouldGetToggleStatusSuccessfully() throws Exception {
        when(jobScheduleService.toggleSchedule("Job Example-1", true))
                .thenReturn("Job is paused");

        mockMvc.perform(post(JOB_TOGGLE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("job_name", "Job Example-1")
                .param("pause", "true")
                .content("Job is paused"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isString());

    }


}
