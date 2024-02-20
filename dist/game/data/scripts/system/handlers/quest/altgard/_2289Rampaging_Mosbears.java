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
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2289Rampaging_Mosbears extends QuestHandler
{
	private final static int questId = 2289;
	
	public _2289Rampaging_Mosbears() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203616).addOnQuestStart(questId);
		qe.registerQuestNpc(203616).addOnTalkEvent(questId);
		qe.registerQuestNpc(203618).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("DF1A_SENSORY_AREA_Q2289_220030000"), questId);
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("DF1A_SENSORY_AREA_Q2289_220030000")) {
				if (var == 0) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203616) {
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
			if (targetId == 203616) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 1352);
						} else if (var == 7) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 62);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 7, 7, true, 5, 2120);
					} case FINISH_DIALOG: {
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 203618) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 6, 7, false);
						giveQuestItem(env, 182203017, 1);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203616) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}