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
package quest.telos;

import com.aionemu.gameserver.model.gameobjects.Npc;
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

public class QUEST_Q10101 extends QuestHandler
{
	private final static int questId = 10101;
	private final static int[] npcs = {701413, 800649, 800653};
	
	public QUEST_Q10101() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEquipItem(102400002, questId); //티아마트 훈련병의 사슬검.
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getLevel() >= 2 && player.getLevel() <= 55 && (qs == null || qs.getStatus() == QuestStatus.NONE)) {
			return QuestService.startQuest(env);
		}
		return false;
	}
	
	@Override
	public boolean onEquipItemEvent(final QuestEnv env, int itemId) {
		changeQuestStep(env, 1, 2, false);
		return closeDialogWindow(env);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800653) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1353);
						} else if (var == 3) {
							return sendQuestDialog(env, 1694);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						PacketSendUtility.sendWhiteMessage(player, "Unequip and equip your weapon!!!");
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						qs.setQuestVar(4);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendWhiteMessage(player, "Please do not use the skills books now, please wait for the next mission to learn them!!!");
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 701413) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 3) {
							return closeDialogWindow(env);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800649) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182209901, 3);
					removeQuestItem(env, 182209902, 5);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
}