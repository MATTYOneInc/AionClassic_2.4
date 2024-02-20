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
package quest.noble_siel_supreme;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _30338Improved_Staff extends QuestHandler
{
	private final static int questId = 30338;
	
	public _30338Improved_Staff() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182209732, questId);
		qe.registerQuestNpc(799336).addOnQuestStart(questId);
		qe.registerQuestNpc(799336).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799336) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182209732, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			long sielEssence = player.getInventory().getItemCountByItemId(182209736);
            if (targetId == 799336) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					if (sielEssence == 1) {
						removeQuestItem(env, 101500736, 1);
						removeQuestItem(env, 186000099, 1);
						removeQuestItem(env, 186000106, 1);
						removeQuestItem(env, 186000107, 1);
						return sendQuestDialog(env, 5);
					} else {
						return sendQuestDialog(env, 2716);
					}
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		long vorpalEssence = player.getInventory().getItemCountByItemId(186000099);
		long blessedAuriumIngot = player.getInventory().getItemCountByItemId(186000106);
		long specialShulackRefiner = player.getInventory().getItemCountByItemId(186000107);
		if (id != 182209732) {
			return HandlerResult.UNKNOWN;
		} if (!player.isInsideZone(ZoneName.get("DEBILKARIMS_FORGE_300160000"))) {
			return HandlerResult.UNKNOWN;
		} if (vorpalEssence == 0 && blessedAuriumIngot == 0 && specialShulackRefiner == 0) {
			PacketSendUtility.sendWhiteMessage(player, "You need collect <Vorpal Essence + Blessed Aurium Ingot + Special Shulack Refiner> for use this item!!!");
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				qs.setQuestVar(0);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				giveQuestItem(env, 182209736, 1);
				removeQuestItem(env, 182209732, 1);
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}