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
package ai.instance.darkPoeta;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Wounded_Scar")
public class Wounded_ScarAI2 extends GeneralNpcAI2
{
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 15) {
                if (startedEvent.compareAndSet(false, true)) {
                    moveAwayWoundedScar();
                }
            }
        }
    }
	
	private void moveAwayWoundedScar() {
        if (!isAlreadyDead()) {
            if (getNpcId() == 281115) { //Wounded Scar.
			    think();
				getSpawnTemplate().setWalkerId("idlf1_e_path_ska_50");
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnWoundedScar();
						spawn(214871, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading()); //Wounded Scar.
					}
				}, 301000);
			}
        }
    }
	
	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		int point = getOwner().getMoveController().getCurrentPoint();
		if (getNpcId() == 281115) { //Wounded Scar.
			if (point == 6) {
				spawn(281148, getOwner().getX() + 5, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(281148, getOwner().getX(), getOwner().getY() + 5, getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(281148, getOwner().getX() - 5, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 15) {
				spawn(281149, getOwner().getX() + 5, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(281149, getOwner().getX(), getOwner().getY() + 5, getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 18) {
				spawn(281150, getOwner().getX() + 5, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 22) {
				spawn(281151, getOwner().getX() + 5, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			}
		}
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		experimentAftereffect();
	}
	
	private void experimentAftereffect() {
	    SkillEngine.getInstance().applyEffectDirectly(18532, getOwner(), getOwner(), 0); //Experiment Aftereffect.
	}
	
	private void despawnWoundedScar() {
		AI2Actions.deleteOwner(this);
	}
}