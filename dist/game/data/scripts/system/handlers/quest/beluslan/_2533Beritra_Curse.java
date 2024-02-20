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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2533Beritra_Curse extends QuestHandler
{
	private final static int questId = 2533;
	
	public _2533Beritra_Curse() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestItem(182204425, questId);
		qe.registerQuestNpc(204801).addOnQuestStart(questId);
		qe.registerQuestNpc(204801).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204801) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182204425, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204801) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							QuestService.questTimerEnd(env);
							return sendQuestDialog(env, 1352);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204801) {
				if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.isInsideZone(ZoneName.get("BERITRAS_WEAPON_220040000"))) {
				QuestService.questTimerStart(env, 300);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false, 182204426, 1, 0));
			}
		}
		return HandlerResult.SUCCESS;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182204426, 1);
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
			removeQuestItem(env, 182204426, 1);
			QuestService.abandonQuest(player, questId);
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
}