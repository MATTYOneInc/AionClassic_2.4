package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Iterator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction
{
    @XmlAttribute(name = "count")
    private int count;
	
    @XmlAttribute(name = "min_level")
    private Integer min_level;
	
    @XmlAttribute(name = "max_level")
    private Integer max_level;
	
    @XmlAttribute(name = "manastone_only")
    private boolean manastone_only;
	
    @XmlAttribute(name = "chance")
    private float chance;
	
    @Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (isSupplementAction()) {
			return false;
		} if (parentItem == null || targetItem == null) {
			///The item cannot be found.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
			return false;
		} if (targetItem.getEnchantLevel() >= 15 && !parentItem.getItemTemplate().isManaStone()) {
			///You cannot enchant %0 any further.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot enchant items while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_INVALID_STANCE(new DescriptionId(2800159)));
			return false;
		}
		int msID = parentItem.getItemTemplate().getTemplateId() / 1000000;
		int tID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		if (msID != 166 && msID != 167 || msID != 166 && tID >= 120) {
			return false;
		}
		return true;
	}
	
    @Override
    public void act(final Player player, final Item parentItem, final Item targetItem) {
        act(player, parentItem, targetItem, null, 1);
    }
	
	public void act(final Player player, final Item parentItem, final Item targetItem, final Item supplementItem, final int targetWeapon) {
		if ((supplementItem != null) && (!checkSupplementLevel(player, supplementItem.getItemTemplate(), targetItem.getItemTemplate()))) {
			return;
		}
		final int currentEnchant = targetItem.getEnchantLevel();
		final boolean isSuccess = isSuccess(player, parentItem, targetItem, supplementItem, targetWeapon, currentEnchant);
		PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 2000, 0, 0));
		final ItemUseObserver Enchant = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), targetItem.getObjectId(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				///You have cancelled the enchanting of %0.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_CANCELED(targetItem.getItemTemplate().getNameId()));
			}
		};
		player.getObserveController().attach(Enchant);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(Enchant);
				ItemTemplate itemTemplate = parentItem.getItemTemplate();
				if (itemTemplate.isEnchantmentStone()) {
					BattlePassService.getInstance().onUpdateBattlePassMission(player, parentItem.getItemId(), 1, BattlePassAction.ITEM_PLAY);
					EnchantService.enchantItemAct(player, parentItem, targetItem, supplementItem, currentEnchant, isSuccess);
				} else {
					BattlePassService.getInstance().onUpdateBattlePassMission(player, parentItem.getItemId(), 1, BattlePassAction.ITEM_PLAY);
					EnchantService.socketManastoneAct(player, parentItem, targetItem, supplementItem, targetWeapon, isSuccess);
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 0));
				if (itemTemplate.isEnchantmentStone()) {
					Iterator<Player> iter = World.getInstance().getPlayersIterator();
					while (iter.hasNext()) {
						Player player2 = iter.next();
						if (targetItem.getEnchantLevel() >= 15 && isSuccess) {
							if (player2.getRace() == player.getRace()) {
								///%0 has succeeded in enchanting %1 to Level 15.
								PacketSendUtility.sendPacket(player2, S_MESSAGE_CODE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_15(player.getName(), targetItem.getItemTemplate().getNameId()));
							}
						}
					}
				}
			}
		}, 2000));
	}
	
    private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem, final Item supplementItem, final int targetWeapon, final int currentEnchant) {
		if (parentItem.getItemTemplate() != null) {
			ItemTemplate itemTemplate = parentItem.getItemTemplate();
			if (itemTemplate.isEnchantmentStone()) {
				return EnchantService.enchantItem(player, parentItem, targetItem, supplementItem);
			}
			return EnchantService.socketManastone(player, parentItem, targetItem, supplementItem, targetWeapon);
		}
		return false;
	}
	
    public int getCount() {
        return count;
    }
	
    public int getMaxLevel() {
        return max_level != null ? max_level : 0;
    }
	
    public int getMinLevel() {
        return min_level != null ? min_level : 0;
    }
	
    public boolean isManastoneOnly() {
        return manastone_only;
    }
	
    public float getChance() {
        return chance;
    }
	
    boolean isSupplementAction() {
        return getMinLevel() > 0 || getMaxLevel() > 0 || getChance() > 0 || isManastoneOnly();
    }
	
    private boolean checkSupplementLevel(final Player player, final ItemTemplate supplementTemplate, final ItemTemplate targetItemTemplate) {
        if (supplementTemplate.getCategory() != ItemCategory.ENCHANTMENT) {
            int minEnchantLevel = targetItemTemplate.getLevel();
            int maxEnchantLevel = targetItemTemplate.getLevel();
            EnchantItemAction action = supplementTemplate.getActions().getEnchantAction();
            if (action != null) {
                if (action.getMinLevel() != 0) {
                    minEnchantLevel = action.getMinLevel();
                } if (action.getMaxLevel() != 0) {
                    maxEnchantLevel = action.getMaxLevel();
                }
            } if (minEnchantLevel <= targetItemTemplate.getLevel() && maxEnchantLevel >= targetItemTemplate.getLevel()) {
                return true;
            }
            //You cannot use those Supplements.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_ENCHANT_ASSISTANT_NO_RIGHT_ITEM);
            return false;
        }
        return true;
    }
}