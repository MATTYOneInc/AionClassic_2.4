package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_RECIPE_DELETE extends AionClientPacket
{
	int recipeId;
	
	public CM_RECIPE_DELETE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
    @Override
	protected void readImpl() {
		recipeId = readD();
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
		player.getRecipeList().deleteRecipe(player, recipeId);
	}
}