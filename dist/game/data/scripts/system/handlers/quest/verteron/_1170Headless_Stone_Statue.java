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
package quest.verteron;

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

public class _1170Headless_Stone_Statue extends QuestHandler
{
	private final static int questId = 1170;
	
	public _1170Headless_Stone_Statue() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnMovieEndQuest(16, questId);
		qe.registerQuestNpc(730000).addOnQuestStart(questId);
		qe.registerQuestNpc(730000).addOnTalkEvent(questId);
		qe.registerQuestNpc(700033).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 730000) {
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
			if (targetId == 700033) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						giveQuestItem(env, 182200504, 1);
						return useQuestObject(env, 0, 0, true, true);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 730000) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 1352);
					} case STEP_TO_1: {
						playQuestMovie(env, 16);
						return closeDialogWindow(env);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (movieId == 16) {
				QuestService.finishQuest(env);
				giveQuestItem(env, 186000001, 1);
				removeQuestItem(env, 182200504, 1);
				return true;
			}
		}
		return false;
	}
}