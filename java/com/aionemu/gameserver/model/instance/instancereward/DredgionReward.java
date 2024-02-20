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
package com.aionemu.gameserver.model.instance.instancereward;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.instance.playerreward.DredgionPlayerReward;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import javolution.util.FastList;
import org.apache.commons.lang.mutable.MutableInt;

/****/
/** Author Rinzler (Encom)
/****/

public class DredgionReward extends InstanceReward<DredgionPlayerReward>
{
	private int winnerPoints;
	private int looserPoints;
	@SuppressWarnings("unused")
	private int drawPoins;
	private MutableInt asmodiansPoints = new MutableInt(0);
	private MutableInt elyosPoins = new MutableInt(0);
	private Race race;
	private FastList<DredgionRooms> dredgionRooms = new FastList<DredgionRooms>();
	private Point3D asmodiansStartPosition;
	private Point3D elyosStartPosition;
	
	public DredgionReward(Integer mapId, int instanceId) {
		super(mapId, instanceId);
		winnerPoints = mapId == 300110000 ? 3000 : 4500;
		looserPoints = mapId == 300110000 ? 1500 : 2500;
		drawPoins = mapId == 300110000 ? 2250 : 3750;
		setStartPositions();
		for (int i = 1; i < 15; i++) {
			dredgionRooms.add(new DredgionRooms(i));
		}
	}
	
	private void setStartPositions() {
		Point3D a = new Point3D(570.0000f, 166.0000f, 432.0000f);
		Point3D b = new Point3D(400.0000f, 166.0000f, 432.0000f);
		switch (Rnd.get(1, 2)) {
		    case 1:
				asmodiansStartPosition = a;
			    elyosStartPosition = b;
			break;
			case 2:
				asmodiansStartPosition = b;
			    elyosStartPosition = a;
			break;
		}
	}
	
	public void portToPosition(Player player) {
		if (player.getRace() == Race.ASMODIANS) {
			TeleportService2.teleportTo(player, mapId, instanceId, asmodiansStartPosition.getX(), asmodiansStartPosition.getY(), asmodiansStartPosition.getZ());
		} else {
			TeleportService2.teleportTo(player, mapId, instanceId, elyosStartPosition.getX(), elyosStartPosition.getY(), elyosStartPosition.getZ());
		}
	}
	
	public class DredgionRooms {
		private int roomId;
		private int state = 0xFF;
		
		public DredgionRooms(int roomId) {
			this.roomId = roomId;
		}
		
		public int getRoomId() {
			return roomId;
		}
		
		public void captureRoom(Race race) {
			state = race.equals(Race.ASMODIANS) ? 0x01 : 0x00;
		}
		
		public int getState() {
			return state;
		}
	}
	
	public FastList<DredgionRooms> getDredgionRooms() {
		return dredgionRooms;
	}
	
	public DredgionRooms getDredgionRoomById(int roomId) {
		for (DredgionRooms dredgionRoom : dredgionRooms) {
			if (dredgionRoom.getRoomId() == roomId) {
				return dredgionRoom;
			}
		}
		return null;
	}
	
	public MutableInt getPointsByRace(Race race) {
		switch (race) {
			case ELYOS:
				return elyosPoins;
			case ASMODIANS:
				return asmodiansPoints;
		}
		return null;
	}
	
	public void addPointsByRace(Race race, int points) {
		MutableInt racePoints = getPointsByRace(race);
		racePoints.add(points);
		if (racePoints.intValue() < 0) {
			racePoints.setValue(0);
		}
	}
	
	public int getLooserPoints() {
		return looserPoints;
	}
	
	public int getWinnerPoints() {
		return winnerPoints;
	}
	
	public void setWinningRace(Race race) {
		this.race = race;
	}
	
	public Race getWinningRace() {
		return race;
	}
	
	public Race getWinningRaceByScore() {
		return asmodiansPoints.compareTo(elyosPoins) > 0 ? Race.ASMODIANS : Race.ELYOS;
	}
	
	@Override
	public void clear() {
		super.clear();
		dredgionRooms.clear();
	}
}