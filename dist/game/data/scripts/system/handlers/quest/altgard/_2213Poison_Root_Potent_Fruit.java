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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _2213Poison_Root_Potent_Fruit extends QuestHandler
{
	private final static int questId = 2213;
	
	public _2213Poison_Root_Potent_Fruit() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerGetingItem(182203208, questId); //Okaru Fruit.
		qe.registerQuestNpc(203604).addOnQuestStart(questId);
		qe.registerQuestNpc(203604).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			qs.setQuestVar(1);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182203208, 1); //Okaru Fruit.
			player.getEffectController().removeEffect(1851); //Okaru Poison.
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
			if (targetId == 203604) {
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
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203604) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
						removeQuestItem(env, 182203208, 1); //Okaru Fruit.
						player.getEffectController().removeEffect(1851); //Okaru Poison.
						return sendQuestEndDialog(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203604) {
                return sendQuestEndDialog(env);
            }
        }
		return false;
	}
}