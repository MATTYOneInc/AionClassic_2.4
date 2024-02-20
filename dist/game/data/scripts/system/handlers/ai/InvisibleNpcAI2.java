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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.S_INVISIBLE_LEVEL;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler
 */

@AIName("invisible_npc")
public class InvisibleNpcAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleSpawned() {
        super.handleSpawned();
		switch (getNpcId()) {
			//Heiron.
			case 211172:
			case 212112:
			//Telos.
			case 219351:
			case 219352:
			case 219355:
			case 219356:
			case 219370:
			case 219371:
			    hideShape();
			break;
		}
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
	}
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().unsetVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
    }
    
    @Override
	protected void handleTargetGiveup() {
    	super.handleTargetGiveup();
		switch (getNpcId()) {
			//Heiron.
			case 211172:
			case 212112:
			//Telos.
			case 219351:
			case 219352:
			case 219355:
			case 219356:
			case 219370:
			case 219371:
			    hideShape();
			break;
		}
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
	}
	
	private void hideShape() {
	    SkillEngine.getInstance().applyEffectDirectly(17001, getOwner(), getOwner(), 0); //Hide Shape.
	}
	
	@Override
    protected void handleDied() {
        super.handleDied();
	}
}