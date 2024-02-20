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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _1054The_Power_Of_Elim extends QuestHandler
{
	private final static int questId = 1054;
	private final static int[] npcs = {730024, 204647, 730008, 730019};
	
	public _1054The_Power_Of_Elim() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } 
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 730024) {
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
			} if (targetId == 204647) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case SELECT_ACTION_2377: {
						playQuestMovie(env, 187);
						PacketSendUtility.sendWhiteMessage(player, "If you leave Heiron <Trajanus's Blessing> will be removed!!!");
						SkillEngine.getInstance().applyEffectDirectly(1860, player, player, 3600000); //Trajanus's Blessing.
						return sendQuestDialog(env, 2377);
					} case STEP_TO_5: {
						removeQuestItem(env, 182201606, 1);
						removeQuestItem(env, 182201607, 1);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						player.getEffectController().removeEffect(1860);
						return checkQuestItems(env, 5, 5, true, 5, 10001);
					}
				}
			} if (targetId == 730008) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182201606, 1);
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730019) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						giveQuestItem(env, 182201607, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204647) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}