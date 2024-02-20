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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2057Glaciont_The_Hardy extends QuestHandler
{
	private final static int questId = 2057;
	private final static int[] npcs = {204787, 204784};
	private final static int[] CyclopsReStatueDQ_43_An_1 = {213730};
	private final static int[] CyclopsReStatueDQ_43_An_2 = {213788};
	private final static int[] CyclopsReStatueDQ_43_An_3 = {213789};
	private final static int[] CyclopsReStatueDQ_43_An_4 = {213790};
	private final static int[] CyclopsReStatueDQ_43_An_5 = {213791};
	
	public _2057Glaciont_The_Hardy() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204316, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: CyclopsReStatueDQ_43_An_1) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: CyclopsReStatueDQ_43_An_2) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: CyclopsReStatueDQ_43_An_3) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: CyclopsReStatueDQ_43_An_4) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: CyclopsReStatueDQ_43_An_5) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2056, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204787) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1013: {
						playQuestMovie(env, 246);
						return sendQuestDialog(env, 1013);
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204784) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 247);
						giveQuestItem(env, 182204316, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204787) {
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
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182204316) {
            if (var == 2 && player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2057"))) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false, 248));
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				int var5 = qs.getQuestVarById(5);
				switch (env.getTargetId()) {
					case 213730:
						if (var1 < 0) {
							return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_1, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 1 && var3 == 1 && var4 == 1 && var5 == 1) {
								qs.setQuestVarById(0, 1090785347);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_1, 0, 1, 1);
							}
						}
					break;
					case 213788:
						if (var2 < 0) {
							return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_2, 0, 0, 2);
						} else if (var2 == 0) {
							if (var1 == 1 && var3 == 1 && var4 == 1 && var5 == 1) {
								qs.setQuestVarById(0, 1090785347);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_2, 0, 1, 2);
							}
						}
					break;
					case 213789:
						if (var3 < 0) {
							return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_3, 0, 0, 3);
						} else if (var3 == 0) {
							if (var1 == 1 && var2 == 1 && var4 == 1 && var5 == 1) {
								qs.setQuestVarById(0, 1090785347);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_3, 0, 1, 3);
							}
						}
					break;
					case 213790:
						if (var4 < 0) {
							return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_4, 0, 0, 4);
						} else if (var4 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1 && var5 == 1) {
								qs.setQuestVarById(0, 1090785347);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_4, 0, 1, 4);
							}
						}
					break;
					case 213791:
						if (var5 < 0) {
							return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_5, 0, 0, 5);
						} else if (var5 == 0) {
							if (var1 == 1 && var2 == 1 && var3 == 1 && var4 == 1) {
								qs.setQuestVarById(0, 1090785347);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, CyclopsReStatueDQ_43_An_5, 0, 1, 5);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}