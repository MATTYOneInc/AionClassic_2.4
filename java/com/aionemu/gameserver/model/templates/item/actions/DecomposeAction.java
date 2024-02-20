/*
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.decomposable.*;
import com.aionemu.gameserver.model.templates.item.*;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.missing.SM_SELECT_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.missing.SM_SELECT_ITEM_ADD;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.*;

/**
 * @rework Rinzler (Encom)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecomposeAction")
public class DecomposeAction extends AbstractItemAction
{
	@XmlAttribute(name="select")
	public boolean isSelect;
	
	@XmlAttribute(name="all")
	public boolean isAll;
	
	@XmlAttribute(name="id")
	public int id;
	
	@XmlAttribute
    protected int count;
	
	private static final Logger log = LoggerFactory.getLogger(DecomposeAction.class);
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		int decomposeLevel = parentItem.getItemTemplate().getRequiredLevel(player.getCommonData().getPlayerClass());
		DecomposableTemplate template = DataManager.DECOMPOSABLE_TEMPLATE_DATA.getInfoByItemId(id);
		if (decomposeLevel == -1 || decomposeLevel > player.getLevel()) {
			///You cannot use %1 until you reach level %0.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(parentItem.getNameId(), parentItem.getItemTemplate().getLevel()));
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot extract item while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_INVALID_STANCE(new DescriptionId(2800159)));
			return false;
		} if (player.getInventory().isFullSpecialCube() || player.getInventory().isFull()) {
			///You cannot acquire the item because there is no space in the inventory.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DICE_INVEN_ERROR);
			return false;
		}
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		final List<DecomposableItemList> selectedList = new ArrayList<DecomposableItemList>();
		player.getController().cancelUseItem();
		if (this.isSelect) { //Selectable Chest.
			final ItemUseObserver moveObserver = new ItemUseObserver() {
				@Override
				public void abort() {
					player.getController().cancelTask(TaskId.ITEM_USE);
					player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
					player.getObserveController().removeObserver(this);
					PacketSendUtility.sendPacket(player, new SM_SELECT_ITEM_ADD(0, 0));
				}
			};
			player.getObserveController().attach(moveObserver);
			player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (player.getDecomposableItemLists().size() > 0) {
						player.getDecomposableItemLists().clear();
					} for (DecomposableItemList resultItem : getItemList(player, id)) {
						selectedList.add(resultItem);
					}
					player.setDecomposableItemLists(selectedList);
					PacketSendUtility.sendPacket(player, new SM_SELECT_ITEM(player, selectedList, parentItem.getObjectId()));
				}
			}, 0));
		} else if (this.isAll) {
			for (DecomposableItemList resultItem : getItemList(player, id)) {
				ItemService.addItem(player, resultItem.getId(), resultItem.getCount());
			}
			player.getInventory().decreaseByItemId(parentItem.getItemId(), 1);
		} else { //Decomposable Chest.
			PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 1000, 0, 0));
			final ItemUseObserver moveObserver = new ItemUseObserver() {
				@Override
				public void abort() {
					player.getController().cancelTask(TaskId.ITEM_USE);
					player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
					if (parentItem.getItemTemplate().getCategory() == ItemCategory.GATHERABLE) {
						///You have stopped opening the %0.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_UNCOMPRESS_COMPRESSED_ITEM_CANCELED(parentItem.getItemTemplate().getNameId()));
					} else if ((targetItem != null) && (targetItem.getItemTemplate().isArmor() || targetItem.getItemTemplate().isWeapon())) {
						///You have cancelled the extraction from %0.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_CANCELED(targetItem.getNameId()));
					} else {
						///You have cancelled using the item.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
					}
					PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
					player.getObserveController().removeObserver(this);
				}
			};
			player.getObserveController().attach(moveObserver);
			player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					player.getObserveController().removeObserver(moveObserver);
					List<DecomposableItemList> itemLists = getItemList(player, id);
					boolean validAction = true;
					if (validAction) {
						if (itemLists.size() > 0) {
							int index = Rnd.get(0, itemLists.size() - 1);
							DecomposableItemList itemresult = itemLists.get(index);
							ItemService.addItem(player, itemresult.getId(), itemresult.getCount());
							player.getInventory().decreaseByItemId(parentItem.getItemId(), 1);
							if (parentItem.getItemTemplate().getCategory() == ItemCategory.GATHERABLE) {
								///You have opened the %0.
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_UNCOMPRESS_COMPRESSED_ITEM_SUCCEEDED(parentItem.getNameId()));
							} else if ((targetItem != null) && (targetItem.getItemTemplate().isArmor() || targetItem.getItemTemplate().isWeapon())) {
								///You have successfully extracted from %0.
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_SUCCEED(targetItem.getNameId()));
							} else {
								///You have used %0.
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_USE_ITEM(new DescriptionId(parentItem.getNameId())));
							}
						}
					}
					PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, validAction ? 1 : 2, 384));
					player.getController().cancelTask(TaskId.ITEM_USE);
				}
			}, 1000));
		}
	}
	
	public List<DecomposableItemList> getItemList(Player player, int id) {
		List<DecomposableItemList> items = new FastList<DecomposableItemList>();
		DecomposableTemplate template = DataManager.DECOMPOSABLE_TEMPLATE_DATA.getInfoByItemId(id);
		for (DecomposableList list : template.getItems()) {
			if (list.getPlayerClass() == PlayerClass.ALL && list.getRace() == Race.PC_ALL && list.getMin_level() == 0 && list.getMax_level() == 0) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() != PlayerClass.ALL && list.getPlayerClass() ==  player.getPlayerClass() && list.getRace() == Race.PC_ALL && list.getMin_level() == 0 && list.getMax_level() == 0) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() != PlayerClass.ALL && list.getPlayerClass() ==  player.getPlayerClass() && list.getRace() != Race.PC_ALL && list.getRace() == player.getRace() && list.getMin_level() == 0 && list.getMax_level() == 0) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() == PlayerClass.ALL && list.getRace() != Race.PC_ALL && list.getRace() == player.getRace() && list.getMin_level() == 0 && list.getMax_level() == 0) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() == PlayerClass.ALL  && list.getRace() == Race.PC_ALL && list.getMin_level() != 0 && list.getMax_level() != 0 && player.getLevel() >= list.getMin_level() && player.getLevel() <= list.getMax_level()) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() != PlayerClass.ALL && list.getPlayerClass() ==  player.getPlayerClass() && list.getRace() == Race.PC_ALL && list.getMin_level() != 0 && list.getMax_level() != 0 && player.getLevel() >= list.getMin_level() && player.getLevel() <= list.getMax_level()) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() != PlayerClass.ALL && list.getPlayerClass() ==  player.getPlayerClass() && list.getRace() != Race.PC_ALL && list.getRace() == player.getRace() && list.getMin_level() != 0 && list.getMax_level() != 0 && player.getLevel() >= list.getMin_level() && player.getLevel() <= list.getMax_level()) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			} else if (list.getPlayerClass() == PlayerClass.ALL && list.getRace() != Race.PC_ALL && list.getRace() == player.getRace() && list.getMin_level() != 0 && list.getMax_level() != 0 && player.getLevel() >= list.getMin_level() && player.getLevel() <= list.getMax_level()) {
				for (DecomposableItemList it : list.getItemsCollections()) {
					items.add(it);
				}
			}
		}
		return items;
	}
}