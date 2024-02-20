/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.drop;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.network.aion.serverpackets.S_GROUP_ITEM_DIST;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DropDistributionService
{
	private static Logger log = LoggerFactory.getLogger(DropDistributionService.class);
	
	public static DropDistributionService getInstance() {
		return SingletonHolder.instance;
	}
	
	public void handleRoll(Player player, int roll, int itemId, int npcId, int index) {
        DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
        if (player == null || dropNpc == null) {
            return;
        }
        int luck = 0;
        if (player.isInGroup2() || player.isInAlliance2()) {
            if (roll == 0) {
				//You gave up rolling the dice.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DICE_GIVEUP_ME);
            } else {
				luck = Rnd.get(1, 100);
				//You rolled the dice and got %0 (max. %num1).
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DICE_RESULT_ME(luck, 100));
            } for (Player member : dropNpc.getInRangePlayers()) {
                if (member == null) {
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? " + player.isInAlliance2());
                    continue;
                }
                int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new S_GROUP_ITEM_DIST(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), luck, index));
                if (!player.equals(member) && member.isOnline()) {
					if (roll == 0) {
						//%0gave up rolling the dice.
						PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_DICE_GIVEUP_OTHER(player.getName()));
					} else {
						//%0rolled the dice and got %1 (max. %num2).
						PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_DICE_RESULT_OTHER(player.getName(), luck, 100));
					}
				}
            }
            distributeLoot(player, luck, itemId, npcId);
        }
    }
	
	public void handleBid(Player player, long bid, int itemId, int npcId, int index) {
        DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
        if (dropNpc == null) {
            return;
        } if (player == null) {
            return;
        } if (player.isInGroup2() || player.isInAlliance2()) {
            if ((bid > 0 && player.getInventory().getKinah() < bid) || bid < 0 || bid > 999999999) {
                bid = 0;
            } if (bid > 0) {
				//The account was instantly settled.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_PAY_RESULT_ME);
            } else {
				//You gave up the Bidding.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_PAY_GIVEUP_ME);
            } for (Player member : dropNpc.getInRangePlayers()) {
                if (member == null) {
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? " + player.isInAlliance2());
                    continue;
                }
                int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new S_GROUP_ITEM_DIST(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), bid, index));
				if (!player.equals(member) && member.isOnline()) {
					if (bid > 0) {
						//%0 settled the account instantly.
						PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_PAY_RESULT_OTHER(player.getName()));
					}  else {
						//%0 gave up the Bidding.
						PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_PAY_GIVEUP_OTHER(player.getName()));
					}
				}
            }
            distributeLoot(player, bid, itemId, npcId);
        }
    }
	
	private void distributeLoot(Player player, long luckyPlayer, int itemId, int npcId) {
        DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npcId);
        DropItem requestedItem = null;
        if (dropItems == null) {
            return;
        }
        synchronized (dropItems) {
            for (DropItem dropItem : dropItems) {
                if (dropItem.getIndex() == dropNpc.getCurrentIndex()) {
                    requestedItem = dropItem;
                    break;
                }
            }
        } if (requestedItem == null) {
            return;
        }
        player.unsetPlayerMode(PlayerMode.IN_ROLL);
        if (dropNpc.containsPlayerStatus(player)) {
            dropNpc.delPlayerStatus(player);
        } if (luckyPlayer > requestedItem.getHighestValue()) {
            requestedItem.setHighestValue(luckyPlayer);
            requestedItem.setWinningPlayer(player);
        } if (!dropNpc.getPlayerStatus().isEmpty()) {
            return;
        } if (player.isInGroup2() || player.isInAlliance2()) {
            for (Player member : dropNpc.getInRangePlayers()) {
                if (member == null) {
                    continue;
                } if (requestedItem.getWinningPlayer() == null) {
                    PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_PAY_ALL_GIVEUP);
                }
                int teamId = member.getCurrentTeamId();
			    PacketSendUtility.sendPacket(member, new S_GROUP_ITEM_DIST(teamId, requestedItem.getWinningPlayer() != null ? requestedItem.getWinningPlayer().getObjectId() : 1, itemId, npcId, dropNpc.getDistributionId(), 0xFFFFFFFF, requestedItem.getIndex()));
            }
        }
        LootGroupRules lgr = player.getLootGroupRules();
        if (lgr != null) {
            lgr.removeItemToBeDistributed(requestedItem);
        } if (requestedItem.getWinningPlayer() == null) {
            requestedItem.isFreeForAll(true);
            if (lgr != null && !lgr.getItemsToBeDistributed().isEmpty()) {
                DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
            }
            return;
        }
        requestedItem.isDistributeItem(true);
        DropService.getInstance().requestDropItem(player, npcId, dropNpc.getCurrentIndex());
        if (lgr != null && !lgr.getItemsToBeDistributed().isEmpty()) {
            DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
        }
    }
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final DropDistributionService instance = new DropDistributionService();
	}
}