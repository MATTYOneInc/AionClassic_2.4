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
package quest.abyssal_splinter;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _30365A_Ray_Of_Hope extends QuestHandler
{
    private final static int questId = 30365;
	
    public _30365A_Ray_Of_Hope() {
        super(questId);
    }
	
    @Override
    public void register() {
        qe.registerQuestItem(182209824, questId);
		qe.registerQuestNpc(204241).addOnTalkEvent(questId);
        qe.registerQuestNpc(203574).addOnTalkEvent(questId);
        qe.registerQuestNpc(278040).addOnTalkEvent(questId);
    }
	
    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, final Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (QuestService.startQuest(env)) {
                return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
            }
        }
        return HandlerResult.FAILED;
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 204241) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
            } if (targetId == 203574) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						changeQuestStep(env, 3, 3, true);
						return sendQuestDialog(env, 5);
					}
				}
            } if (targetId == 278040) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203574) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}