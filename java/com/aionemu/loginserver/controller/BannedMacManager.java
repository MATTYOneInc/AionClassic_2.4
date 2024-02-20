package com.aionemu.loginserver.controller;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.loginserver.dao.BannedMacDAO;
import com.aionemu.loginserver.model.base.BannedMacEntry;

/**
 *
 * @author KID
 *
 */
public class BannedMacManager {
	private static BannedMacManager manager = new BannedMacManager();

	public static BannedMacManager getInstance() {
		return manager;
	}

	public BannedMacManager() {
		bannedList = BannedMacDAO.load();
	}

	private Map<String, BannedMacEntry> bannedList = new FastMap<String, BannedMacEntry>();

	public void unban(String address, String details) {
		if(bannedList.containsKey(address)) {
			bannedList.remove(address);
			BannedMacDAO.remove(address);
		}
	}

	public void ban(String address, long time, String details) {
		BannedMacEntry mac = new BannedMacEntry(address, new Timestamp(time), details);
		this.bannedList.put(address, mac);
		BannedMacDAO.update(mac);
	}

	public final Map<String, BannedMacEntry> getMap() {
		return this.bannedList;
	}
}
