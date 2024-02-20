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
package quest.reshanta;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
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

public class _1701Governor_Directive extends QuestHandler
{
    private final static int questId = 1701;
	private final static int[] npcs = {278501};
	
    public _1701Governor_Directive() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("LATIS_PLAZA_400010000"), questId);
		qe.registerOnEnterZone(ZoneName.get("TEMINON_LEAP_400010000"), questId);
		qe.registerOnEnterZone(ZoneName.get("TEMINON_WHARF_400010000"), questId);
    }
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("LATIS_PLAZA_400010000") ||
		    zoneName == ZoneName.get("TEMINON_LEAP_400010000") ||
		    zoneName == ZoneName.get("TEMINON_WHARF_400010000")) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
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
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 278501) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 10002);
                        }
					} case SELECT_REWARD: {
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 278501) {
                if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    int[] reshantaMission = {1071, 1072, 1073, 1074, 1075, 1076, 1077};
                    for (int mission: reshantaMission) {
                        QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), mission, env.getDialogId()));
                    }
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}