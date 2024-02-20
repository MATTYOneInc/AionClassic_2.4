package com.aionemu.gameserver.model.templates.portal;

import com.aionemu.gameserver.configs.main.GSConfig;

import javax.xml.bind.annotation.*;
import java.util.List;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalReq")
public class PortalReq {

	@XmlElement(name = "quest_req")
	protected List<QuestReq> questReq;

	@XmlElement(name = "item_req")
	protected List<ItemReq> itemReq;

	@XmlAttribute(name = "min_level")
	protected int minLevel;

	@XmlAttribute(name = "max_level")
	protected int maxLevel = GSConfig.PLAYER_MAX_LEVEL;

	@XmlAttribute(name = "kinah_req")
	protected int kinahReq;

	@XmlAttribute(name = "title_id")
	protected int titleId;

	@XmlAttribute(name = "err_level")
	protected int errLevel;

	public List<QuestReq> getQuestReq() {
		return questReq;
	}

	public List<ItemReq> getItemReq() {
		return itemReq;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int value) {
		minLevel = value;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int value) {
		maxLevel = value;
	}

	public int getKinahReq() {
		return kinahReq;
	}

	public void setKinahReq(int value) {
		kinahReq = value;
	}

	public int getTitleId() {
		return titleId;
	}

	public int getErrLevel() {
		return errLevel;
	}
}
