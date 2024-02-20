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
package quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _3090In_Search_Of_Pippi_The_Porgus extends QuestHandler
{
	private final static int questId = 3090;
	
	public _3090In_Search_Of_Pippi_The_Porgus() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(798182).addOnQuestStart(questId);
		qe.registerQuestNpc(798182).addOnTalkEvent(questId);
		qe.registerQuestNpc(798193).addOnTalkEvent(questId);
		qe.registerQuestNpc(700420).addOnTalkEvent(questId);
		qe.registerQuestNpc(700421).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF2A_SENSORY_AREA_Q3090_210060000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798182) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798193) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						giveQuestItem(env, 182208050, 1);
						changeQuestStep(env, 2, 3, false);
						player.getInventory().decreaseKinah(10000);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 700420) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 1) {
                            qs.setQuestVar(2);
							updateQuestStatus(env);
							///I found Pippi's red ribbon!
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111007, player.getObjectId(), 2));
							return true;
                        }
					}
                }
            } if (targetId == 700421) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case SET_REWARD: {
						giveQuestItem(env, 182208051, 1);
						removeQuestItem(env, 182208050, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						qs.setQuestVar(3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798182) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182208051, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
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
			if (var == 1) {
				if (zoneName == ZoneName.get("LF2A_SENSORY_AREA_Q3090_210060000")) {
					changeQuestStep(env, 0, 1, false, 1);
					///These must be Pippi's footprints!
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111006, player.getObjectId(), 2));
					return true;
				}
			}
		}
		return false;
	}
}