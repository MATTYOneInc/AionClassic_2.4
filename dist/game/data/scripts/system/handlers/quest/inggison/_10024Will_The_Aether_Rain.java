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

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _10024Will_The_Aether_Rain extends QuestHandler
{
	private final static int questId = 10024;
	private final static int[] npcs = {799020, 700605, 798970, 798979, 203793, 216498};
	
	public _10024Will_The_Aether_Rain() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestItem(182206620, questId);
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
		return defaultOnLvlUpEvent(env, 10022, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798970) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						playQuestMovie(env, 505);
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798979) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700605) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203793) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182206620, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 5, 6, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 6) {
							defaultCloseDialog(env, 6, 6);
						} else if (var == 5) {
							defaultCloseDialog(env, 5, 5);
						}
					}
				}
			} if (targetId == 216498) { //Warden Angasa.
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_10: {
						changeQuestStep(env, 9, 10, false);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getSpawn().setWalkerId("lf4_c5_qpath");
						WalkManager.startWalking((NpcAI2) npc.getAi2());
						npc.setState(1);
						PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								npc.getController().onDelete();
								npc.getSpawn().setWalkerId(null);
								npc.getController().scheduleRespawn();
								WalkManager.stopWalking((NpcAI2) npc.getAi2());
							}
						}, 8000);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 1608);
						}
					} case SET_REWARD: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						return defaultCloseDialog(env, 10, 10, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798970) {
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
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			player.getController().updateZone();
			player.getEffectController().removeEffect(2252); //Drakan Transformation.
			return true;
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 8 && player.isInsideZone(ZoneName.get("LF4_ITEMUSEAREA_Q10024A"))) {
				QuestService.questTimerStart(env, 60);
				QuestService.addNewSpawn(210050000, player.getInstanceId(), 216498, 2368.0000f, 2035.0000f, 250.0000f, (byte) 33); //Warden Angasa.
                return HandlerResult.fromBoolean(useQuestItem(env, item, 8, 9, false));
            }
        }
        return HandlerResult.FAILED;
    }
}