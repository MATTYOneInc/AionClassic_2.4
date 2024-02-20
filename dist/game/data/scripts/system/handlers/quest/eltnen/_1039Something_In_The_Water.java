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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1039Something_In_The_Water extends QuestHandler
{
	private final static int questId = 1039;
	private final static int[] npcs = {203946, 203705};
	private final static int[] LehparAs_33_An = {210946, 210968, 210969, 210947};
	
	public _1039Something_In_The_Water() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182201009, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LehparAs_33_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1035, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
		int var2 = qs.getQuestVarById(2);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203946) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 3) {
							return sendQuestDialog(env, 1693);
						} else if (var == 4 && var1 == 3 && var2 == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 182);
						giveQuestItem(env, 182201009, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
				}
			} if (targetId == 203705) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						removeQuestItem(env, 182201010, 1);
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203946) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("LF2_ITEMUSEAREA_Q1039"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false, 182201010, 1, 0));
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 4) {
				int[] vaegir = {210946, 210968};
				int[] fighter = {210969, 210947};
				switch (env.getTargetId()) {
					case 210946:
					case 210968: {
						return defaultOnKillEvent(env, vaegir, 0, 3, 1);
					}
					case 210969:
					case 210947: {
						return defaultOnKillEvent(env, fighter, 0, 3, 2);
					}
				}
			}
		}
		return false;
	}
}