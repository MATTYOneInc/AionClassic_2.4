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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _11227Easy_As_4_3_2_1 extends QuestHandler
{
	private final static int questId = 11227;
	private final static int[] LF4_B2_Bookie_NamedQ1_54_An = {217071};
	private final static int[] LF4_B2_Bookie_NamedQ2_54_An = {217070};
	private final static int[] LF4_B2_Bookie_NamedQ3_53_An = {217069};
	private final static int[] LF4_B2_Bookie_NamedQ_WindBox_53_An = {217068};
	
	public _11227Easy_As_4_3_2_1() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: LF4_B2_Bookie_NamedQ1_54_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B2_Bookie_NamedQ2_54_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B2_Bookie_NamedQ3_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: LF4_B2_Bookie_NamedQ_WindBox_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(799076).addOnQuestStart(questId);
		qe.registerQuestNpc(799076).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799076) {
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
		} else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799076) {
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
			int var = qs.getQuestVarById(0);
			if (var == 0) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 217071:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ1_54_An, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(1);
						        qs.setStatus(QuestStatus.REWARD);
						        updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ1_54_An, 0, 1, 1);
							}
						}
					break;
					case 217070:
						if (var2 < 0) {
							return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ2_54_An, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ2_54_An, 0, 1, 2);
							}
						}
					break;
					case 217069:
						if (var3 < 0) {
							return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ3_53_An, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1 && var4 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ3_53_An, 0, 1, 3);
							}
						}
					break;
					case 217068:
						if (var4 < 0) {
							return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ_WindBox_53_An, 0, 0, 4);
						} else if (var4 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1) {
								qs.setQuestVar(1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B2_Bookie_NamedQ_WindBox_53_An, 0, 1, 4);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}