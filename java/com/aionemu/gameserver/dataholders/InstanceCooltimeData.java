/* Copyright (c) 2018 To Aion Ryzen Project */


package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.InstanceCooltime;
import com.aionemu.gameserver.services.instance.InstanceService;
import javolution.util.FastMap;
import org.joda.time.DateTime;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "instance_cooltimes")
public class InstanceCooltimeData
{
	@XmlElement(name = "instance_cooltime", required = true)
	protected List<InstanceCooltime> instanceCooltime;
	private FastMap<Integer, InstanceCooltime> instanceCooltimes = new FastMap<Integer, InstanceCooltime>();
	private HashMap<Integer, Integer> syncIdToMapId = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> syncId = new HashMap<Integer, Integer>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (InstanceCooltime tmp : instanceCooltime) {
			instanceCooltimes.put(tmp.getWorldId(), tmp);
			syncIdToMapId.put(tmp.getId(), tmp.getWorldId());
			syncId.put(tmp.getSynchId(), tmp.getWorldId());
		}
		instanceCooltime.clear();
	}
	
	public FastMap<Integer, InstanceCooltime> getAllInstances() {
		return instanceCooltimes;
	}
	
	public InstanceCooltime getInstanceCooltimeByWorldId(int worldId) {
		return instanceCooltimes.get(worldId);
	}
	
	public int getWorldId(int syncId) {
		if (!syncIdToMapId.containsKey(syncId)) {
			return 0;
		}
		return syncIdToMapId.get(syncId);
	}

	public int getSynchId(int id) {
		if (!syncId.containsKey(id)) {
			return 0;
		}
		return syncId.get(id);
	}


	public int getInstanceEntranceCountByWorldId(int worldId) {
		InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		if (clt != null) {
			return clt.getMaxEntriesCount();
		} else {
			return 0;
		}
	}

	public long getInstanceEntranceCooltime(Player player, int worldId) {
		int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
		long instanceCoolTime = 0;
		InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		if (clt != null) {
			instanceCoolTime = clt.getEntCoolTime();
			if (clt.getCoolTimeType().isDaily() || clt.getCoolTimeType().isRelative()) {
				DateTime now = DateTime.now();
				DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 9, 0, 0);
				if (now.isAfter(repeatDate)) {
					repeatDate = repeatDate.plusHours(24);
				}
				instanceCoolTime = repeatDate.getMillis();
			} else if (clt.getCoolTimeType().isWeekly()) {
				String[] days = clt.getTypeValue().split(",");
				instanceCoolTime = getUpdateHours(days, clt.getEntCoolTime() / 100);
			}
		} if (instanceCooldownRate != 1) {
			instanceCoolTime = System.currentTimeMillis() + ((instanceCoolTime - System.currentTimeMillis()) / instanceCooldownRate);
		}
		return instanceCoolTime;
	}

	private long getUpdateHours(String[] days, int hour) {
		DateTime now = DateTime.now();
		DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, 0, 0);
		int curentDay = now.getDayOfWeek();
		for (String name : days) {
			int day = getDay(name);
			if (day < curentDay) {
				continue;
			} if (day == curentDay) {
				if (now.isBefore(repeatDate)) {
					return repeatDate.getMillis();
				}
			} else {
				repeatDate = repeatDate.plusDays(day - curentDay);
				return repeatDate.getMillis();
			}
		}
		return repeatDate.plusDays((7 - curentDay) + getDay(days[0])).getMillis();
	}

	private int getDay(String day) {
		if (day.equals("Mon")) {
			return 1;
		} else if (day.equals("Tue")) {
			return 2;
		} else if (day.equals("Wed")) {
			return 3;
		} else if (day.equals("Thu")) {
			return 4;
		} else if (day.equals("Fri")) {
			return 5;
		} else if (day.equals("Sat")) {
			return 6;
		} else if (day.equals("Sun")) {
			return 7;
		}
		throw new IllegalArgumentException("Invalid Day: " + day);
	}
	
	public Integer size() {
		return instanceCooltimes.size();
	}
}