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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320100000)
public class Fire_Temple extends GeneralInstanceHandler
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
		///If a boss is not spawned, then it is replaced by a mob!!!
		switch (Rnd.get(1, 6)) {
			case 1:
				spawn(212794, 153.0201f, 299.6971f, 123.0225f, (byte) 41);
				spawn(212797, 322.3416f, 431.3785f, 134.5256f, (byte) 81);
				spawn(212799, 350.9436f, 351.7678f, 146.8524f, (byte) 59);
				spawn(212802, 296.8132f, 201.8575f, 119.3651f, (byte) 61);
				spawn(212807, 296.7394f, 92.98186f, 128.8082f, (byte) 26);
				spawn(212839, 127.0583f, 176.1410f, 99.66929f, (byte) 16); //Blue Crystal Molgat.
			break;
			case 2:
				spawn(212790, 127.0583f, 176.1410f, 99.66929f, (byte) 16);
				spawn(212797, 322.3416f, 431.3785f, 134.5256f, (byte) 81);
				spawn(212799, 350.9436f, 351.7678f, 146.8524f, (byte) 59);
				spawn(212802, 296.8132f, 201.8575f, 119.3651f, (byte) 61);
				spawn(212807, 296.7394f, 92.98186f, 128.8082f, (byte) 26);
				spawn(212840, 153.0201f, 299.6971f, 123.0225f, (byte) 41); //Lava Gatneri.
			break;
			case 3:
				spawn(212790, 127.0583f, 176.1410f, 99.66929f, (byte) 16);
				spawn(212794, 153.0201f, 299.6971f, 123.0225f, (byte) 41);
				spawn(212797, 322.3416f, 431.3785f, 134.5256f, (byte) 81);
				spawn(212802, 296.8132f, 201.8575f, 119.3651f, (byte) 61);
				spawn(212807, 296.7394f, 92.98186f, 128.8082f, (byte) 26);
				spawn(212841, 350.9436f, 351.7678f, 146.8524f, (byte) 59); //Flame Branch Flavi.
			break;
			case 4:
				spawn(212790, 127.0583f, 176.1410f, 99.66929f, (byte) 16);
				spawn(212794, 153.0201f, 299.6971f, 123.0225f, (byte) 41);
				spawn(212799, 350.9436f, 351.7678f, 146.8524f, (byte) 59);
				spawn(212802, 296.8132f, 201.8575f, 119.3651f, (byte) 61);
				spawn(212807, 296.7394f, 92.98186f, 128.8082f, (byte) 26);
				spawn(212842, 322.3416f, 431.3785f, 134.5256f, (byte) 81); //Black Smoke Asparn.
			break;
			case 5:
				spawn(212790, 127.0583f, 176.1410f, 99.66929f, (byte) 16);
				spawn(212794, 153.0201f, 299.6971f, 123.0225f, (byte) 41);
				spawn(212797, 322.3416f, 431.3785f, 134.5256f, (byte) 81);
				spawn(212799, 350.9436f, 351.7678f, 146.8524f, (byte) 59);
				spawn(212807, 296.7394f, 92.98186f, 128.8082f, (byte) 26);
				spawn(212843, 296.8132f, 201.8575f, 119.3651f, (byte) 61); //Tough Sipus.
			break;
			case 6:
				spawn(212790, 127.0583f, 176.1410f, 99.66929f, (byte) 16);
				spawn(212794, 153.0201f, 299.6971f, 123.0225f, (byte) 41);
				spawn(212797, 322.3416f, 431.3785f, 134.5256f, (byte) 81);
				spawn(212799, 350.9436f, 351.7678f, 146.8524f, (byte) 59);
				spawn(212802, 296.8132f, 201.8575f, 119.3651f, (byte) 61);
				spawn(212845, 296.7394f, 92.98186f, 128.8082f, (byte) 26); //Broken Wing Kutisen.
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(212846, 421.9691f, 93.5550f, 117.3052f, (byte) 51); //Kromede The Corrupt.
			break;
			case 2:
				spawn(214621, 421.9691f, 93.5550f, 117.3052f, (byte) 51); //Vile Judge Kromede.
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700237: //Sacred Chalice.
				///It was a fake Sacred Chalice!
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100727, player.getObjectId(), 2));
			break;
			case 730048: //Fire Temple Exit.
				if (player.getRace() == Race.ELYOS) {
					fireTempleExitE(player, 1343.0000f, 350.0000f, 348.0000f, (byte) 0);
				} else {
					fireTempleExitA(player, 1592.0000f, 977.0000f, 140.0000f, (byte) 0);
				}
			break;
		}
	}
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/2036/
			case 204409: //Agnita.
			    final Npc agnita = instance.getNpc(204409);
				///Thank you for breaking the seal of Kromede, [%username]!
				NpcShoutsService.getInstance().sendMsg(agnita, 1100705, agnita.getObjectId(), 0, 3000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(204409));
					}
				}, 120000);
			break;
		}
	}
	
	protected void fireTempleExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210020000, 1, x, y, z, h);
	}
	protected void fireTempleExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220020000, 1, x, y, z, h);
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
}