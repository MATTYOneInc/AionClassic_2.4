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
package quest.reshanta;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _2071Speaking_Balaur extends QuestHandler
{
	private final static int questId = 2071;
	private final static int[] npcs = {278003, 278086, 278039, 279027, 204210};
	
	public _2071Speaking_Balaur() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(253610).addOnAttackEvent(questId);
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
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 278003) {
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
            } if (targetId == 278086) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						PacketSendUtility.sendWhiteMessage(player, "Attack <Ashikar Assaulter> and reduce his HP up to 50%!!!");
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 278039) {
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
			} if (targetId == 279027) {
				switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        } else if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_5: {
                        changeQuestStep(env, 4, 5, false);
						TeleportService2.teleportTo(env.getPlayer(), 120010000, 1368.0000f, 1034.0000f, 206.0000f, (byte) 90);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						playQuestMovie(env, 293);
						removeQuestItem(env, 182205501, 1);
                        return defaultCloseDialog(env, 6, 6, true, false);
					}
				}
			} if (targetId == 204210) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						giveQuestItem(env, 182205501, 1);
						changeQuestStep(env, 5, 6, false);
						TeleportService2.teleportTo(env.getPlayer(), 400010000, 871.0000f, 3099.0000f, 1642.0000f, (byte) 38);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 278003) {
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
	public boolean onAttackEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 2) {
				int targetId = env.getTargetId();
				if (env.getVisibleObject() instanceof Npc) {
					targetId = ((Npc) env.getVisibleObject()).getNpcId();
				} if (targetId == 253610) {
					Npc npc = (Npc) env.getVisibleObject();
					if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
						playQuestMovie(env, 289);
						npc.getController().onDelete();
						npc.getController().scheduleRespawn();
						changeQuestStep(env, 2, 3, false);
					}
				}
			}
		}
		return false;
	}
}