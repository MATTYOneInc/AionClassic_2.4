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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.Npc;
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

public class _2018Reconstructing_Impetusium extends QuestHandler
{
	private final static int questId = 2018;
	private final static int[] npcs = {203649, 700098};
	private final static int[] UndeadD_18_An = {210588, 210722};
	private final static int[] LycanFighterSQ_18_An = {210752};
	
	public _2018Reconstructing_Impetusium() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: UndeadD_18_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LycanFighterSQ_18_An) {
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
			if (targetId == 203649) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestDialog(env, 1352);
						} else if (var == 7) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						changeQuestStep(env, 7, 7, true);
						return sendQuestDialog(env, 5);
					}
				}
			} if (targetId == 700098) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							return sendQuestDialog(env, 2034);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, false)) {
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							npc.getController().scheduleRespawn();
							QuestService.addNewSpawn(220030000, 1, 210752, player.getX() + 3, player.getY(), player.getZ(), (byte) 0);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 2120);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203649) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (var >= 1 && var < 4) {
				int[] UndeadD_18_An = {210588, 210722};
				return defaultOnKillEvent(env, UndeadD_18_An, var, var + 1);
			} else if (var == 5) {
				if (env.getTargetId() == 210752) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}