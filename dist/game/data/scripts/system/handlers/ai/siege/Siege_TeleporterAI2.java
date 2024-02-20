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
package ai.siege;

import ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.network.aion.serverpackets.S_ARTIFACT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_ABYSS_TELEPORTER_STATUS;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("siege_teleporter")
public class Siege_TeleporterAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleDespawned() {
		siegeTeleport(false);
		artifactTeleport(false);
		super.handleDespawned();
	}
	
	@Override
	protected void handleSpawned() {
		siegeTeleport(true);
		artifactTeleport(true);
		super.handleSpawned();
	}
	
	private void siegeTeleport(final boolean status) {
		final int id = ((SiegeNpc) getOwner()).getSiegeId();
		SiegeService.getInstance().getFortress(id).setCanTeleport(status);
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_CHANGE_ABYSS_TELEPORTER_STATUS(id, status));
			}
		});
	}
	
	private void artifactTeleport(final boolean status) {
        final int id = ((SiegeNpc) getOwner()).getSiegeId();
        SiegeService.getInstance().getArtifact(id).setCanTeleport(status);
        getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                PacketSendUtility.sendPacket(player, new S_ARTIFACT_INFO(id, status));
            }
        });
    }
}