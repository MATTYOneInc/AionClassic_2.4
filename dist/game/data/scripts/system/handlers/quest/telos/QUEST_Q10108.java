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
package quest.telos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q10108 extends QuestHandler
{
	private final static int questId = 10108;
	private final static int[] npcs = {800713, 800714, 800715};
	
	public QUEST_Q10108() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_A_210070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_B_210070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_C_210070000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_A_220080000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_B_220080000"), questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_C_220080000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getLevel() >= 16 && player.getLevel() <= 55 && (qs == null || qs.getStatus() == QuestStatus.NONE)) {
			return QuestService.startQuest(env);
		}
		return false;
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800713) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 800714) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 800715) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						qs.setQuestVar(6);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						giveQuestItem(env, 182209905, 1);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800714) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182209905, 1);
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
			if (zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_A_210070000") ||
			    zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_A_220080000")) {
				if (var == 1) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_B_210070000") ||
			    zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_B_220080000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			} else if (zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_C_210070000") ||
			    zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10108_C_220080000")) {
				if (var == 4) {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}