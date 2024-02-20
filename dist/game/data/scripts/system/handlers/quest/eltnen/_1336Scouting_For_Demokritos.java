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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1336Scouting_For_Demokritos extends QuestHandler
{
	private final static int questId = 1336;
	
	public _1336Scouting_For_Demokritos() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204006).addOnQuestStart(questId);
		qe.registerQuestNpc(204006).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF2_SENSORY_AREA_Q1336_A_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF2_SENSORY_AREA_Q1336_B_210040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF2_SENSORY_AREA_Q1336_C_210040000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204006) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						PacketSendUtility.sendWhiteMessage(player, "Enter the 3 zones in the order requested by the quest!!!");
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204006) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
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
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0) {
				if (zoneName == ZoneName.get("LF2_SENSORY_AREA_Q1336_A_210040000")) {
					playQuestMovie(env, 43);
					changeQuestStep(env, 0, 16, false);
					return true;
				}
			} else if (var == 16) {
				if (zoneName == ZoneName.get("LF2_SENSORY_AREA_Q1336_B_210040000")) {
					playQuestMovie(env, 44);
					changeQuestStep(env, 16, 48, false);
					return true;
				}
			} else if (var == 48) {
				if (zoneName == ZoneName.get("LF2_SENSORY_AREA_Q1336_C_210040000")) {
					playQuestMovie(env, 45);
					changeQuestStep(env, 48, 48, true);
					return true;
				}
			}
		}
		return false;
	}
}