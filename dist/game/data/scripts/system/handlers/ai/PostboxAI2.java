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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("postbox")
public class PostboxAI2 extends NpcAI2
{
	@Override
	protected void handleDialogStart(Player player) {
		int level = player.getLevel();
		if (level < 10) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FREE_EXPERIENCE_CHARACTER_CANT_SEND_ITEM("10"));
			return;
		}
		PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 18));
		player.getMailbox().sendMailList(false);
	}
	
	@Override
	protected void handleDialogFinish(Player player) {
	}
}