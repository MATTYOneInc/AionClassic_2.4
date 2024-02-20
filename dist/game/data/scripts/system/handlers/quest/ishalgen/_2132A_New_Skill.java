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
package quest.ishalgen;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2132A_New_Skill extends QuestHandler
{
	private final static int questId = 2132;
	
	public _2132A_New_Skill() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203527).addOnTalkEvent(questId);
		qe.registerQuestNpc(203528).addOnTalkEvent(questId); 
		qe.registerQuestNpc(203529).addOnTalkEvent(questId);
		qe.registerQuestNpc(203530).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (QuestService.startQuest(env)) {
			qs = player.getQuestStateList().getQuestState(questId);
			qs.setStatus(QuestStatus.REWARD);
			PlayerClass playerClass = player.getPlayerClass();
			if (!playerClass.isStartingClass()) {
				playerClass = PlayerClass.getStartingClassFor(playerClass);
			} switch (playerClass) {
				case WARRIOR:
					qs.setQuestVar(1);
				break;
				case SCOUT:
					qs.setQuestVar(2);
				break;
				case MAGE:
					qs.setQuestVar(3);
				break;
				case PRIEST:
					qs.setQuestVar(4);
				break;
			}
			updateQuestStatus(env);
		}
		return true;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false;
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		switch (targetId) {
			case 203527:
				if (playerClass == PlayerClass.WARRIOR) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 1011);
					else if (env.getDialogId() == 1009)
						return sendQuestDialog(env, 5);
					else
						return this.sendQuestEndDialog(env);
				}
				return false;
			case 203528:
				if (playerClass == PlayerClass.SCOUT) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 1352);
					else if (env.getDialogId() == 1009)
						return sendQuestDialog(env, 6);
					else
						return this.sendQuestEndDialog(env);
				}
				return false;
			case 203529:
				if (playerClass == PlayerClass.MAGE) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 1693);
					else if (env.getDialogId() == 1009)
						return sendQuestDialog(env, 7);
					else
						return this.sendQuestEndDialog(env);
				}
				return false;
			case 203530:
				if (playerClass == PlayerClass.PRIEST) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 2034);
					else if (env.getDialogId() == 1009)
						return sendQuestDialog(env, 8);
					else
						return this.sendQuestEndDialog(env);
				}
				return false;
		}
		return false;
	}
}