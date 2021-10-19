package com.study.springbatch_scheduler.quartz;

import static org.quartz.JobBuilder.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QuartzSchedulingConfiguration {
	private final Scheduler scheduler;

	@PostConstruct
	public void start(){
		log.info("QuartzScheduler start");
		try{
			JobDetail jobDetail = buildJobDetail(QuartzTestJob.class, "jobDetailA","5초마다 진행",new HashMap());
			JobDetail jobDetailStop = buildJobDetail(QuartzStopTestJob.class, "jobDetailB","조건부에 따라 멈춤",new HashMap());

			//DB에 존재하는 스케줄이라면 삭제
			validExistJob(jobDetail);
			validExistJob(jobDetailStop);

			//Job과 Trigger설정
			scheduler.scheduleJob(jobDetail, buildJobTrigger("0/5 * * * * ?"));
			scheduler.scheduleJob(jobDetailStop, buildJobTrigger("0/10 * * * * ?"));
		}catch(SchedulerException e){
			e.printStackTrace();
		}
	}

	//CronTrigger
	public Trigger buildJobTrigger(String scheduleExp){
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
			.build();
	}

	//SimpleJobTrigger 시분초 만 가능한거같다.
	public Trigger buildSimpleJobTrigger(Integer hour){
		return TriggerBuilder.newTrigger()
			.withSchedule(SimpleScheduleBuilder
				.simpleSchedule()
				.repeatForever()
				.withIntervalInHours(hour))
			.build();
	}

	public JobDetail buildJobDetail(Class job, String name, String desc, Map params){
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(params);

		return JobBuilder
			.newJob(job)
			.withIdentity(name)
			.withDescription(desc)
			.usingJobData(jobDataMap)
			.build();
		// return newJob(job).usingJobData(jobDataMap).build();
	}

	private void validExistJob(JobDetail jobDetail) throws SchedulerException {
		if(scheduler.checkExists(jobDetail.getKey())){
			scheduler.deleteJob(jobDetail.getKey());
		}
	}
}
