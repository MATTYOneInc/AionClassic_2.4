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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.chest.ChestTemplate;
import com.aionemu.gameserver.model.templates.chest.KeyItem;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;

@AIName("chest")
public class ChestAI2 extends ActionItemNpcAI2
{
	private ChestTemplate chestTemplate;

	@Override
	protected void handleDialogStart(final Player player) {
		chestTemplate = DataManager.CHEST_DATA.getChestTemplate(getNpcId());
		if (chestTemplate == null) {
			return;
		}
		super.handleDialogStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (analyzeOpening(player)) {
			if (isAlreadyDead()) {
				return;
			}
			AI2Actions.dieSilently(this, player);
			Collection<Player> players = new HashSet<Player>();
			if (player.isInGroup2()) {
				for (Player member: player.getPlayerGroup2().getOnlineMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
						players.add(member);
					}
				}
			} else if (player.isInAlliance2()) {
				for (Player member: player.getPlayerAlliance2().getOnlineMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
						players.add(member);
					}
				}
			} else {
				players.add(player);
			}
			//DropRegistrationService.getInstance().registerDrop(getOwner(), player, maxFrom(players).getLevel(), players);
			DropRegistrationService.getInstance().registerDrop(getOwner(), player, Collections.max(players, Comparator.comparingInt(Player::getLevel)).getLevel(), players);
			DropService.getInstance().requestDropList(player, getObjectId());
		} else {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111300, player.getObjectId(), 2));
		}
	}

	private boolean analyzeOpening(final Player player) {
		List<KeyItem> keyItems = chestTemplate.getKeyItem();
		int i = 0;
		for (KeyItem keyItem : keyItems) {
			if (keyItem.getItemId() == 0) {
				return true;
			}
			Item item = player.getInventory().getFirstItemByItemId(keyItem.getItemId());
			if (item != null) {
				if (item.getItemCount() != keyItem.getQuantity()) {
					int _i = 0;
					for (Item findedItem : player.getInventory().getItemsByItemId(keyItem.getItemId())) {
						_i += findedItem.getItemCount();
					}
					if (_i < keyItem.getQuantity()) {
						return false;
					}
				}
				i++;
				continue;
			} else {
				return false;
			}
		}
		if (i == keyItems.size()) {
			for (KeyItem keyItem : keyItems) {
				player.getInventory().decreaseByItemId(keyItem.getItemId(), keyItem.getQuantity());
			}
			return true;
		}
		return false;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
	        case CAN_SPAWN_ON_DAYTIME_CHANGE:
			    return AIAnswers.POSITIVE;
			case SHOULD_DECAY:
			    return AIAnswers.POSITIVE;
			case SHOULD_RESPAWN:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
			    return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}
}
