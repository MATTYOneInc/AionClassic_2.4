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
package quest.telos_of_the_forgotten;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q19535 extends QuestHandler
{
	private final static int questId = 19535;
	private final static int[] IDLDF1_Lizard_As_55_Ae3 = {219370};
	private final static int[] IDLDF1_Lizard_Fi_55_Ae3 = {219369};
	private final static int[] IDLDF1_Lizard_Pr_55_Ae3 = {219372};
	private final static int[] IDLDF1_Lizard_Ra_55_Ae3 = {219371};
	
	public QUEST_Q19535() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: IDLDF1_Lizard_As_55_Ae3) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDLDF1_Lizard_Fi_55_Ae3) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDLDF1_Lizard_Pr_55_Ae3) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDLDF1_Lizard_Ra_55_Ae3) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(799381).addOnQuestStart(questId);
		qe.registerQuestNpc(799381).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SZ_START_300550000"), questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799381) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799381) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
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
			if (zoneName == ZoneName.get("IDLDF1_SZ_START_300550000")) {
				if (var == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
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
			if (var == 1) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				switch (targetId) {
					case 219370:
						if (var1 < 0) {
							return defaultOnKillEvent(env, IDLDF1_Lizard_As_55_Ae3, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Lizard_As_55_Ae3, 0, 1, 1);
							}
						}
					break;
					case 219369:
						if (var2 < 0) {
							return defaultOnKillEvent(env, IDLDF1_Lizard_Fi_55_Ae3, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Lizard_Fi_55_Ae3, 0, 1, 2);
							}
						}
					break;
					case 219372:
						if (var3 < 0) {
							return defaultOnKillEvent(env, IDLDF1_Lizard_Pr_55_Ae3, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1 && var4 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Lizard_Pr_55_Ae3, 0, 1, 3);
							}
						}
					break;
					case 219371:
						if (var4 < 0) {
							return defaultOnKillEvent(env, IDLDF1_Lizard_Ra_55_Ae3, 0, 0, 4);
						} else if (var4 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Lizard_Ra_55_Ae3, 0, 1, 4);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}