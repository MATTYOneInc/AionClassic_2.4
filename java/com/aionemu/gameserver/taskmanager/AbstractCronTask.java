package com.aionemu.gameserver.taskmanager;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

public abstract class AbstractCronTask implements Runnable
{
	private String cronExpressionString;
	private CronExpression runExpression;
	private int runTime;
	private long period;

	public final int getRunTime() {
		return runTime;
	}

	abstract protected long getRunDelay();
	protected void preInit() {
	}

	protected void postInit() {
	}

	public final String getCronExpressionString() {
		return cronExpressionString;
	}

	abstract protected String getServerTimeVariable();

	public long getPeriod() {
		return period;
	}

	protected void preRun() {
	}

	abstract protected void executeTask();
	abstract protected boolean canRunOnInit();

	protected void postRun() {
	}

	public AbstractCronTask(String cronExpression) throws ParseException {
		if (cronExpression == null)
			throw new NullPointerException("cronExpressionString");
		cronExpressionString = cronExpression;
		runTime = ServerVariablesDAO.load(getServerTimeVariable());
		preInit();
		runExpression = new CronExpression(cronExpressionString);
		Date nextDate = runExpression.getTimeAfter(new Date());
		Date nextAfterDate = runExpression.getTimeAfter(nextDate);
		period = nextAfterDate.getTime() - nextDate.getTime();
		postInit();
		if (getRunDelay() == 0) {
			if (canRunOnInit())
				ThreadPoolManager.getInstance().schedule(this, 0);
			else {
				saveNextRunTime();
			}
		}
		scheduleNextRun();
	}

	private void scheduleNextRun() {
		CronService.getInstance().schedule(this, cronExpressionString, true);
	}

	private void saveNextRunTime() {
		Date nextDate = runExpression.getTimeAfter(new Date());
		runTime = (int) (nextDate.getTime() / 1000);
		ServerVariablesDAO.store(getServerTimeVariable(), runTime);
	}

	@Override
	public final void run() {
        if (getRunDelay() > 0) {
            ThreadPoolManager.getInstance().schedule(this, getRunDelay());
        } else {
		    preRun();
		    executeTask();
		    saveNextRunTime();
		    postRun();
        }
	}
}
