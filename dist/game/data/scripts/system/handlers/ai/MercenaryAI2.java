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
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("instance_summon")
public class MercenaryAI2 extends GeneralNpcAI2
{
	private AtomicBoolean isTransformed = new AtomicBoolean(false);
	
	@Override
	protected void handleDialogStart(Player player) {
		if (!isTransformed.get()) {
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 10));
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 59 && isTransformed.compareAndSet(false, true)) {
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 0));
			if (player.getSummon() != null) {
				//You already have a spirit following you.
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300072, new Object[0]));
				return true;
			}
			Summon summon = new Summon(getObjectId(), new SummonController(), getSpawnTemplate(), getObjectTemplate(), getObjectTemplate().getLevel(), 120);
			player.setSummon(summon);
			summon.setMaster(player);
			summon.setTarget(player.getTarget());
			summon.setKnownlist(getKnownList());
			summon.setEffectController(new EffectController(summon));
			summon.setPosition(getPosition());
			summon.setLifeStats(getLifeStats());
			PacketSendUtility.sendPacket(player, new S_CHANGE_MASTER(player, getObjectId()));
			PacketSendUtility.sendPacket(player, new S_CHANGE_FLAG(getObjectId(), 0, 38, 0));
			summon.setState(1);
			PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, EmotionType.START_EMOTE2, 0, summon.getObjectId()));
		} else if (dialogId == 1011) {
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1011));
        }
		return true;
	}
}