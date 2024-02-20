package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.decomposable.DecomposableItemList;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.missing.SM_SELECT_ITEM_ADD;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_SELECT_ITEM extends AionClientPacket
{
	private int uniqueItemId;
	private int index;

	private static final Logger log = LoggerFactory.getLogger(CM_SELECT_ITEM.class);
	
	@SuppressWarnings("unused")
	private int unk;
	public CM_SELECT_ITEM(int opcode, AionConnection.State state, AionConnection.State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		this.uniqueItemId = readD();
		this.unk = readD();
		this.index = readC();
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
		final Item item = player.getInventory().getItemByObjId(uniqueItemId);
		if (item == null) {
			return;
		}
		final int nameId = item.getNameId();
		sendPacket(new S_USE_ITEM(player.getObjectId(), player.getObjectId(), uniqueItemId, item.getItemId(), 1000, 0, 0));
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400453, new DescriptionId(nameId)));
				player.getObserveController().removeObserver(this);
				sendPacket(new S_USE_ITEM(player.getObjectId(), player.getObjectId(), uniqueItemId, item.getItemId(), 0, 2, 2));
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				sendPacket(new S_USE_ITEM(player.getObjectId(), player.getObjectId(), uniqueItemId, item.getItemId(), 0, 1, 1));
				boolean delete = player.getInventory().decreaseByObjectId(uniqueItemId, 1L);
				if (delete) {
					DecomposableItemList selectitem = player.getDecomposableItemLists().get(index);
					ItemService.addItem(player, selectitem.getId(), selectitem.getCount());
					sendPacket(new SM_SELECT_ITEM_ADD(uniqueItemId, index));
				}
			}
		}, 1000));
	}
}