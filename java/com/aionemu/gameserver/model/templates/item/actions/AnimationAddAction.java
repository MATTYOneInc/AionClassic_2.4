package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.S_CUSTOM_ANIM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnimationAddAction")
public class AnimationAddAction extends AbstractItemAction
{
    @XmlAttribute
    protected Integer idle;
    @XmlAttribute
    protected Integer run;
    @XmlAttribute
    protected Integer jump;
    @XmlAttribute
    protected Integer rest;
	@XmlAttribute
    protected Integer shop;
    @XmlAttribute
    protected Integer minutes;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		player.getController().cancelUseItem();
		PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 1000, 0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.getInventory().decreaseItemCount(parentItem, 1) != 0)
					return;
				if (idle != null) {
					addMotion(player, idle);
				} if (run != null) {
					addMotion(player, run);
				} if (jump != null) {
					addMotion(player, jump);
				} if (rest != null) {
					addMotion(player, rest);
				} if (shop != null) {
					addMotion(player, shop);
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 0));
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300423, new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()), false);
			}
		}, 1000));
	}
	
	private void addMotion(Player player, int motionId){
		Motion motion = new Motion(motionId, minutes == null ? 0 : (int)(System.currentTimeMillis() / 1000) + minutes * 60, true);
		player.getMotions().add(motion, true);
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM((short) motion.getId(), motion.getRemainingTime()));
	}
}