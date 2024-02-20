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
package quest.dark_poeta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _4502Essence_Of_Fate extends QuestHandler
{
	private final static int questId = 4502;
	private final static int[] mobs = {214894, 214895, 214896, 214897, 214904};
	
	public _4502Essence_Of_Fate() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204837).addOnQuestStart(questId);
		qe.registerQuestNpc(204837).addOnTalkEvent(questId);
		qe.registerQuestNpc(204182).addOnTalkEvent(questId);
		for (int mob: mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204837) {
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
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204182) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 2, 2, true, 5, 10001);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204182) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			switch (targetId) {
				case 214894: //Telepathy Controller.
					if (var == 1) {
						return defaultOnKillEvent(env, 214894, 1, 2, 0);
					}
				break;
				case 214895: //Main Power Generator.
					if (var == 2 && var1 != 1) {
						defaultOnKillEvent(env, 214895, 0, 1, 1);
						if (var2 == 1 && var3 == 1) {
							return true;
						}
						return true;
					}
				break;
				case 214896: //Auxiliary Power Generator.
					if (var == 2 && var2 != 1) {
						defaultOnKillEvent(env, 214896, 0, 1, 2);
						if (var1 == 1 && var3 == 1) {
							return true;
						}
						return true;
					}
				break;
				case 214897: //Emergency Generator.
					if (var == 2 && var3 != 1) {
						defaultOnKillEvent(env, 214897, 0, 1, 3);
						if (var1 == 1 && var2 == 1) {
							return true;
						}
						return true;
					}
				break;
				case 214904: //Brigade General Anuhart.
					if (var == 2 && var1 == 1 && var2 == 1 && var3 == 1) {
						return defaultOnKillEvent(env, 214904, 2, false);
					}
				break;
			}
		}
		return false;
	}
}