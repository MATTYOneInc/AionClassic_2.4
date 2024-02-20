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
package quest.kromedes_trial;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _18602Nightmare_In_Shining_Armor extends QuestHandler
{
	private final static int questId = 18602;
	protected final int CROMEDE_BUFF5_NR = 19288;
	private final static int[] IDCromede_2up_Named_Angry_Judge_38_An = {217005, 217006};
	
	public _18602Nightmare_In_Shining_Armor() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(205229).addOnTalkEvent(questId);
		qe.registerQuestNpc(700939).addOnTalkEvent(questId);
		for (int mob: IDCromede_2up_Named_Angry_Judge_38_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnEnterZone(ZoneName.get("GRAND_CAVERN_300230000"), questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 18601, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 205229) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						WorldMapInstance kromedeTrial = InstanceService.getNextAvailableInstance(300230000);
						InstanceService.registerPlayerWithInstance(kromedeTrial, player);
						TeleportService2.teleportTo(player, 300230000, kromedeTrial.getInstanceId(), 244.0000f, 244.0000f, 189.0000f, (byte) 30);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 700939) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
					} case STEP_TO_3: {
                        ///Oh, Robstin.... I'll avenge you with blood!
						SkillEngine.getInstance().applyEffectDirectly(CROMEDE_BUFF5_NR, player, player, 3600000);
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111307, player.getObjectId(), 2));
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205229) {
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
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("GRAND_CAVERN_300230000")) {
				if (var == 1) {
					playQuestMovie(env, 453);
					return true;
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
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (var == 3) {
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, IDCromede_2up_Named_Angry_Judge_38_An, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(4);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 1 && var < 4) {
                qs.setQuestVar(0);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}