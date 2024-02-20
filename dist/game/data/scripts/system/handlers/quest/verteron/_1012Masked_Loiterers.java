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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1012Masked_Loiterers extends QuestHandler
{
	private final static int questId = 1012;
	private final static int[] npcs = {203111};
	
	public _1012Masked_Loiterers() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("LF1A_SENSORY_AREA_Q1012_A_210030000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF1A_SENSORY_AREA_Q1012_B_210030000"), questId);
		qe.registerOnEnterZone(ZoneName.get("LF1A_SENSORY_AREA_Q1012_C_210030000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203111) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} if (var == 2) {
							return sendQuestDialog(env, 1352);
						} if (var == 3) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 3, 3, true, 5, 2034);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203111) {
				return sendQuestEndDialog(env);
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
			if (zoneName == ZoneName.get("LF1A_SENSORY_AREA_Q1012_A_210030000") ||
			    zoneName == ZoneName.get("LF1A_SENSORY_AREA_Q1012_B_210030000") ||
				zoneName == ZoneName.get("LF1A_SENSORY_AREA_Q1012_C_210030000")) {
				if (var == 1) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}