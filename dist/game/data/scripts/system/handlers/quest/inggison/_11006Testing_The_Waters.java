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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _11006Testing_The_Waters extends QuestHandler
{
	private final static int questId = 11006;
	
	public _11006Testing_The_Waters() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestItem(182206704, questId);
		qe.registerQuestItem(182206705, questId);
		qe.registerQuestNpc(798940).addOnQuestStart(questId);
		qe.registerQuestNpc(798940).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798940) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182206704, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798940) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182206705, 1);
						removeQuestItem(env, 182206706, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798940) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182206707, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (var == 0 && player.isInsideZone(ZoneName.get("OBELISK_VALLEY_210050000"))) {
				giveQuestItem(env, 182206706, 1);
				removeQuestItem(env, 182206704, 1);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false));
            } else if (var == 2 && player.isInsideZone(ZoneName.get("DIMAIA_FOUNTAINFALL_210050000"))) {
				giveQuestItem(env, 182206707, 1);
				removeQuestItem(env, 182206705, 1);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 2, true));
            }
        }
		return HandlerResult.FAILED;
    }
}