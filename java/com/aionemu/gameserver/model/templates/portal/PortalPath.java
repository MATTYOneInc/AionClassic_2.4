package com.aionemu.gameserver.model.templates.portal;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalPath")
public class PortalPath {

	@XmlElement(name = "portal_req")
	protected PortalReq portalReq;

	@XmlAttribute(name = "dialog")
	protected int dialog;

	@XmlAttribute(name = "loc_id")
	protected int locId;

	@XmlAttribute(name = "player_count")
	protected int playerCount;

	@XmlAttribute(name = "instance")
	protected boolean instance;

	@XmlAttribute(name = "siege_id")
	protected int siegeId;

	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;

	@XmlAttribute(name = "err_group")
	protected int errGroup;

	public PortalReq getPortalReq() {
		return portalReq;
	}

	public int getDialog() {
		return dialog;
	}

	public void setDialog(int value) {
		dialog = value;
	}

	public int getLocId() {
		return locId;
	}

	public void setLocId(int value) {
		locId = value;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int value) {
		playerCount = value;
	}

	public boolean isInstance() {
		return instance;
	}

	public void setInstance(boolean value) {
		instance = value;
	}

	public int getSiegeId() {
		return siegeId;
	}

	public Race getRace() {
		return race;
	}

	public int getErrGroup() {
		return errGroup;
	}
}
