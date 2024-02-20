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
package quest.daevanion;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2990Making_The_Daevanion_Weapon extends QuestHandler
{
	private int A = 0;
	private int B = 0;
	private int C = 0;
	private int ALL = 0;
	private final static int questId = 2990;
	private final static int[] npcs = {204146};
	private final static int[] Ab1_1251_Elementalwater3_30_An = {256617};
	private final static int[] Ab1_1141_FungyB_29_An = {253720, 253721};
	private final static int[] Ab1_1211_Maidengolem_30_An = {254513, 254514};
	
	public _2990Making_The_Daevanion_Weapon() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204146).addOnQuestStart(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: Ab1_1251_Elementalwater3_30_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Ab1_1141_FungyB_29_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Ab1_1211_Maidengolem_30_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	private boolean isDaevanionEquipped(Player player) {
		int cloth = player.getEquipment().itemSetPartsEquipped(10);
		int leather = player.getEquipment().itemSetPartsEquipped(11);
		int chain = player.getEquipment().itemSetPartsEquipped(12);
		int plate = player.getEquipment().itemSetPartsEquipped(13);
		if (cloth == 5 || leather == 5 || chain == 5 || plate == 5) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204146) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (isDaevanionEquipped(player)) {
							return sendQuestDialog(env, 4762);
						} else {
							return sendQuestDialog(env, 4848);
						}
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
			int var1 = qs.getQuestVarById(1);
			if (targetId == 204146) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2 && var1 == 60) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 0, 1, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case SELECT_ACTION_1694: {
						return sendQuestDialog(env, 1694);
					} case SELECT_ACTION_2035: {
						if (var == 3) {
							if (player.getCommonData().getDp() >= 4000) {
								player.getCommonData().setDp(0);
								return checkItemExistence(env, 3, 3, true, 186000040, 1, true, 5, 2120, 0, 0);
							}
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						qs.setQuestVar(3);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204146) {
                if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onKillEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				switch (env.getTargetId()) {
					case 256617: {
						if (A >= 0 && A < 30) {
							++A;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					} case 253720:
					case 253721: {
						if (B >= 0 && B < 30) {
							++B;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					} case 254513:
					case 254514: {
						if (C >= 0 && C < 30) {
							++C;
							ALL = C;
							ALL = ALL << 7;
							ALL += B;
							ALL = ALL << 7;
							ALL += A;
							ALL = ALL << 7;
							ALL += 2;
							qs.setQuestVar(ALL);
							updateQuestStatus(env);
						}
						break;
					}
				} if (qs.getQuestVarById(0) == 2 && A == 30 && B == 30 && C == 30) {
					qs.setQuestVarById(1, 60);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}