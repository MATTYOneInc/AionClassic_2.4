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
package quest.poeta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1003Illegal_Logging extends QuestHandler
{
	private final static int questId = 1003;
	private final static int[] npcs = {203081};
	private final static int[] DBrownieMWa_7_An = {210096, 210149, 210145, 210146, 210150, 210151, 210092, 210154, 210685};
	private final static int[] TKrallKeeper_8_An = {210160};
	
	public _1003Illegal_Logging() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: DBrownieMWa_7_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: TKrallKeeper_8_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 203081) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 13) {
							return sendQuestDialog(env, 1352);
						}
                    } case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
                        changeQuestStep(env, 13, 14, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203081) {
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
            switch (env.getTargetId()) {
                case 210096:
				case 210149:
				case 210145:
				case 210146:
				case 210150:
				case 210151:
				case 210092:
				case 210154:
				case 210685:
					if (var >= 1 && var <= 12) {
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(env);
						return true;
					}
				break;
				case 210160:
				    if (var >= 14 && var <= 15) {
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(env);
						return true;
					} else if (var == 16) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
				break;
            }
        }
        return false;
    }
}