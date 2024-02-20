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
package ai.worlds.beluslan;

import ai.GeneralNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Hoarfrost_Survivor")
public class Hoarfrost_SurvivorAI2 extends GeneralNpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 15) {
                if (startedEvent.compareAndSet(false, true)) {
					canThink = false;
                    hoarfrostSurvivor();
                }
            }
        }
    }
	
	private void hoarfrostSurvivor() {
        if (!isAlreadyDead()) {
			if (getNpcId() == 204806) {
				think();
				getSpawnTemplate().setWalkerId("df3_df3_npcpath_q2059");
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
			}
        }
    }
}