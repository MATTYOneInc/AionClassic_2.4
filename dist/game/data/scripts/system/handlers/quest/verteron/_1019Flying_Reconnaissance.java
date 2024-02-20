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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1019Flying_Reconnaissance extends QuestHandler
{
	private final static int questId = 1019;
	private final static int[] npcs = {203146, 203098, 203147, 700037};
	private final static int[] TKrallShamanNamed_19_Ae = {210697};
	private final static int[] LF1A_TKrallShamanNmd_19_An = {216891};
	
	public _1019Flying_Reconnaissance() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182200505, questId);
		qe.registerQuestItem(182200023, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(210158).addOnAttackEvent(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: TKrallShamanNamed_19_Ae) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF1A_TKrallShamanNmd_19_An) {
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
			if (targetId == 203146) {
			    switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182200505, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203098) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203147) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 1438);
						} else if (var == 5) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						PacketSendUtility.sendWhiteMessage(player, "Attack <Tursin Loudmouth Boss> and reduce his HP up to 50%!!!");
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						giveQuestItem(env, 182200023, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700037) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var >= 6 && var < 9) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							return useQuestObject(env, var, var + 1, false, 0, 0, 0, 0, 0, 0, false);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2034);
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
			if (var == 4) {
				int targetId = env.getTargetId();
				if (env.getVisibleObject() instanceof Npc) {
					targetId = ((Npc) env.getVisibleObject()).getNpcId();
				} if (targetId == 210158) {
					Npc npc = (Npc) env.getVisibleObject();
					if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
						qs.setQuestVar(5);
						updateQuestStatus(env);
						playQuestMovie(env, 13);
					}
				}
			}
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182200505) {
            if (var == 1 && player.isInsideZone(ZoneName.get("TURSIN_OUTPOST_210030000"))) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				playQuestMovie(env, 18);
				removeQuestItem(env, 182200505, 1);
				return HandlerResult.SUCCESS;
            }
        } else if (id == 182200023) {
            if (var == 9 && player.isInsideZone(ZoneName.get("TURSIN_TOTEM_POLE_210030000"))) {
				qs.setQuestVar(10);
				updateQuestStatus(env);
				playQuestMovie(env, 174);
				removeQuestItem(env, 182200023, 1);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (var == 10) {
                int[] TKrallShamanNamed_19_Ae = {210697};
                int[] LF1A_TKrallShamanNmd_19_An = {216891};
                switch (env.getTargetId()) {
                    case 210697: {
                        return defaultOnKillEvent(env, TKrallShamanNamed_19_Ae, 10, true);
                    } case 216891: {
                        return defaultOnKillEvent(env, LF1A_TKrallShamanNmd_19_An, 10, true);
                    }
                }
            }
		}
		return false;
	}
}