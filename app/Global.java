import org.quartz.*;
import org.quartz.impl.*;
import play.*;

public class Global extends GlobalSettings {

	private Scheduler scheduler;

	@Override
	public void onStart(Application app) {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();

			JobDetail job = JobBuilder.newJob(Worker.class)
					.withIdentity("job1", "group1").build();

			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("trigger1", "group1")
					.startNow()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(10).withRepeatCount(3))
					.build();
			scheduler.scheduleJob(job, trigger);
			Logger.info("Quartz scheduler started.");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	@Override
	public void onStop(Application app) {
		try {
			if (scheduler != null) {
				scheduler.shutdown();
				scheduler = null;
				Logger.info("Quartz scheduler shutdown.");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}
	
	public static class Worker implements Job {
		public void execute(JobExecutionContext ctx) {
			Logger.debug("Scheduled Job triggered at: " + new java.util.Date());
		}
	}

}
