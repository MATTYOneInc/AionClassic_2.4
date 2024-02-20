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
package quest.brusthonin;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2092Grave_Of_The_Red_Sky_Legion extends QuestHandler
{
	private final static int questId = 2092;
	private final static int[] npcs = {205150, 205188, 205190, 205208, 205209, 205210, 205211, 205212, 205213, 205214, 700394, 730156, 730158, 730159, 730160, 730161, 730162, 730163};
	private final static int[] ZombieD_35_An = {214402, 214403, 214611};
	
	public _2092Grave_Of_The_Red_Sky_Legion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: ZombieD_35_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
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
			if (targetId == 205150) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1012: {
						playQuestMovie(env, 395);
						return sendQuestDialog(env, 1012);
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205188) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205190) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							giveQuestItem(env, 182209009, 1);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 700394) { //Dug-up Grave.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 1) {
							return useQuestObject(env, 1, 2, false, false);
						}
					}
				}
			} if (targetId == 730156) { //Sudri's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205208, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sudri's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730158) { //Angeiya's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205209, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Angeiya's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730159) { //Erna's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205210, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Erna's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730160) { //Genta's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205211, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Genta's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730161) { //Sith's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205212, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sith's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730162) { //Barache's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205213, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Barache's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 730163) { //Bert's Tombstone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220050000, player.getInstanceId(), 205214, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Bert's.
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 205208) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2717);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205209) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2802);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205210) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2887);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205211) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2972);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205212) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 3058);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205213) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 3143);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205214) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 3228);
						}
					} case STEP_TO_6: {
						removeQuestItem(env, 182209009, 1);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}	
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205150) {
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
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case 214402:
				case 214403:
				case 214611:
					if (var >= 6 && var < 20) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return true;
					} else if (var == 20) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
				break;
			}
		}
		return false;
	}
}