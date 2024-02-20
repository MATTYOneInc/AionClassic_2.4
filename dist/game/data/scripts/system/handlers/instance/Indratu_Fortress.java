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
package instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(310090000)
public class Indratu_Fortress extends GeneralInstanceHandler
{
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700515: //Balaur Artifact.
				final QuestState qs3908 = player.getQuestStateList().getQuestState(3908); //[Group] To Master The Dragon.
				if (qs3908 != null && qs3908.getStatus() == QuestStatus.START && qs3908.getQuestVarById(0) == 0) {
					despawnNpc(npc);
					spawn(215384, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Avatar Malak.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
}