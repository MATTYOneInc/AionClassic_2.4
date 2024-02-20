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
package quest.steel_rake;

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

public class _3217Imprisoned_Guardian extends QuestHandler
{
	private final static int questId = 3217;
	
	public _3217Imprisoned_Guardian() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(798335).addOnTalkEvent(questId);
		qe.registerQuestNpc(204590).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("INSIDE_STEEL_RAKE_1_300100000"), questId);
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("INSIDE_STEEL_RAKE_1_300100000")) {
			if (qs == null || qs.canRepeat() || qs.getStatus() == QuestStatus.NONE) {
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
		int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798335) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case SET_REWARD: {
						changeQuestStep(env, 1, 1, true);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 0, 1, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 1) {
							defaultCloseDialog(env, 1, 1);
						} else if (var == 0) {
							defaultCloseDialog(env, 0, 0);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204590) {
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
}