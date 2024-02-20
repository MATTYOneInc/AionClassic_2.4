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
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Actions.SelectDialogResult;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("quest_use_item")
public class QuestItemNpcAI2 extends ActionItemNpcAI2
{
	private List<Player> registeredPlayers = new ArrayList<Player>();
	
	@Override
	protected void handleDialogStart(Player player) {
		if (!(QuestEngine.getInstance().onCanAct(new QuestEnv(getOwner(), player, 0, 0),
			getObjectTemplate().getTemplateId(), QuestActionType.ACTION_ITEM_USE))) {
			return;
		}
		super.handleDialogStart(player);
	}
	
	@Override
	protected void handleUseItemFinish(Player player) {
		SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, -1);
		if (!dialogResult.isSuccess()) {
			if (isDialogNpc()) {
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), QuestDialog.SELECT_ACTION_1011.id()));
			}
			return;
		}
		QuestEnv questEnv = dialogResult.getEnv();
		if (QuestService.getQuestDrop(getNpcId()).isEmpty()) {
			return;
		} if (registeredPlayers.isEmpty()) {
			AI2Actions.scheduleRespawn(this);
			if (player.isInGroup2()) {
				registeredPlayers = QuestService.getEachDropMembersGroup(player.getPlayerGroup2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty()) {
					registeredPlayers.add(player);
				}
			} else if (player.isInAlliance2()) {
				registeredPlayers = QuestService.getEachDropMembersAlliance(player.getPlayerAlliance2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty()) {
					registeredPlayers.add(player);
				}
			} else {
				registeredPlayers.add(player);
			}
			AI2Actions.registerDrop(this, player, registeredPlayers);
			DropService.getInstance().requestDropList(player, getObjectId());
		} else if (registeredPlayers.contains(player)) {
			DropService.getInstance().requestDropList(player, getObjectId());
		}
	}
	
	private boolean isDialogNpc() {
		return getObjectTemplate().isDialogNpc();
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		registeredPlayers.clear();
	}
}