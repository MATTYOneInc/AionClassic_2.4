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
package quest.taloc_hollow;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

public class _11468With_Friend_Like_These extends QuestHandler
{
	private final static int questId = 11468;
	
	public _11468With_Friend_Like_These() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestItem(164000137, questId); //Shishir's Powerstone.
		qe.registerQuestItem(164000138, questId); //Gellmar's Wardstone.
		qe.registerQuestItem(164000139, questId); //Neith's Sleepstone.
		qe.registerQuestNpc(799526).addOnQuestStart(questId);
		qe.registerQuestNpc(799526).addOnTalkEvent(questId);
		qe.registerQuestNpc(799503).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799526) {
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
			if (targetId == 799503) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 10002);
						}
					} case SELECT_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799503) {
                return sendQuestEndDialog(env);
            }
        }
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, final Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int id = item.getItemTemplate().getTemplateId();
			if (player.getWorldId() == 300190000 && var == 0) {
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				if (id == 164000137) {
					if (var1 >= 0 && var1 < 10) {
						changeQuestStep(env, var1, var1 + 1, false, 1);
						return HandlerResult.SUCCESS;
					}
				} else if (id == 164000138) {
					if (var2 >= 0 && var2 < 5) {
						changeQuestStep(env, var2, var2 + 1, false, 2);
						return HandlerResult.SUCCESS;
					}
				} else if (id == 164000139) {
					if (var3 >= 0 && var3 < 3) {
						changeQuestStep(env, var3, var3 + 1, false, 3);
						return HandlerResult.SUCCESS;
					}
				}
			}
		}
		return HandlerResult.UNKNOWN;
	}
}