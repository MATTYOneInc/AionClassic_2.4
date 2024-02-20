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
package quest.miragent_holy_templar;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _3940Loyalty extends QuestHandler
{
	private final static int questId = 3940;
	private final static int[] npcs = {203701, 203752};
	private final static int[] Ab1_1011_50_An = {251002, 251018, 251021, 251036, 251039};
	private final static int[] Dreadgion_DevaQ_DrakanBoss_50_Ah = {214823, 216850, 216886};
	
	public _3940Loyalty() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203701).addOnQuestStart(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: Ab1_1011_50_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: Dreadgion_DevaQ_DrakanBoss_50_Ah) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) {
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
			if (targetId == 203701) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 306) {
							return sendQuestDialog(env, 1693);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_ACTION_1694: {
						return sendQuestDialog(env, 1694);
					} case STEP_TO_3: {
						qs.setQuestVar(3);
						updateQuestStatus(env);
						PacketSendUtility.sendWhiteMessage(player, "Finish off one captain in <Baranath/Chantra Dredgion> or kill <Captain Mituna> in Reshanta.");
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 0, 6, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			} if (targetId == 203752) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case SELECT_ACTION_2718: {
						if (player.getCommonData().getDp() >= 4000) {
							return checkItemExistence(env, 5, 5, false, 186000083, 1, true, 2718, 2887, 0, 0);
						} else {
							return sendQuestDialog(env, 2802);
						}
					} case SET_REWARD: {
						player.getCommonData().setDp(0);
						return defaultCloseDialog(env, 5, 5, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203701) {
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
	public boolean onKillEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 6 && var < 306) {
				int[] Ab1_1011_50_An = {251002, 251018, 251021, 251036, 251039};
				for (int id: Ab1_1011_50_An) {
					if (targetId == id) {
						qs.setQuestVar(var + 1);
						updateQuestStatus(env);
						return true;
					}
				}
			} else if (var == 3) {
				int[] Dreadgion_DevaQ_DrakanBoss_50_Ah = {214823, 216850, 216886};
				return defaultOnKillEvent(env, Dreadgion_DevaQ_DrakanBoss_50_Ah, 3, 4);
			}
		}
		return false;
	}
}