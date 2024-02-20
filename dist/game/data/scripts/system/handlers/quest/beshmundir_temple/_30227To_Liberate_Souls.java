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
package quest.beshmundir_temple;

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

public class _30227To_Liberate_Souls extends QuestHandler
{
	private final static int questId = 30227;
	private final static int[] IDCatacombsN_Lichkey4_55_Ae = {216590};
	private final static int[] IDCatacombsN_LichNmd_55_Ah = {216245};
	
	public _30227To_Liberate_Souls() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798946).addOnQuestStart(questId);
		qe.registerQuestNpc(798946).addOnTalkEvent(questId);
		qe.registerQuestNpc(799521).addOnTalkEvent(questId);
		for (int mob: IDCatacombsN_Lichkey4_55_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDCatacombsN_LichNmd_55_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnEnterZone(ZoneName.get("BESHMUNDIRS_WALK_300170000"), questId);
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("BESHMUNDIRS_WALK_300170000")) {
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
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798946) {
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
			if (targetId == 799521) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798946) {
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
    public boolean onKillEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (var == 1) {
				switch (targetId) {
					case 216590: {
						qs.setQuestVar(2);
						updateQuestStatus(env);
						return true;
					}
                }
			} else if (var == 2) {
				switch (targetId) {
					case 216245: {
						qs.setQuestVar(2);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
                }
			}
		}
		return false;
    }
}