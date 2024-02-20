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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _1036Kaidan_Prisoner extends QuestHandler
{
	private final static int questId = 1036;
	private final static int[] npcs = {203904, 204045, 204003, 204004, 204020, 203901};
	
	public _1036Kaidan_Prisoner() {
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
			if (targetId == 203904) {
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
			} if (targetId == 204045) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1354: {
						return playQuestMovie(env, 32);
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						TeleportService2.teleportTo(env.getPlayer(), 210020000, 1461.0000f, 2488.0000f, 344.0000f, (byte) 58);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204003) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3 && QuestService.collectItemCheck(env, true)) {
							return sendQuestDialog(env, 2034);
						} else {
							return sendQuestDialog(env, 2120);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						playQuestMovie(env, 50);
						changeQuestStep(env, 3, 4, false);
						Npc npc = (Npc) env.getVisibleObject();
						///You must meet a Krall called Gardugu.
						NpcShoutsService.getInstance().sendMsg(npc, 1100640, npc.getObjectId(), 0, 5000);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204004) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						giveQuestItem(env, 182201004, 1);
						changeQuestStep(env, 4, 5, false);
						TeleportService2.teleportTo(env.getPlayer(), 210020000, 1605.0000f, 1528.0000f, 318.0000f, (byte) 119);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						qs.setQuestVar(6);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						giveQuestItem(env, 182201005, 1);
						removeQuestItem(env, 182201004, 1);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203901) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3057);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182201005, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}