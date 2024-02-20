package com.aionemu.gameserver.utils;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * @author Rolandas
 */
public class WorkStealThreadFactory extends PriorityThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory
{
	public WorkStealThreadFactory(String namePrefix)
	{
		super(namePrefix, Thread.NORM_PRIORITY);
	}

	public void setDefaultPool(ForkJoinPool pool)
	{
		// In Java 8 and jsr166 common pool exists, not in Java 7
		/*
		if (pool == null)
			pool = ForkJoinPool.commonPool();
		 */
		super.setDefaultPool(pool);
	}

	@Override
	public ForkJoinPool getDefaultPool()
	{
		return (ForkJoinPool) super.getDefaultPool();
	}

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool)
	{
		return new WorkStealThread(pool);
	}

	private static class WorkStealThread extends ForkJoinWorkerThread
	{
		private static final Logger log = LoggerFactory.getLogger(WorkStealThread.class);

		/**
		 * Creates a ForkJoinWorkerThread operating in the given pool.
		 *
		 * @param pool the pool this thread works in
		 * @throws NullPointerException if pool is null
		 */
		public WorkStealThread(ForkJoinPool pool)
		{
			super(pool);
		}

		@Override
		protected void onStart()
		{
			super.onStart();
		}

		@Override
		protected void onTermination(Throwable exception)
		{
			if (exception != null)
				log.error("Error - Thread: " + this.getName() + " terminated abnormaly: " + exception);

			super.onTermination(exception);
		}
	}
}
