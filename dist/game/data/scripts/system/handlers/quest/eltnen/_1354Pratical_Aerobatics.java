/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1354Pratical_Aerobatics extends QuestHandler
{
	private final static int questId = 1354;
	
	private String[] rings = {"ERACUS_TEMPLE_AIR_BOOSTER_1", "ERACUS_TEMPLE_AIR_BOOSTER_2",
	"ERACUS_TEMPLE_AIR_BOOSTER_3", "ERACUS_TEMPLE_AIR_BOOSTER_4", "ERACUS_TEMPLE_AIR_BOOSTER_5"};
	
	public _1354Pratical_Aerobatics() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203983).addOnQuestStart(questId);
		qe.registerQuestNpc(203983).addOnTalkEvent(questId);
		for (String ring: rings)  {
			qe.registerOnPassFlyingRings(ring, questId);
		}
		qe.registerOnQuestTimerEnd(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203983) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						playQuestMovie(env, 41);
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203983) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 2, false);
						QuestService.questTimerStart(env, 300);
						PacketSendUtility.sendWhiteMessage(player, "Cross the rings located in the pillars as quickly as possible!!!");
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						qs.setQuestVar(8);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						QuestService.questTimerEnd(env);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203983) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onPassFlyingRingEvent(QuestEnv env, String flyingRing) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (rings[0].equals(flyingRing)) {
				changeQuestStep(env, 2, 3, false);
				return true;
			} else if (rings[1].equals(flyingRing)) {
				changeQuestStep(env, 3, 4, false);
				return true;
			} else if (rings[2].equals(flyingRing)) {
				changeQuestStep(env, 4, 5, false);
				return true;
			} else if (rings[3].equals(flyingRing)) {
				changeQuestStep(env, 5, 6, false);
				return true;
			} else if (rings[4].equals(flyingRing)) {
				QuestService.questTimerEnd(env);
				changeQuestStep(env, 6, 8, false);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 2 && var <= 6) {
				QuestService.abandonQuest(player, questId);
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
				return true;
			}
		}
		return false;
	}
}