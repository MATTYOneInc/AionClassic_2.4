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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.utils.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_CRAFT extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_CRAFT.class);
	private int itemID;
	private long itemCount;
	private int unk;
	private int targetTemplateId;
	private int recipeId;
	private int targetObjId;
	private int materialsCount;
	private int craftType;
	
	public CM_CRAFT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		unk = readC();
		targetTemplateId = readD();
		recipeId = readD();
		targetObjId = readD();
		materialsCount = readH();
		for (int i = 0 ; i < materialsCount ; i++) {
			itemID = readD();
			itemCount = readQ();
			CraftService.checkComponents(player, recipeId, itemID, materialsCount);
		}
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.getController().isInShutdownProgress()) {
            return;
        } if (unk != 129) {
			VisibleObject staticObject = player.getKnownList().getKnownObjects().get(targetObjId);
			if (staticObject == null || !MathUtil.isIn3dRange(player, staticObject, 10.0f) ||
			    staticObject.getObjectTemplate().getTemplateId() != targetTemplateId) {
				return;
			}
		}
		CraftService.startCrafting(player, recipeId, targetObjId);
	}
}