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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;

/****/
/** Author Rinzler (Encom)
/****/

public class _1423Expert_Advice extends QuestHandler
{
	private final static int questId = 1423;

	public _1423Expert_Advice() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203983).addOnQuestStart(questId);
		qe.registerQuestNpc(203983).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203983) {
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
			if (targetId == 203983) {
                switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					} case STEP_TO_1: {
						SkillEngine.getInstance().getSkill(player, 10169, 60, player).useSkill(); //Increase Flight Speed.
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
		    if (targetId == 203983) {
			    return sendQuestEndDialog(env);
		    }
		}
		return false;
	}
}