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
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2060Restoring_Beluslan_Observatory extends QuestHandler
{
	private final static int questId = 2060;
	private final static int[] npcs = {204701, 204785, 278003, 278088, 700293};
	private final static int[] DF3_DragonObelisk_Q2060A = {700290};
	
	public _2060Restoring_Beluslan_Observatory() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204318, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF3_DragonObelisk_Q2060A) {
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
			if (targetId == 204701) {
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
			} if (targetId == 204785) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 4) {
							if (QuestService.collectItemCheck(env, false)) {
								return sendQuestDialog(env, 2375);
							} else {
								giveQuestItem(env, 182204318, 1);
								return sendQuestDialog(env, 2461);
							}
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						removeQuestItem(env, 182204319, 1);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 278003) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						return defaultCloseDialog(env, 2, 3);
					}
				}
			} if (targetId == 278088) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						playQuestMovie(env, 281);
						giveQuestItem(env, 182204318, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700293) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 8) {
							return useQuestObject(env, 8, 8, true, 0, 254);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204701) {
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
		return defaultOnKillEvent(env, 700290, 5, 8);
	}
	
	@Override
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182204318) {
            if (var == 4 && player.isInsideZone(ZoneName.get("AB1_ITEMUSEAREA_Q2060"))) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 4, 4, false, 182204319, 1, 0));
            }
        }
        return HandlerResult.FAILED;
    }
}