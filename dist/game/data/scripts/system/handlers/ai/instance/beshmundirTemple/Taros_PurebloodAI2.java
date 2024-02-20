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
package ai.instance.beshmundirTemple;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/
 
@AIName("Taros_Pureblood")
public class Taros_PurebloodAI2 extends GeneralNpcAI2
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
                    tarosPureblood();
                }
            }
        }
    }
	
	private void tarosPureblood() {
        if (!isAlreadyDead()) {
			//Taros Pureblood.
			if (getNpcId() == 216528) {
				think();
				getSpawnTemplate().setWalkerId("Taros_Pureblood");
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							deleteTaros();
						}
					}
				}, 8000);
			}
        }
    }
	
	private void deleteTaros() {
        AI2Actions.deleteOwner(this);
    }
}