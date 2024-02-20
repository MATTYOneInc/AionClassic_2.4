package com.aionemu.gameserver.model.instance.instancereward;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instanceposition.*;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANT_DUNGEON_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;

import java.util.*;

import static ch.lambdaj.Lambda.*;

public class PvPArenaReward extends InstanceReward<PvPArenaPlayerReward>
{
	private Map<Integer, Boolean> positions = new HashMap<Integer, Boolean>();
	private FastList<Integer> zones = new FastList<Integer>();
	private int round = 1;
	private Integer zone;
	private int bonusTime;
	private int capPoints;
	private long instanceTime;
	private final byte buffId;
	protected WorldMapInstance instance;
	private GenerealInstancePosition instancePosition;
	
	public PvPArenaReward(Integer mapId, int instanceId, WorldMapInstance instance) {
		super(mapId, instanceId);
		this.instance = instance;
		boolean isSolo = isSoloArena();
		capPoints = isSolo ? 14400 : 50000;
		bonusTime = isSolo ? 8100 : 12000;
		Collections.addAll(zones, isSolo ? new Integer[]{1, 2, 3, 4} : new Integer[]{1, 2, 3, 4, 5, 6});
		int positionSize;
		if (isSolo) {
			positionSize = 4;
			buffId = 8;
			instancePosition = new DisciplineInstancePosition();
		} else if (isGlory()) {
			buffId = 7;
			positionSize = 8;
			instancePosition = new GloryInstancePosition();
		} else {
			buffId = 7;
			positionSize = 12;
			instancePosition = new ChaosInstancePosition();
		}
		instancePosition.initsialize(mapId, instanceId);
		for (int i = 1; i <= positionSize; i++) {
			positions.put(i, Boolean.FALSE);
		}
		setRndZone();
	}
	
	public final boolean isSoloArena() {
		return mapId == 300360000 || mapId == 300430000;
	}
	
	public final boolean isGlory() {
		return mapId == 300470000;
	}
	
	public int getCapPoints() {
		return capPoints;
	}
	
	public final void setRndZone() {
		int index = Rnd.get(zones.size());
		zone = zones.get(index);
		zones.remove(index);
	}
	
	private List<Integer> getFreePositions() {
		List<Integer> p = new ArrayList<Integer>();
		for (Integer key : positions.keySet()) {
			if (!positions.get(key)) {
				p.add(key);
			}
		}
		return p;
	}
	
	public synchronized void setRndPosition(Integer object) {
		PvPArenaPlayerReward reward = getPlayerReward(object);
		int position = reward.getPosition();
		if (position != 0) {
			clearPosition(position, Boolean.FALSE);
		}
		Integer key = getFreePositions().get(Rnd.get(getFreePositions().size()));
		clearPosition(key, Boolean.TRUE);
		reward.setPosition(key);
	}
	
	public synchronized void clearPosition(int position, Boolean result) {
		positions.put(position, result);
	}
	
	public int getRound() {
		return round;
	}
	
	public void setRound(int round) {
		this.round = round;
	}
	
	public void regPlayerReward(Integer object) {
		if (!containPlayer(object)) {
			addPlayerReward(new PvPArenaPlayerReward(object, bonusTime, buffId));
		}
	}
	
	@Override
	public void addPlayerReward(PvPArenaPlayerReward reward) {
		super.addPlayerReward(reward);
	}
	
	@Override
	public PvPArenaPlayerReward getPlayerReward(Integer object) {
		return (PvPArenaPlayerReward) super.getPlayerReward(object);
	}
	
	public void portToPosition(Player player) {
		Integer object = player.getObjectId();
		regPlayerReward(object);
		setRndPosition(object);
		PvPArenaPlayerReward playerReward = getPlayerReward(object);
		playerReward.applyBoostMoraleEffect(player);
		instancePosition.port(player, zone, playerReward.getPosition());
	}
	
	public List<PvPArenaPlayerReward> sortPoints() {
		return sort(getInstanceRewards(), on(PvPArenaPlayerReward.class).getScorePoints(), new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 != null ? o2.compareTo(o1) : -o1.compareTo(o2);
			}
		});
	}
	
	public int getRank(int points) {
		int rank = -1;
		for (PvPArenaPlayerReward reward : sortPoints()) {
			if (reward.getScorePoints() >= points) {
				rank++;
			}
		}
		return rank;
	}
	
	public boolean hasCapPoints() {
		if (isSoloArena() && (maxFrom(getInstanceRewards()).getPoints() - minFrom(getInstanceRewards()).getPoints() >= 1500))
			return true;
		return maxFrom(getInstanceRewards()).getPoints() >= capPoints;
	}
	
	public int getTotalPoints() {
		return sum(getInstanceRewards(), on(PvPArenaPlayerReward.class).getScorePoints());
	}
	
	public boolean canRewarded() {
		return mapId == 300350000 || mapId == 300360000 || mapId == 300420000 || mapId == 300430000 || mapId == 300470000;
	}
	
	public int getNpcBonusSkill(int npcId) {
		switch (npcId) {
			///Plaza Flame Thrower.
			case 701169:
			case 701170:
			case 701171:
			case 701172:
				return 0x4E5732;
			///Blessed Relics.
			case 701317:
				return 0x4f8532;
			///Blessed Relics.
			case 701318:
				return 0x4f8537;
			///Blessed Relics.
			case 701319:
				return 0x4f853C;
			///Blessed Relics.
			case 701220:
				return 0x4E5537;
			default:
				return 0;
		}
	}
	
	public int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (isRewarded()) {
			return 0;
		} if (result < 120000) {
			return (int) (120000 - result);
		} else {
			return (int) (180000 * getRound() - (result - 120000));
		}
	}
	
	public void setInstanceStartTime() {
		this.instanceTime = System.currentTimeMillis();
	}
	
	public void sendPacket() {
		final List<Player> players = instance.getPlayersInside();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(getTime(), getInstanceReward(), players));
			}
		});
	}
	
	public byte getBuffId() {
		return buffId;
	}
	
	@Override
	public void clear() {
		super.clear();
		positions.clear();
	}
}