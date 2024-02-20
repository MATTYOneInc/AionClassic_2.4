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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Npc;
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

public class _11012Practical_Nursing extends QuestHandler
{
	private final static int questId = 11012;
	
	public _11012Practical_Nursing() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(799071).addOnQuestStart(questId);
		qe.registerQuestNpc(799071).addOnTalkEvent(questId);
		qe.registerQuestNpc(799072).addOnTalkEvent(questId);
		qe.registerQuestNpc(799073).addOnTalkEvent(questId);
		qe.registerQuestNpc(799074).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799071) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182206715, 3);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799072) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						changeQuestStep(env, 0, 1, false);
						removeQuestItem(env, 182206715, 1);
						QuestService.addNewSpawn(210050000, 1, 703515, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799073) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						changeQuestStep(env, 1, 2, false);
						removeQuestItem(env, 182206715, 1);
						QuestService.addNewSpawn(210050000, 1, 703516, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799074) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						qs.setQuestVar(2);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						removeQuestItem(env, 182206715, 1);
						QuestService.addNewSpawn(210050000, 1, 703517, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799071) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}