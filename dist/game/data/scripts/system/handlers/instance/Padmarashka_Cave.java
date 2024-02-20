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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320150000)
public class Padmarashka_Cave extends GeneralInstanceHandler
{
	private int dramataEgg55;
	private Future<?> dramataTask;
	private boolean padmarashkaEvent;
	
	protected final int BN_SLEEPSELF_DRAMATA = 19186;
	protected final int IDDRAMATA_STATUP_NR = 20101;
	protected final int IDDRAMATA_STATUP_SPPAAD = 20174;
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
    }
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		padmarashkaTimer();
		//You must defeat the protector within the time limit to wake Padmarashka from the Protective Slumber.
		sendMsgByRace(1400711, Race.PC_ALL, 10000);
		//Padmarashka has cast defensive magic. You will be removed from Padmarashka's Cave in 2 hours.
		sendMsgByRace(1400506, Race.PC_ALL, 60000);
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
			case 218756: //Padmarashka.
			    dramataTask.cancel(true);
				//Padmarashka has died. You will be removed from Padmarashka's Cave in 30 minutes.
				sendMsgByRace(1400675, Race.PC_ALL, 10000);
			break;
			case 282613: //Padmarashka's Eggs.
			case 282614: //Huge Padmarashka's Eggs.
			    final Npc padmarashka = instance.getNpc(218756); //Padmarashka.
			    dramataEgg55++;
				if (padmarashka != null) {
					if (dramataEgg55 == 5) {
						//Padmarashka is about to lay eggs.
						sendMsgByRace(1400526, Race.PC_ALL, 2000);
						SkillEngine.getInstance().applyEffectDirectly(IDDRAMATA_STATUP_NR, padmarashka, padmarashka, 0);
					} else if (dramataEgg55 == 10) {
						//Padmarashka is furious after seeing so many of her eggs destroyed.
						sendMsgByRace(1401213, Race.PC_ALL, 2000);
						SkillEngine.getInstance().applyEffectDirectly(IDDRAMATA_STATUP_SPPAAD, padmarashka, padmarashka, 0);
					}
				}
			break;
			///Thunder Shield.
			case 281931:
			case 281932:
			case 281933:
			    despawnNpc(npc);
			break;
		}
    }
	
	@Override
	public void onSpawn(final Npc npc) {
		switch (npc.getNpcId()) {
			case 218756: //Padmarashka.
				SkillEngine.getInstance().applyEffectDirectly(BN_SLEEPSELF_DRAMATA, npc, npc, 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						npc.getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
					}
				}, 10000);
			break;
		}
	}
	
	@Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("PADMARASHKAS_NEST_320150000")) {
            if (!padmarashkaEvent) {
				padmarashkaEvent = true;
				final Npc padmarashka2 = instance.getNpc(218756); //Padmarashka.
				padmarashka2.getAi2().think();
				//Padmarashka has awoken from the Protective Slumber.
				sendMsgByRace(1400728, Race.PC_ALL, 0);
				despawnNpcs(instance.getNpcs(282123)); //Dramata Shield.
				padmarashka2.getEffectController().removeEffect(BN_SLEEPSELF_DRAMATA);
				padmarashka2.getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
			}
		}
    }
	
	private void padmarashkaTimer() {
		//You will be removed from Padmarashka's Cave in 1 hour and 30 minutes.
        this.sendMessage(1400507, 30 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 1 hour.
		this.sendMessage(1400508, 60 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 30 minutes.
		this.sendMessage(1400509, 90 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 15 minutes.
		this.sendMessage(1400510, 105 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 10 minutes.
		this.sendMessage(1400511, 110 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 5 minutes.
		this.sendMessage(1400512, 115 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 3 minutes.
		this.sendMessage(1400513, 117 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 2 minutes.
		this.sendMessage(1400514, 118 * 60 * 1000);
		//You will be removed from Padmarashka's Cave in 1 minute.
		this.sendMessage(1400515, 119 * 60 * 1000);
        dramataTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				//You have been forcibly removed from Padmarashka's Cave by Padmarashka's defensive magic.
				sendMsgByRace(1400524, Race.PC_ALL, 0);
				despawnNpcs(instance.getNpcs(218756)); //Padmarashka.
            }
        }, 7200000);
    }
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void sendMessage(final int msgId, long delay) {
        if (delay == 0) {
            this.sendMsg(msgId);
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                public void run() {
                    sendMsg(msgId);
                }
            }, delay);
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