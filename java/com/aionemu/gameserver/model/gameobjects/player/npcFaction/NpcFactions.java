package com.aionemu.gameserver.model.gameobjects.player.npcFaction;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.factions.NpcFactionTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestMentorType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;

public class NpcFactions
{
	private Player owner;
	
	private Map<Integer, NpcFaction> factions = new HashMap<Integer, NpcFaction>();
	private NpcFaction[] activeNpcFaction = new NpcFaction[2];
	private int[] timeLimit = new int[] {0, 0};
	
	public NpcFactions(Player owner) {
		this.owner = owner;
	}
	
	public void addNpcFaction(NpcFaction faction) {
		factions.put(faction.getId(), faction);
		int type = 0;
		if (faction.isMentor()) {
			type = 1;
		} if (faction.isActive()) {
			activeNpcFaction[type] = faction;
		} if (faction.getTime() == -1) {
			faction.setTime((int) (System.currentTimeMillis() / 1000));
			timeLimit[type] = faction.getTime();
		} else if (timeLimit[type] < faction.getTime() && faction.getState() == ENpcFactionQuestState.COMPLETE) {
			timeLimit[type] = faction.getTime();
		}
	}
	
	public NpcFaction getNpcFactionById(int id) {
		return factions.get(id);
	}
	
	public Collection<NpcFaction> getNpcFactions() {
		return factions.values();
	}
	
	public NpcFaction getActiveNpcFaction(boolean mentor) {
		if (mentor) {
			return activeNpcFaction[1];
		} else {
			return activeNpcFaction[0];
		}
	}
	
	public NpcFaction setActive(int npcFactionId) {
		NpcFaction npcFaction = factions.get(npcFactionId);
		if (npcFaction == null) {
			npcFaction = new NpcFaction(npcFactionId, 0, false, ENpcFactionQuestState.NOTING, 0);
			factions.put(npcFactionId, npcFaction);
		}
		npcFaction.setActive(true);
		if (npcFaction.isMentor()) {
			this.activeNpcFaction[1] = npcFaction;
		} else {
			this.activeNpcFaction[0] = npcFaction;
		}
		return npcFaction;
	}
	
	public void leaveNpcFaction(Npc npc) {
		int targetObjectId = npc.getObjectId();
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null) {
			return;
		}
		NpcFaction npcFaction = getNpcFactionById(npcFactionTemplate.getId());
		if (npcFaction == null || !npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new S_NPC_HTML_MESSAGE(targetObjectId, 1438));
			return;
		}
		PacketSendUtility.sendPacket(owner, new S_NPC_HTML_MESSAGE(targetObjectId, 1353));
		leaveNpcFaction(npcFaction);
	}
	
	private void leaveNpcFaction(NpcFaction npcFaction) {
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(npcFaction.getId());
		PacketSendUtility.sendPacket(owner, new S_MESSAGE_CODE(1300526, new DescriptionId(npcFactionTemplate.getNameId())));
		npcFaction.setActive(false);
		activeNpcFaction[npcFactionTemplate.isMentor() ? 1 : 0] = null;
		if (npcFaction.getState() == ENpcFactionQuestState.START) {
			QuestService.abandonQuest(owner, npcFaction.getQuestId());
			owner.getController().updateZone();
			owner.getController().updateNearbyQuests();
			npcFaction.setState(ENpcFactionQuestState.NOTING);
		}
	}
	
	public void enterGuild(Npc npc) {
		int targetObjectId = npc.getObjectId();
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null) {
			return;
		}
		NpcFaction npcFaction = getNpcFactionById(npcFactionTemplate.getId());
		NpcFaction activeNpcFaction = getActiveNpcFaction(npcFactionTemplate.isMentor());
		int npcFactionId = npcFactionTemplate.getId();
		if (owner.getLevel() < npcFactionTemplate.getMinLevel() || owner.getLevel() > npcFactionTemplate.getMaxLevel()) {
			PacketSendUtility.sendPacket(owner, new S_NPC_HTML_MESSAGE(targetObjectId, 1182));
			return;
		} if (owner.getRace() != npcFactionTemplate.getRace() && !npcFactionTemplate.getRace().equals(Race.NPC)) {
			PacketSendUtility.sendPacket(owner, new S_NPC_HTML_MESSAGE(targetObjectId, 1097));
			return;
		} if (npcFaction != null && npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new S_MESSAGE_CODE(1300525));
			return;
		} if (activeNpcFaction != null && activeNpcFaction.getId() != npcFactionId) {
			askLeaveNpcFaction(npc);
			return;
		} if (npcFaction == null || !npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new S_MESSAGE_CODE(1300524, new DescriptionId(npcFactionTemplate.getNameId())));
			PacketSendUtility.sendPacket(owner, new S_NPC_HTML_MESSAGE(targetObjectId, 1012));
			setActive(npcFactionId);
			sendDailyQuest();
		}
	}
	
	private void askLeaveNpcFaction(final Npc npc) {
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		final NpcFaction activeNpcFaction = getActiveNpcFaction(npcFactionTemplate.isMentor());
		NpcFactionTemplate activeNpcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(activeNpcFaction.getId());
		RequestResponseHandler responseHandler = new RequestResponseHandler(owner) {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				leaveNpcFaction(activeNpcFaction);
				enterGuild(npc);
			}
			@Override
			public void denyRequest(Creature requester, Player responder) {
			}
		};
		boolean requested = owner.getResponseRequester().putRequest(S_ASK.STR_ASK_JOIN_NEW_FACTION, responseHandler);
		if (requested) {
			PacketSendUtility.sendPacket(owner, new S_ASK(S_ASK.STR_ASK_JOIN_NEW_FACTION, 0, 0, new DescriptionId(activeNpcFactionTemplate.getNameId()), new DescriptionId(npcFactionTemplate.getNameId())));
		}
		return;
	}
	
	public void startQuest(QuestTemplate questTemplate) {
		NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor() ? 1 : 0];
		if (npcFaction == null) {
			return;
		} if (npcFaction.getState() != ENpcFactionQuestState.NOTING && npcFaction.getQuestId() == 0) {
			return;
		}
		npcFaction.setState(ENpcFactionQuestState.START);
	}
	
	public void abortQuest(QuestTemplate questTemplate) {
		NpcFaction npcFaction = this.factions.get(questTemplate.getNpcFactionId());
		if (npcFaction == null || !npcFaction.isActive()) {
			return;
		}
		npcFaction.setState(ENpcFactionQuestState.NOTING);
		sendDailyQuest();
	}
	
	public void completeQuest(QuestTemplate questTemplate) {
		NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor() ? 1 : 0];
		if (npcFaction == null) {
			return;
		}
		npcFaction.setTime(getNextTime());
		npcFaction.setState(ENpcFactionQuestState.COMPLETE);
		this.timeLimit[npcFaction.isMentor() ? 1 : 0] = npcFaction.getTime();
		if (questTemplate.getMentorType() == QuestMentorType.MENTOR) {
			owner.getCommonData().setMentorFlagTime((int) (System.currentTimeMillis() / 1000) + 60 * 60 * 24);
			PacketSendUtility.broadcastPacket(owner, new S_TITLE(owner, true), false);
			PacketSendUtility.sendPacket(owner, new S_TITLE(true));
		}
	}
	
	public void sendDailyQuest() {
		for (int i = 0; i < 2; i++) {
			NpcFaction faction = activeNpcFaction[i];
			if (faction == null || !faction.isActive()) {
				continue;
			} if (this.timeLimit[i] > System.currentTimeMillis() / 1000) {
				continue;
			}
			int questId = 0;
			switch (faction.getState()) {
				case COMPLETE:
					if (faction.getTime() > System.currentTimeMillis() / 1000) {
						continue;
					}
				break;
				case START:
					continue;
				case NOTING:
					if (faction.getTime() > System.currentTimeMillis() / 1000) {
						questId = faction.getQuestId();
					}
				break;
			} if (questId == 0) {
				List<QuestTemplate> quests = DataManager.QUEST_DATA.getQuestsByNpcFaction(faction.getId(), owner);
				if (quests.isEmpty()) {
					continue;
				}
				questId = quests.get(Rnd.get(quests.size())).getId();
				faction.setQuestId(questId);
				faction.setTime(getNextTime());
			}
			PacketSendUtility.sendPacket(owner, new S_QUEST(questId, true));
		}
	}
	
	public void onLevelUp() {
		for (int i = 0; i < 2; i++) {
			NpcFaction faction = activeNpcFaction[i];
			if (faction == null || !faction.isActive()) {
				continue;
			}
			NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(faction.getId());
			if (npcFactionTemplate.getMaxLevel() < owner.getLevel()) {
				faction.setActive(false);
				activeNpcFaction[i] = null;
				if (faction.getState() == ENpcFactionQuestState.START) {
					owner.getController().updateZone();
					owner.getController().updateNearbyQuests();
					QuestService.abandonQuest(owner, faction.getQuestId());
				}
				PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_FACTION_LEAVE_BY_LEVEL_LIMIT(npcFactionTemplate.getNameId()));
				faction.setState(ENpcFactionQuestState.NOTING);
			}
		}
	}
	
	private int getNextTime() {
		Calendar repeatDate = Calendar.getInstance();
		repeatDate.set(Calendar.AM_PM, Calendar.AM);
		repeatDate.set(Calendar.HOUR, 9);
		repeatDate.set(Calendar.MINUTE, 0);
		repeatDate.set(Calendar.SECOND, 0);
		if (repeatDate.getTime().getTime() < System.currentTimeMillis()) {
			repeatDate.add(Calendar.HOUR, 24);
		}
		return (int) (repeatDate.getTimeInMillis() / 1000);
	}
	
	public boolean canStartQuest(QuestTemplate template) {
		int type = template.isMentor() ? 1 : 0;
		NpcFaction faction = activeNpcFaction[type];
		if (faction != null && this.timeLimit[type] < System.currentTimeMillis() / 1000) {
			return true;
		}
		return false;
	}
}