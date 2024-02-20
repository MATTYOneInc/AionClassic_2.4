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
package quest.morheim;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _2034The_Hand_Behind_The_Ice_Claw extends QuestHandler
{
	private final static int questId = 2034;
	private final static int[] npcs = {204303, 204332, 204301};
	private final static int[] DF2_NPC_DireBeastD_Q2034 = {204417};
	private final static int[] ArtifactGuardianQ_35_Ae = {212877};
	
	public _2034The_Hand_Behind_The_Ice_Claw() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF2_NPC_DireBeastD_Q2034) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: ArtifactGuardianQ_35_Ae) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
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
			if (targetId == 204303) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 5, 5, true, false);
					}
				}
			} if (targetId == 204332) {
				long flint = player.getInventory().getItemCountByItemId(182204008);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							if (flint == 0) {
								return sendQuestDialog(env, 1694);
							} else {
								return sendQuestDialog(env, 1693);
							}
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							playQuestMovie(env, 76);
							giveQuestItem(env, 182204008, 1);
							changeQuestStep(env, 1, 2, false);
							return closeDialogWindow(env);
						} else if (var == 2) {
							giveQuestItem(env, 182204008, 1);
							return closeDialogWindow(env);
						}
					} case STEP_TO_4: {
						playQuestMovie(env, 77);
						changeQuestStep(env, 3, 4, false);
						player.getTitleList().addTitle(58, true, 0);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204301) {
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
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case 204417: {
					return defaultOnKillEvent(env, 204417, 2, 3);
				} case 212877: {
					return defaultOnKillEvent(env, 212877, 4, 5);
				}
			}
		}
		return false;
	}
}