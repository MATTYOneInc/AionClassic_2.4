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
package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _3911Teachings_Of_Life extends QuestHandler
{
	private final static int questId = 3911;
	
	public _3911Teachings_Of_Life() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestSkill(972, questId);
		qe.registerQuestSkill(973, questId);
		qe.registerQuestSkill(974, questId);
		qe.registerQuestSkill(990, questId);
		qe.registerQuestSkill(1051, questId);
		qe.registerQuestSkill(1057, questId);
		qe.registerQuestSkill(1128, questId);
		qe.registerQuestSkill(2139, questId);
		qe.registerQuestNpc(203761).addOnQuestStart(questId);
        qe.registerQuestNpc(203761).addOnTalkEvent(questId);
        qe.registerQuestNpc(203707).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203761) {
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
			if (targetId == 203707) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 10002);
					} case SELECT_REWARD: {
						changeQuestStep(env, var, var, true);
				        return sendQuestDialog(env, 5);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203707) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onUseSkillEvent(QuestEnv env, int skillUsedId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		VisibleObject target = player.getTarget();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (((Creature) target).getLifeStats().isAlreadyDead() &&
			    skillUsedId == 972 || skillUsedId == 973 || skillUsedId == 974 || skillUsedId == 990 ||
				skillUsedId == 1051 || skillUsedId == 1057 || skillUsedId == 1128 || skillUsedId == 2139) {
				int var = qs.getQuestVarById(0);
				if (var >= 0 && var < 9) {
					changeQuestStep(env, var, var + 1, false);
					return true;
				} else if (var == 9) {
					qs.setQuestVar(10);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}