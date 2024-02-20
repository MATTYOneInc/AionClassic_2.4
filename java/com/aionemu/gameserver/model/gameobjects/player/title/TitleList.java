package com.aionemu.gameserver.model.gameobjects.player.title;

import com.aionemu.gameserver.dao.PlayerTitleListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.listeners.TitleChangeListener;
import com.aionemu.gameserver.model.templates.TitleTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_TITLE;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

import java.util.Collection;

public class TitleList {

	private final FastMap<Integer, Title> titles;
	private Player owner;

	public TitleList() {
		this.titles = new FastMap<Integer, Title>();
		this.owner = null;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean contains(int titleId) {
		return titles.containsKey(titleId);
	}

	public void addEntry(int titleId, int remaining) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		titles.put(titleId, new Title(tt, titleId, remaining));
	}

	public boolean addTitle(int titleId, boolean questReward, int time) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		} if (owner != null) {
			if (owner.getRace() != tt.getRace() && tt.getRace() != Race.PC_ALL) {
				PacketSendUtility.sendMessage(owner, "This title is not available for your race.");
				return false;
			}
			Title entry = new Title(tt, titleId, time);
			if (!titles.containsKey(titleId)) {
				titles.put(titleId, entry);
				if (time != 0) {
					ExpireTimerTask.getInstance().addTask(entry, owner);
				}
			} else {
				PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_TOOLTIP_LEARNED_TITLE);
				return false;
			} if (questReward) {
				PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_QUEST_GET_REWARD_TITLE(tt.getNameId()));
			} else {
				PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_MSG_GET_CASH_TITLE(tt.getNameId()));
			}
			PacketSendUtility.sendPacket(owner, new S_TITLE(owner));
			PlayerTitleListDAO.storeTitles(owner, entry);
			return true;
		}
		return false;
	}

	public void setDisplayTitle(int titleId) {
        PacketSendUtility.sendPacket(owner, new S_TITLE(titleId));
        PacketSendUtility.broadcastPacketAndReceive(owner, new S_TITLE(owner, titleId));
		if (owner.getCommonData().getTitleId() > 0) {
		    if (owner.getGameStats() != null) {
                TitleChangeListener.onTitleChange(owner.getGameStats(), owner.getCommonData().getTitleId(), false);
			}
        }
        owner.getCommonData().setTitleId(titleId);
		if (titleId > 0 && owner.getGameStats() != null) {
            TitleChangeListener.onTitleChange(owner.getGameStats(), titleId, true);
        }
    }

	public void removeTitle(int titleId) {
		if (!titles.containsKey(titleId)) {
			return;
		} if (owner.getCommonData().getTitleId() == titleId) {
			setDisplayTitle(-1);
		}
		titles.remove(titleId);
		PacketSendUtility.sendPacket(owner, new S_TITLE(owner));
		PlayerTitleListDAO.removeTitle(owner.getObjectId(), titleId);
	}

	public int size() {
		return titles.size();
	}

	public Collection<Title> getTitles() {
		return titles.values();
	}
}
