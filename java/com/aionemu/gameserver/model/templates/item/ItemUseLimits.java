package com.aionemu.gameserver.model.templates.item;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.zone.ZoneName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UseLimits")
public class ItemUseLimits {

	@XmlAttribute(name = "usedelay")
	private int useDelay;

	@XmlAttribute(name = "usedelayid")
	private int useDelayId;

	@XmlAttribute(name = "ownership_world")
	private List<Integer> ownershipWorldId;

	@XmlAttribute
	private String usearea;

	@XmlAttribute(name = "gender")
	private Gender genderPermitted;

	@XmlAttribute(name = "ride_usable")
	private Boolean rideUsable;

	@XmlAttribute(name = "rank_min")
	private int minRank;

	@XmlAttribute(name = "rank_max")
	private int maxRank = AbyssRankEnum.SUPREME_COMMANDER.getId();

	public int getDelayId() {
		return useDelayId;
	}

	public void setDelayId(int delayId) {
		useDelayId = delayId;
	}

	public int getDelayTime() {
		return useDelay;
	}

	public void setDelayTime(int useDelay) {
		this.useDelay = useDelay;
	}

	public ZoneName getUseArea() {
		if (usearea == null)
			return null;
		try {
			return ZoneName.get(usearea);
		}
		catch (Exception e) {
		}
		return null;
	}

	public List<Integer> getOwnershipWorld() {
        if (ownershipWorldId == null) {
        	ownershipWorldId = Collections.emptyList();
        }
        return this.ownershipWorldId;
    }

	public Gender getGenderPermitted() {
		return genderPermitted;
	}

	public int getMinRank() {
		return minRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public boolean verifyRank(int rank) {
		return (minRank <= rank && maxRank >= rank) || rank >= minRank;
	}
}