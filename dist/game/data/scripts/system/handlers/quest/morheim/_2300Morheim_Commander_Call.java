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
package quest.morheim;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2300Morheim_Commander_Call extends QuestHandler
{
	private final static int questId = 2300;
	private final static int[] npcs = {204301};
	
	public _2300Morheim_Commander_Call() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ASMODIANS && player.getLevel() >= 20 && player.getLevel() <= 55) {
            QuestService.startQuest(env);
            return true;
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
            if (targetId == 204301) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
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
            if (targetId == 204301) {
                if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    int[] morheimMission = {2031, 2032, 2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040, 2041};
                    for (int mission: morheimMission) {
                        QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), mission, env.getDialogId()));
                    }
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}