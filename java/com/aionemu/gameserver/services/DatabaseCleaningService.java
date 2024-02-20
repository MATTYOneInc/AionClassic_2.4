package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CleaningConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCleaningService {

	private Logger log = LoggerFactory.getLogger(DatabaseCleaningService.class);

	private final int SECURITY_MINIMUM_PERIOD = 30;

	private final int WORKER_CHECK_TIME = 10000;

	private static DatabaseCleaningService instance = new DatabaseCleaningService();
	private List<Worker> workers;
	private long startTime;

	private DatabaseCleaningService() {
		if (CleaningConfig.CLEANING_ENABLE)
			runCleaning();
	}

	private void runCleaning() {
		log.info("DatabaseCleaningService: Executing database cleaning");
		startTime = System.currentTimeMillis();

		int periodInDays = CleaningConfig.CLEANING_PERIOD;

		if (periodInDays > SECURITY_MINIMUM_PERIOD) {
			delegateToThreads(CleaningConfig.CLEANING_THREADS, PlayerDAO.getPlayersToDelete(periodInDays, CleaningConfig.CLEANING_LIMIT));
			monitoringProcess();
		}
		else {
			log.warn("The configured days for database cleaning is to low. For security reasons the service will only execute with periods over 30 days!");
		}
	}

	private void monitoringProcess() {
		while (!allWorkersReady())
			try {
				Thread.sleep(WORKER_CHECK_TIME);
				log.info("DatabaseCleaningService: Until now " + currentlyDeletedChars() + " chars deleted in " + (System.currentTimeMillis() - startTime) / 1000L + " seconds!");
			}
			catch (InterruptedException ex) {
				log.error("DatabaseCleaningService: Got Interrupted!");
			}
	}

	private boolean allWorkersReady() {
		for (Worker w : workers) {
			if (!w._READY)
				return false;
		}
		return true;
	}

	private int currentlyDeletedChars() {
		int deletedChars = 0;
		for (Worker w : workers) {
			deletedChars += w.deletedChars;
		}
		return deletedChars;
	}

	private void delegateToThreads(int numberOfThreads, List<Integer> idsToDelegate) {
		workers = new ArrayList<Worker>();
		log.info("DatabaseCleaningService: Executing deletion over " + numberOfThreads + " longrunning threads");

		int itr = 0;
		for (int workerNo = 0; itr < idsToDelegate.size(); workerNo %= numberOfThreads) {
			if (workerNo >= workers.size())
				workers.add(new Worker());
			workers.get(workerNo).ids.add(idsToDelegate.get(itr));

			itr++;
			workerNo++;
		}

		for (Worker w : workers)
			ThreadPoolManager.getInstance().executeLongRunning(w);
	}

	public static DatabaseCleaningService getInstance() {
		return instance;
	}

	private class Worker implements Runnable {

		private List<Integer> ids = new ArrayList<Integer>();
		private int deletedChars = 0;
		private boolean _READY = false;

		private Worker() {
		}

		public void run() {
			for (int id : ids) {
				PlayerService.deletePlayerFromDB(id);
				deletedChars += 1;
			}
			_READY = true;
		}
	}
}
