package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FortressAssault extends Assault<FortressSiege>
{
	private final boolean isBalaurea;
	private boolean spawned = false;
	private List<float[]> spawnLocations;
	
	public FortressAssault(FortressSiege siege) {
		super(siege);
		this.isBalaurea = worldId != 400010000;
	}
	
	@Override
	protected void scheduleAssault(int delay) {
		dredgionTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//spawnAttackers();
					}
				}, Rnd.get(30, 60) * 1000);
			}
		}, delay * 1000);
	}
	
	@Override
	protected void onAssaultFinish(boolean captured) {
		if (!spawned) {
			return;
		} if (!captured) {
			return;
		} else {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					///The Balaur have killed the Guardian General.
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_DRAGON_BOSS_KILLED, 0);
				}
			});
		}
		spawnLocations.clear();
	}
	
	private void spawnAttackers() {
        if (this.spawned) {
            return;
        }
        this.spawned = true;
        float x = this.boss.getX();
        float y = this.boss.getY();
        float z = this.boss.getZ();
        byte heading = this.boss.getSpawn().getHeading();
        int radius1 = this.isBalaurea ? 5 : Rnd.get((int)7, (int)13);
        int radius2 = this.isBalaurea ? 9 : Rnd.get((int)15, (int)20);
        int amount = this.isBalaurea ? Rnd.get((int)10, (int)15) : Rnd.get((int)10, (int)15);
        float minAngle = MathUtil.convertHeadingToDegree(heading) - 90.0f;
        if (minAngle < 0.0f) {
            minAngle += 360.0f;
        }
        double minRadian = Math.toRadians(minAngle);
        float interval = (float)(Math.PI / (double)(amount / 2));
        List<Integer> idList = this.getSpawnIds();
        int commanderCount = this.isBalaurea ? 0 : Rnd.get((int)2);
        this.spawnRegularBalaurs();
        for (int i = 0; amount > i; ++i) {
            SiegeSpawnTemplate spawn;
            int templateId;
            float y1;
            float x1;
            if (i < amount / 2) {
                x1 = (float)(Math.cos(minRadian + (double)(interval * (float)i)) * (double)radius1);
                y1 = (float)(Math.sin(minRadian + (double)(interval * (float)i)) * (double)radius1);
            } else {
                x1 = (float)(Math.cos(minRadian + (double)(interval * (float)(i - amount / 2))) * (double)radius2);
                y1 = (float)(Math.sin(minRadian + (double)(interval * (float)(i - amount / 2))) * (double)radius2);
            }
            int n = templateId = i <= commanderCount ? idList.get(0).intValue() : idList.get(Rnd.get((int)1, (int)(idList.size() - 1))).intValue();
            if (i > Math.round(amount / 3) && !this.spawnLocations.isEmpty()) {
                float[] coords = this.spawnLocations.get(Rnd.get((int)this.spawnLocations.size()));
                spawn = SpawnEngine.addNewSiegeSpawn(this.worldId, templateId, this.locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, coords[0], coords[1], coords[2], heading);
                Npc attaker = (Npc)SpawnEngine.spawnObject(spawn, 1);
                attaker.getSpawn().setX(x + x1);
                attaker.getSpawn().setY(y + y1);
                attaker.getSpawn().setZ(z);
                continue;
            }
            spawn = SpawnEngine.addNewSiegeSpawn(this.worldId, templateId, this.locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, x + x1, y + y1, z, heading);
            SpawnEngine.spawnObject(spawn, 1);
        }
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
				///The Dredgion has disgorged a horde of Balaur troopers.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_ABYSS_CARRIER_DROP_DRAGON, 0);
                ///The Balaur Teleport Raiders appeared.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_ABYSS_WARP_DRAGON, 120000);
            }
        });
		idList.clear();
	}
	
	private void spawnRegularBalaurs() {
        this.spawnLocations = new ArrayList<float[]>();
        List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(this.locationId);
        for (SpawnGroup2 spawnGroup : siegeSpawns) {
            for (SpawnTemplate spawnTemplate : spawnGroup.getSpawnTemplates()) {
                SiegeSpawnTemplate temp = (SiegeSpawnTemplate)spawnTemplate;
                AbyssNpcType type = DataManager.NPC_DATA.getNpcTemplate(temp.getNpcId()).getAbyssNpcType();
                if (temp.getSiegeRace() != SiegeRace.BALAUR || !temp.isPeace() || type.equals((Object)AbyssNpcType.ARTIFACT) || type.equals((Object)AbyssNpcType.TELEPORTER)) {
					continue;
				}
				float[] loc = new float[]{spawnTemplate.getX() + 2.0f, spawnTemplate.getY() + 2.0f, spawnTemplate.getZ()};
                SiegeSpawnTemplate spawn = SpawnEngine.addNewSiegeSpawn(spawnTemplate.getWorldId(), spawnTemplate.getNpcId(), this.locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, loc[0], loc[1], loc[2], spawnTemplate.getHeading());
                VisibleObject attaker = SpawnEngine.spawnObject(spawn, 1);
                if (MathUtil.isIn3dRange(attaker, this.boss, this.isBalaurea ? 100.0f : 70.0f)) {
				    this.spawnLocations.add(loc);
				}
            }
        }
    }
	
	private int getSpawnIdByFortressId() {
		switch (locationId) {
			///RESHANTA
			case 1011: //Divine Fortress.
				return 1;
			case 1131: //Siel's Western Fortress.
				return 1;
			case 1132: //Siel's Eastern Fortress.
				return 1;
			case 1141: //Sulfur Fortress.
				return 1;
			case 1211: //Roah Fortress.
			    return 1;
			case 1221: //Krotan Refuge.
				return 1;
			case 1231: //Kysis Fortress.
				return 1;
			case 1241: //Miren Fortress.
				return 1;
			case 1251: //Asteria Fortress.
				return 1;
			///INGGISON
			case 2011: //Temple Of Scales.
				return 1;
			case 2021: //Altar Of Avarice.
				return 1;
			///GELKMAROS
			case 3011: //Vorgaltem Citadel.
				return 1;
			case 3021: //Crimsom Temple.
				return 1;
			default:
				return 1;
		}
	}
	
	private List<Integer> getSpawnIds() {
		List<Integer> Spawns = new ArrayList<Integer>();
		switch (locationId) {
			/*
			case 1011: //Divine Fortress.
				Spawns.add(0);
				return Spawns;
			*/
			case 1131: //Siel's Western Fortress.
				Spawns.add(263026);
				Spawns.add(263041);
				Spawns.add(263056);
				Spawns.add(263071);
				Spawns.add(263235);
				return Spawns;
			case 1132: //Siel's Eastern Fortress.
				Spawns.add(263326);
				Spawns.add(263341);
				Spawns.add(263356);
				Spawns.add(263371);
				Spawns.add(263537);
				return Spawns;
			case 1141: //Sulfur Fortress.
				Spawns.add(263526);
				Spawns.add(263541);
				Spawns.add(263556);
				Spawns.add(263571);
				Spawns.add(264737);
				return Spawns;
			case 1211: //Roah Fortress.
				Spawns.add(266326);
				Spawns.add(266341);
				Spawns.add(266356);
				Spawns.add(266371);
				Spawns.add(266537);
				return Spawns;
			case 1221: //Krotan Refuge.
				Spawns.add(267826);
				Spawns.add(267841);
				Spawns.add(267856);
				Spawns.add(267871);
				Spawns.add(268035);
				return Spawns;
			case 1231: //Kysis Fortress.
				Spawns.add(269026);
				Spawns.add(269041);
				Spawns.add(269056);
				Spawns.add(269071);
				Spawns.add(269235);
				return Spawns;
			case 1241: //Miren Fortress.
				Spawns.add(269926);
				Spawns.add(269941);
				Spawns.add(269956);
				Spawns.add(269971);
				Spawns.add(270135);
				return Spawns;
			case 1251: //Asteria Fortress.
				Spawns.add(270826);
				Spawns.add(270841);
				Spawns.add(270856);
				Spawns.add(270871);
				Spawns.add(271037);
				return Spawns;
			case 2011: //Temple Of Scales.
				Spawns.add(257059);
				Spawns.add(257062);
				Spawns.add(257065);
				Spawns.add(257068);
				Spawns.add(257071);
				return Spawns;
			case 2021: //Altar Of Avarice.
				Spawns.add(257359);
				Spawns.add(257362);
				Spawns.add(257365);
				Spawns.add(257368);
				Spawns.add(257371);
				return Spawns;
			case 3011: //Vorgaltem Citadel.
				Spawns.add(257659);
				Spawns.add(257662);
				Spawns.add(257665);
				Spawns.add(257668);
				Spawns.add(257671);
				return Spawns;
			case 3021: //Crimsom Temple.
				Spawns.add(257959);
				Spawns.add(257962);
				Spawns.add(257965);
				Spawns.add(257968);
				Spawns.add(257971);
				return Spawns;
			default:
				return Spawns;
		}
	}
	
	private void rewardDefendingPlayers() {
	}
}