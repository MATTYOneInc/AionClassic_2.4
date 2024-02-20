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
package instance.quest;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300540000)
public class Forgotten_SpaceQ extends GeneralInstanceHandler
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
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 701415: //ldf1_fobj_restrict_device.
				despawnNpc(npc);
				switch (player.getGender()) {
					case MALE:
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 703));
								}
							}
						});
					break;
					case FEMALE:
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 706));
								}
							}
						});
					break;
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