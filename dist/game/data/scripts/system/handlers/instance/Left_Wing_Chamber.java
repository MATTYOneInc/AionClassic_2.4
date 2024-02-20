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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300080000)
public class Left_Wing_Chamber extends GeneralInstanceHandler
{
	private Future<?> chestLeftWingTask;
	private boolean isStartTimer = false;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
		spawn(700455, 672.834290f, 606.225037f, 322.475067f, (byte) 0, 1);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		switch (Rnd.get(1, 3)) {
			case 1:
				spawn(700466, 208.0130f, 684.2384f, 364.6045f, (byte) 115);
				spawn(700466, 196.0931f, 552.6651f, 364.6045f, (byte) 1);
				spawn(700467, 229.1023f, 746.4455f, 364.6045f, (byte) 112);
				spawn(700467, 195.4822f, 618.6922f, 364.6045f, (byte) 118);
				spawn(700468, 230.8335f, 424.9972f, 364.6045f, (byte) 8);
				spawn(700468, 208.4219f, 487.3297f, 364.6045f, (byte) 8);
			break;
			case 2:
				spawn(700467, 208.0130f, 684.2384f, 364.6045f, (byte) 115);
				spawn(700467, 196.0931f, 552.6651f, 364.6045f, (byte) 1);
				spawn(700468, 229.1023f, 746.4455f, 364.6045f, (byte) 112);
				spawn(700468, 195.4822f, 618.6922f, 364.6045f, (byte) 118);
				spawn(700466, 230.8335f, 424.9972f, 364.6045f, (byte) 8);
				spawn(700466, 208.4219f, 487.3297f, 364.6045f, (byte) 8);
			break;
			case 3:
				spawn(700468, 208.0130f, 684.2384f, 364.6045f, (byte) 115);
				spawn(700468, 196.0931f, 552.6651f, 364.6045f, (byte) 1);
				spawn(700466, 229.1023f, 746.4455f, 364.6045f, (byte) 112);
				spawn(700466, 195.4822f, 618.6922f, 364.6045f, (byte) 118);
				spawn(700467, 230.8335f, 424.9972f, 364.6045f, (byte) 8);
				spawn(700467, 208.4219f, 487.3297f, 364.6045f, (byte) 8);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(215424, 502.8083f, 502.7722f, 352.9443f, (byte) 45);
			break;
			case 2:
				spawn(215424, 507.9877f, 661.3476f, 352.9463f, (byte) 75);
			break;
		}
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 215424: //leftciel_nagawinamedq_35_ah.
				chestLeftWingTask.cancel(true);
				instance.doOnAllPlayers(new Visitor<Player>() {
			        @Override
			        public void visit(Player player) {
				        if (player.isOnline()) {
						    PacketSendUtility.sendPacket(player, new S_QUEST(0, 0));
					    }
				    }
			    });
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206089: //idab1_sensoryarea_timer_c.
				if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					if (!isStartTimer) {
						isStartTimer = true;
						System.currentTimeMillis();
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									leftWingTimer();
									//The Balaur protective magic ward has been activated.
									sendMsgByRace(1400243, Race.PC_ALL, 0);
									PacketSendUtility.sendPacket(player, new S_QUEST(0, 900));
								}
							}
						});
					}
				}
			break;
		}
    }
	
	private void leftWingTimer() {
		chestLeftWingTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//All the Ancient Treasure Boxes are missing.
				sendMsgByRace(1400244, Race.PC_ALL, 0);
				despawnNpcs(instance.getNpcs(215424));
				despawnNpcs(instance.getNpcs(700466));
				despawnNpcs(instance.getNpcs(700467));
				despawnNpcs(instance.getNpcs(700468));
				//ab1_1131_treasurebox_fail.
				spawn(700465, 208.0130f, 684.2384f, 364.6045f, (byte) 115);
				spawn(700465, 196.0931f, 552.6651f, 364.6045f, (byte) 1);
				spawn(700465, 229.1023f, 746.4455f, 364.6045f, (byte) 112);
				spawn(700465, 195.4822f, 618.6922f, 364.6045f, (byte) 118);
				spawn(700465, 230.8335f, 424.9972f, 364.6045f, (byte) 8);
				spawn(700465, 208.4219f, 487.3297f, 364.6045f, (byte) 8);
			}
		}, 900000); //...15 Min.
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700455: //ab1_1131_treasuregate_out.
				if (player.getRace() == Race.ELYOS) {
					IDAbRe_Low_WcielE(player, 1374.0000f, 1074.0000f, 450.0000f, (byte) 65);
				} else {
					IDAbRe_Low_WcielA(player, 1428.0000f, 2190.0000f, 375.0000f, (byte) 104);
				}
			break;
		}
	}
	
	protected void IDAbRe_Low_WcielE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void IDAbRe_Low_WcielA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
}