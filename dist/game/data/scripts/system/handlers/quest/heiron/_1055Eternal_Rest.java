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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _1055Eternal_Rest extends QuestHandler
{
	private final static int questId = 1055;
	private final static int[] npcs = {204629, 204625, 204628, 204627, 204626, 204622, 700270};
	
	public _1055Eternal_Rest() {
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
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204629) {
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
			} if (targetId == 204625) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 188);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							giveQuestItem(env, 182201613, 1);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case SET_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204628) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1694);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182201609, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().die();
						player.getController().updateZone();
						///I'm so tired. Now I can rest.
						NpcShoutsService.getInstance().sendMsg(npc, 1111055, npc.getObjectId(), 0, 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204627) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1781);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182201610, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().die();
						player.getController().updateZone();
						///Thank you. Thank you.
						NpcShoutsService.getInstance().sendMsg(npc, 1111056, npc.getObjectId(), 0, 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204626) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1864);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182201611, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().die();
						player.getController().updateZone();
						///I don't want to be a Banshee any longer.
						NpcShoutsService.getInstance().sendMsg(npc, 1111057, npc.getObjectId(), 0, 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204622) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1949);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182201612, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().die();
						player.getController().updateZone();
						///I'm returning to the flow of Aether....
						NpcShoutsService.getInstance().sendMsg(npc, 1111058, npc.getObjectId(), 0, 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700270) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 3) {
							//Released Soul.
							QuestService.addNewSpawn(210040000, player.getInstanceId(), 204623, player.getX() + 3, player.getY(), player.getZ(), (byte) 0);
							QuestService.addNewSpawn(210040000, player.getInstanceId(), 204624, player.getX(), player.getY() + 3, player.getZ(), (byte) 0);
							return useQuestObject(env, 3, 4, false, 0, 0, 0, 182201613, 1);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204629) {
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