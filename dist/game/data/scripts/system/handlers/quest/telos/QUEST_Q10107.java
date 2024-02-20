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

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.ClassChangeService;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q10107 extends QuestHandler
{
	private final static int questId = 10107;
	private final static int[] npcs = {800709, 800713};
	private final static int[] LDF1_BrohumM = {219288, 219289, 219290, 219291, 219292};
	private final static int[] LDF1_BrohumF = {219293, 219294, 219295, 219296, 219297};
	private final static int[] LDF1_Brohum_M_Young = {219298, 219299, 219300, 219301};
	private final static int[] LDF1_Brohum_F_Young = {219303, 219304, 219305};
	
	public QUEST_Q10107() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: LDF1_BrohumM) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF1_BrohumF) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF1_Brohum_M_Young) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LDF1_Brohum_F_Young) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(702, questId);
		qe.registerOnMovieEndQuest(705, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getLevel() >= 15 && player.getLevel() <= 55 && (qs == null || qs.getStatus() == QuestStatus.NONE)) {
			return QuestService.startQuest(env);
		}
		return false;
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800709) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 800713) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (var == 3) {
							switch (player.getGender()) {
								case MALE:
									playQuestMovie(env, 702);
								break;
								case FEMALE:
									playQuestMovie(env, 705);
								break;
							}
						} else if (var == 4) {
							qs.setQuestVar(4);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							player.getCommonData().setLevel(16);
							return sendQuestDialog(env, 5);
						}
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800713) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		if (movieId == 702) {
			qs.setQuestVar(4);
			updateQuestStatus(env);
			return setPlayerClass(env, qs, PlayerClass.THUNDERER);
		} else if (movieId == 705) {
			qs.setQuestVar(4);
			updateQuestStatus(env);
			return setPlayerClass(env, qs, PlayerClass.THUNDERER);
		}
		return false;
	}
	
	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
		final Player player = env.getPlayer();
		ClassChangeService.setClass(player, playerClass);
		player.getController().upgradePlayer();
		return true;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 219288:
					case 219289:
					case 219290:
					case 219291:
					case 219292:
						if (var1 < 4) {
							return defaultOnKillEvent(env, LDF1_BrohumM, 0, 4, 1);
						} else if (var1 == 4) {
							if (var2 == 5 && var3 == 5 && var4 == 5) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF1_BrohumM, 4, 5, 1);
							}
						}
					break;
					case 219293:
					case 219294:
					case 219295:
					case 219296:
					case 219297:
						if (var2 < 4) {
							return defaultOnKillEvent(env, LDF1_BrohumF, 0, 4, 2);
						} else if (var2 == 4) {
							if (var1 == 5 && var3 == 5 && var4 == 5) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF1_BrohumF, 4, 5, 2);
							}
						}
					break;
					case 219298:
					case 219299:
					case 219300:
					case 219301:
						if (var3 < 4) {
							return defaultOnKillEvent(env, LDF1_Brohum_M_Young, 0, 4, 3);
						} else if (var3 == 4) {
							if (var1 == 5 && var2 == 5 && var4 == 5) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF1_Brohum_M_Young, 4, 5, 3);
							}
						}
					break;
					case 219303:
					case 219304:
					case 219305:
						if (var4 < 4) {
							return defaultOnKillEvent(env, LDF1_Brohum_F_Young, 0, 4, 4);
						} else if (var4 == 4) {
							if (var1 == 5 && var2 == 5 && var3 == 5) {
								qs.setQuestVar(2);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LDF1_Brohum_F_Young, 4, 5, 4);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}