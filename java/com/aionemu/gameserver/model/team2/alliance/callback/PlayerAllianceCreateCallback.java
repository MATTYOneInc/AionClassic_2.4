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
package com.aionemu.gameserver.model.team2.alliance.callback;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.gameserver.model.gameobjects.player.Player;

@SuppressWarnings("rawtypes")
public abstract class PlayerAllianceCreateCallback implements Callback
{
    @Override
    public CallbackResult beforeCall(Object obj, Object[] args) {
        onBeforeAllianceCreate((Player) args[0]);
        return CallbackResult.newContinue();
    }
	
    @Override
    public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
        onAfterAllianceCreate((Player) args[0]);
        return CallbackResult.newContinue();
    }
	
    @Override
    public Class<? extends Callback> getBaseClass() {
        return PlayerAllianceCreateCallback.class;
    }
	
    public abstract void onBeforeAllianceCreate(Player player);
    public abstract void onAfterAllianceCreate(Player player);
}