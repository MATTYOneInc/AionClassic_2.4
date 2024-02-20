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
package quest.daevanion;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2993Another_Beginning extends QuestHandler
{
	private int item;
	private int choice = 0;
	private final static int questId = 2993;
	
	private final static int dialogs[] = {1013, 1034, 1055, 1076, 5103, 1098, 1119, 1140, 1161, 5104, 1183, 1204,
	1225, 1246, 5105, 1268, 1289, 1310, 1331, 5106, 2376, 2461, 2546, 2631, 2632};
	
	private final static int daevanionArmor[] = {110100932, 113100844, 112100791, 111100832, 114100867, 110300882,
	113300861, 112300785, 111300835, 114300894, 110500850, 113500828, 112500775, 111500822, 114500838, 110600835, 113600801, 112600786, 111600814, 114600795};
	
	public _2993Another_Beginning() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204076).addOnQuestStart(questId);
		qe.registerQuestNpc(204076).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		int dialogId = env.getDialogId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 204076) {
				if (dialogId == 54) {
					QuestService.startQuest(env);
					return sendQuestDialog(env, 1011);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			long daevanionLight = player.getInventory().getItemCountByItemId(186000041);
			if (targetId == 204076) {
				if (dialogId == 54) {
					return sendQuestDialog(env, 1011);
				} switch (dialogId) {
					case 1012:
					case 1097:
					case 1182:
					case 1267:
						return sendQuestDialog(env, dialogId);
					case 1013:
					case 1034:
					case 1055:
					case 1076:
					case 5103:
					case 1098:
					case 1119:
					case 1140:
					case 1161:
					case 5104:
					case 1183:
					case 1204:
					case 1225:
					case 1246:
					case 5105:
					case 1268:
					case 1289:
					case 1310:
					case 1331:
					case 5106:
					case 2376:
					case 2461:
					case 2546:
					case 2631:
					case 2632: {
						item = getItem(dialogId);
						if (player.getInventory().getItemCountByItemId(item) > 0) {
							return sendQuestDialog(env, 1013);
						} else {
							return sendQuestDialog(env, 1352);
						}
					}
					case 10000:
					case 10001:
					case 10002:
					case 10003: {
						if (daevanionLight == 0) {
							return sendQuestDialog(env, 1009);
						}
						changeQuestStep(env, 0, 0, true);
						choice = dialogId - 10000;
						return sendQuestDialog(env, choice + 5);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204076) {
				removeQuestItem(env, item, 1);
				removeQuestItem(env, 186000041, 1);
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}
	
	private int getItem(int dialogId) {
		int x = 0;
		for (int id : dialogs) {
			if (id == dialogId) {
				break;
			}
			x++;
		}
		return (daevanionArmor[x]);
	}
}