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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1607Mapping_The_Revolutionaries extends QuestHandler
{
	private final static int questId = 1607;
	
	public _1607Mapping_The_Revolutionaries() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182201744, questId);
		qe.registerQuestNpc(204578).addOnTalkEvent(questId);
		qe.registerQuestNpc(204574).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1607_A_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1607_B_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1607_C_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF3_SENSORY_AREA_Q1607_D_210040000"), questId);
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (targetId == 0) {
			switch (env.getDialog()) {
				case ASK_ACCEPTION: {
					return sendQuestDialog(env, 4);
				} case ACCEPT_QUEST: {
					return sendQuestStartDialog(env);
				} case REFUSE_QUEST: {
				    return closeDialogWindow(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			int var4 = qs.getQuestVarById(4);
			if (targetId == 204578) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 204574) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1 && var1 == 1 && var2 == 1 && var3 == 1 && var4 == 1) {
							return sendQuestDialog(env, 10002);
						}
					} case SELECT_REWARD: {
						changeQuestStep(env, 1, 1, true);
						return sendQuestDialog(env, 5);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204574) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			int var4 = qs.getQuestVarById(4);
			if (var == 1) {
				if (zoneName == ZoneName.get("LF3_SENSORY_AREA_Q1607_A_210040000")) {
					if (var1 == 0) {
						changeQuestStep(env, 0, 1, false, 1);
						return true;
					}
				} else if (zoneName == ZoneName.get("LF3_SENSORY_AREA_Q1607_B_210040000")) {
					if (var2 == 0) {
						changeQuestStep(env, 0, 1, false, 2);
						return true;
					}
				} else if (zoneName == ZoneName.get("LF3_SENSORY_AREA_Q1607_C_210040000")) {
					if (var3 == 0) {
						changeQuestStep(env, 0, 1, false, 3);
						return true;
					}
				} else if (zoneName == ZoneName.get("LF3_SENSORY_AREA_Q1607_D_210040000")) {
					if (var4 == 0) {
						changeQuestStep(env, 0, 1, false, 4);
						return true;
					}
				}
			}
		}
		return false;
	}
}