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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/****/
/** Author Rinzler (Encom)
/****/

public class _1640Teleporter_Repair extends QuestHandler
{
	private final static int questId = 1640;
	
	public _1640Teleporter_Repair() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(730033).addOnQuestStart(questId);
		qe.registerQuestNpc(730033).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 730033) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case STEP_TO_1: {
						QuestService.startQuest(env);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 730033) {
                switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						if (var == 0 && player.getInventory().getItemCountByItemId(182201790) >= 1) {
							qs.setStatus(QuestStatus.REWARD);
							QuestService.finishQuest(env);
							removeQuestItem(env, 182201790, 1);
							TeleportService2.teleportTo(env.getPlayer(), 210040000, 193.0000f, 2719.0000f, 141.0000f, (byte) 0);
							return closeDialogWindow(env);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
            }
		}
		return false;
	}
}