package com.study.springbatch_scheduler.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QuartzStopTestJob extends QuartzJobBean {
	private JobKey jobKey = null;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		boolean isInterrupt = false;
		log.info("Quartz Job B");

		jobKey = context.getJobDetail().getKey();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date nowDate = new Date();

		try{
			Date date = sdf.parse("2021-10-19 11:15");
			String newDate = sdf.format(nowDate);
			nowDate = sdf.parse(newDate);

			if(nowDate.equals(date)){
				scheduler.pauseJob(jobKey);
				log.info("Quartz Job B 작업중지");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
