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
package quest.craft;

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

public class _19020Master_Tailors_Potential extends QuestHandler
{
	private final static int questId = 19020;
	
	public _19020Master_Tailors_Potential() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnFailCraft(182206766, questId);
		qe.registerQuestNpc(203793).addOnQuestStart(questId);
        qe.registerQuestNpc(203793).addOnTalkEvent(questId);
        qe.registerQuestNpc(203794).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203793) {
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
			long kinah = player.getInventory().getKinah();
			if (targetId == 203794) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_10: {
						if (kinah >= 84000) {
							giveQuestItem(env, 152201962, 1);
							changeQuestStep(env, 0, 1, false);
							player.getInventory().decreaseKinah(84000);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 4400);
						}
					} case STEP_TO_20: {
						if (kinah >= 111500) {
							giveQuestItem(env, 152201963, 1);
							changeQuestStep(env, 0, 1, false);
							player.getInventory().decreaseKinah(111500);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 4400);
						}
					}
                }
            } if (targetId == 203793) {
				long eremitiaNobleSash = player.getInventory().getItemCountByItemId(182206766);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (eremitiaNobleSash >= 1 && var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (eremitiaNobleSash == 0 && var == 1) {
							changeQuestStep(env, 1, 0, false);
							return sendQuestDialog(env, 3398);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 2716);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203793) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onFailCraftEvent(QuestEnv env, int itemId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (itemId) {
				case 182206766: {
					changeQuestStep(env, 1, 0, false);
					return true;
				}
			}
		}
		return false;
	}
}