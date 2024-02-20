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
package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _2925A_Heartfelt_Confession extends QuestHandler
{
	private final static int questId = 2925;
	
	public _2925A_Heartfelt_Confession() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(204261).addOnQuestStart(questId);
		qe.registerQuestNpc(204261).addOnTalkEvent(questId);
		qe.registerQuestNpc(204235).addOnTalkEvent(questId);
		qe.registerQuestNpc(204127).addOnTalkEvent(questId);
		qe.registerQuestNpc(204193).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204261) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						PacketSendUtility.sendWhiteMessage(player, "Buy the Eiren cloth to <Lark> in pandaemonium & equip them !!!");
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204235) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							if (!player.getEquipment().getEquippedItemsByItemId(110100288).isEmpty() && //Eiren Blouse.
							    !player.getEquipment().getEquippedItemsByItemId(111100264).isEmpty() && //Eiren Gloves.
								!player.getEquipment().getEquippedItemsByItemId(112100243).isEmpty() && //Eiren Pauldrons Ornament.
								!player.getEquipment().getEquippedItemsByItemId(114100281).isEmpty() && //Eiren Shoes.
								!player.getEquipment().getEquippedItemsByItemId(113100263).isEmpty()) { //Eiren Skirt.
								return sendQuestDialog(env, 1011);
							} else {
								return sendQuestDialog(env, 1097);
							}
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204261) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 5) {
							qs.setQuestVar(5);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						removeQuestItem(env, 110100288, 1);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204127) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204193) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204261) {
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