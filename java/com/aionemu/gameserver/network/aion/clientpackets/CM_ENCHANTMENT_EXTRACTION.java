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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.actions.ExtractAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_ENCHANTMENT_EXTRACTION extends AionClientPacket
{
    Logger log = LoggerFactory.getLogger(CM_ENCHANMENT_STONES.class);
	
    private int itemId;
	
    public CM_ENCHANTMENT_EXTRACTION(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        itemId = readD();
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
        Item item = player.getInventory().getItemByObjId(itemId);
        ExtractAction action = new ExtractAction();
        if (action.canAct(player, item, item)) {
            action.act(player, item, item);
        }
    }
}