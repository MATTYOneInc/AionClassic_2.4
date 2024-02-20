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
package quest.gelkmaros;

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

public class _21080Message_In_A_Windstream extends QuestHandler
{
	private final static int questId = 21080;
	
	public _21080Message_In_A_Windstream() {
		super(questId);
	}
	
	public void register() {
		qe.registerOnMovieEndQuest(563, questId);
		qe.registerQuestNpc(799231).addOnQuestStart(questId);
		qe.registerQuestNpc(799231).addOnTalkEvent(questId);
		qe.registerQuestNpc(799427).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ANTAGOR_CANYON_220070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("GELKMAROS_FORTRESS_220070000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799231) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						playQuestMovie(env, 562);
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182207939, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799427) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						playQuestMovie(env, 563);
						removeQuestItem(env, 182207939, 1);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799231) {
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 563) {
			qs.setQuestVar(4);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("ANTAGOR_CANYON_220070000")) {
				if (var < 3) {
					changeQuestStep(env, var, var + 1, false);
					return true;
				}
			} else if (zoneName == ZoneName.get("GELKMAROS_FORTRESS_220070000")) {
				if (var == 4) {
					changeQuestStep(env, 4, 4, true); 
					return true;
				}
			}
		}
		return false;
	}
}