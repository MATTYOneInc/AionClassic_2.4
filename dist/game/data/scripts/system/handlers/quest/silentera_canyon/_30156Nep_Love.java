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
package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.Npc;
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

public class _30156Nep_Love extends QuestHandler 
{
	private final static int questId = 30156;
	
	public _30156Nep_Love() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(799234).addOnQuestStart(questId); //Nep.
		qe.registerQuestNpc(799234).addOnTalkEvent(questId); //Nep.
		qe.registerQuestNpc(204304).addOnTalkEvent(questId); //Vili.
		qe.registerQuestNpc(799339).addOnTalkEvent(questId); //Sinigalla.
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799234) { //Nep.
                switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182209253, 1); //Medicine Of Restoration.
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799339) { //Sinigalla
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_1: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						return defaultCloseDialog(env, 0, 0, true, false);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204304) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}