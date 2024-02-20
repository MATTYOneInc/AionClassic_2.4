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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1146Delicate_Mandrake extends QuestHandler
{
	private final static int questId = 1146;
	
	public _1146Delicate_Mandrake() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestNpc(203123).addOnQuestStart(questId);
		qe.registerQuestNpc(203123).addOnTalkEvent(questId);
		qe.registerQuestNpc(203139).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203123) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						QuestService.questTimerStart(env, 120);
						return sendQuestStartDialog(env, 182200519, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203139) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 0) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_REWARD: {
						qs.setQuestVar(0);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						QuestService.questTimerEnd(env);
						removeQuestItem(env, 182200519, 1);
						return sendQuestDialog(env, 5);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203139) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182200519, 1);
			QuestService.abandonQuest(player, questId);
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182200519, 1);
			QuestService.abandonQuest(player, questId);
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
}