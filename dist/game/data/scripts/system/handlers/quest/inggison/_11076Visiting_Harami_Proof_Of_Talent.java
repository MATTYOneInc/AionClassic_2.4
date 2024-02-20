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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _11076Visiting_Harami_Proof_Of_Talent extends QuestHandler
{
	private final static int questId = 11076;
	
	public _11076Visiting_Harami_Proof_Of_Talent() {
		super(questId);
	}
	
	public void register() {
		qe.registerOnMovieEndQuest(513, questId);
		qe.registerQuestNpc(799025).addOnQuestStart(questId);
		qe.registerQuestNpc(799025).addOnTalkEvent(questId);
		qe.registerQuestNpc(799084).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q11076_A_1_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q11076_A_2_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q11076_B_210050000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_Q11076_C_210050000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799025) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						playQuestMovie(env, 512);
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
			if (targetId == 799084) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						playQuestMovie(env, 513);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799025) {
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
		if (movieId == 513) {
			qs.setQuestVar(4);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
			TeleportService2.teleportTo(env.getPlayer(), 210050000, 1338.0000f, 280.0000f, 589.0000f, (byte) 78);
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
			if (zoneName == ZoneName.get("LF4_Q11076_A_1_210050000") || zoneName == ZoneName.get("LF4_Q11076_A_2_210050000")) {
				if (var == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
					///You took the Illusion Fortress Windstream!
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_QUEST_SAY_LF4_18, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LF4_Q11076_B_210050000")) {
				if (var == 1) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					///You took the Illusion Fortress Windstream No.2!
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_QUEST_SAY_LF4_19, 0);
					return true;
				}
			} else if (zoneName == ZoneName.get("LF4_Q11076_C_210050000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					///You took the Phanoe & Taloc Windstream!
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_QUEST_SAY_LF4_20, 0);
					return true;
				}
			}
		}
		return false;
	}
}