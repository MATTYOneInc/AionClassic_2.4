package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.*;
import com.aionemu.gameserver.model.instance.playerreward.*;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import javolution.util.FastList;

@SuppressWarnings("rawtypes")
public class S_INSTANT_DUNGEON_INFO extends AionServerPacket
{
	private final Logger log = LoggerFactory.getLogger(S_INSTANT_DUNGEON_INFO.class);
	
	private int type;
	private int mapId;
	private int instanceTime;
	private InstanceScoreType instanceScoreType;
	private InstanceReward instanceReward;
	private List<Player> players;
	private Integer object;
	private int PlayerStatus = 0;
	private int PlayerRaceId = 0;
	private Player player;
	private Player opponent;
	
	public S_INSTANT_DUNGEON_INFO(int type, int instanceTime, InstanceReward instanceReward, Integer object, int PlayerStatus, int PlayerRaceId) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        this.PlayerStatus = PlayerStatus;
        this.PlayerRaceId = PlayerRaceId;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

	public S_INSTANT_DUNGEON_INFO(int type, int instanceTime, InstanceReward instanceReward, Integer object) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

	public S_INSTANT_DUNGEON_INFO(int instanceTime, InstanceReward instanceReward, List<Player> players) {
        this.mapId = instanceReward.getMapId();
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.players = players;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }

	public S_INSTANT_DUNGEON_INFO(int type, int instanceTime, InstanceReward instanceReward, List<Player> players, boolean tis) {
		this.mapId = instanceReward.getMapId();
		this.type = type;
		this.instanceTime = instanceTime;
		this.instanceReward = instanceReward;
		this.players = players;
		instanceScoreType = instanceReward.getInstanceScoreType();
	}

	public S_INSTANT_DUNGEON_INFO(InstanceReward instanceReward, InstanceScoreType instanceScoreType) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceScoreType;
    }

	public S_INSTANT_DUNGEON_INFO(InstanceReward instanceReward) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceReward.getInstanceScoreType();
    }
	
	public S_INSTANT_DUNGEON_INFO(int type, Player player, int instanceTime, InstanceReward instanceReward) {
		this.mapId = instanceReward.getMapId();
		this.type = type;
		this.player = player;
		this.instanceTime = instanceTime;
		this.instanceReward = instanceReward;
		instanceScoreType = instanceReward.getInstanceScoreType();
	}
	
	public S_INSTANT_DUNGEON_INFO(int type, Player player, Player opponent, int instanceTime, InstanceReward instanceReward) {
		this.mapId = instanceReward.getMapId();
		this.type = type;
		this.player = player;
		this.opponent = opponent;
		this.instanceTime = instanceTime;
		this.instanceReward = instanceReward;
		instanceScoreType = instanceReward.getInstanceScoreType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void writeImpl(AionConnection con) {
		int playerCount = 0;
		Player owner = con.getActivePlayer();
		Integer ownerObject = owner.getObjectId();
		writeD(mapId);
		writeD(instanceTime);
		writeD(instanceScoreType.getId());
		switch (mapId) {
			case 300040000: //Dark Poeta.
				DarkPoetaReward dpr = (DarkPoetaReward) instanceReward;
				writeD(dpr.getPoints());
				writeD(dpr.getNpcKills());
				writeD(dpr.getGatherCollections());
				writeD(dpr.getRank());
			break;
			case 300500000: //Tempus 2.5
			case 300510000: //Tempus [Quest] 2.5
				TempusReward tr = (TempusReward) instanceReward;
				writeD(tr.getPoints());
				writeD(tr.getNpcKills());
				writeD(tr.getRank());
			break;
			case 300110000: //Baranath Dredgion.
			case 300210000: //Chantra Dredgion.
				fillTableWithGroup(Race.ELYOS);
				fillTableWithGroup(Race.ASMODIANS);
				DredgionReward dredgionReward = (DredgionReward) instanceReward;
				int elyosScore = dredgionReward.getPointsByRace(Race.ELYOS).intValue();
				int asmosScore = dredgionReward.getPointsByRace(Race.ASMODIANS).intValue();
				writeD(instanceScoreType.isEndProgress() ? (asmosScore > elyosScore ? 1 : 0) : 255);
				writeD(elyosScore);
				writeD(asmosScore);
				for (DredgionReward.DredgionRooms dredgionRoom : dredgionReward.getDredgionRooms()) {
					writeC(dredgionRoom.getState());
				}
			break;
			case 300440000: //Tiak Research Base.
				fillTiakTableWithGroup(Race.ELYOS);
				fillTiakTableWithGroup(Race.ASMODIANS);
				TiakReward tiakReward = (TiakReward) instanceReward;
				int elyosTiakScore = tiakReward.getPointsByRace(Race.ELYOS).intValue();
				int asmosTiakScore = tiakReward.getPointsByRace(Race.ASMODIANS).intValue();
				writeD(instanceScoreType.isEndProgress() ? (asmosTiakScore > elyosTiakScore ? 1 : 0) : 255);
				writeD(elyosTiakScore);
				writeD(asmosTiakScore);
				for (TiakReward.TiakRooms tiakRoom : tiakReward.getTiakRooms()) {
					writeC(tiakRoom.getState());
				}
			break;
			case 300300000: //Empyrean Crucible.
			case 300320000: //Empyrean Crucible Challenge.
				for (CruciblePlayerReward playerReward : (FastList<CruciblePlayerReward>) instanceReward.getInstanceRewards()) {
					writeD(playerReward.getOwner());
					writeD(playerReward.getPoints());
					writeD(instanceScoreType.isEndProgress() ? 3 : 1);
					if (mapId == 300300000) {
						if (playerReward.getInsignia() != 0) {
							writeD(186000169);
							writeD(playerReward.getInsignia());
						}
					} if (mapId == 300320000) {
						if (playerReward.getIDArenaSoloReward01() != 0) {
							writeD(188052934);
							writeD(playerReward.getIDArenaSoloReward01());
							writeD(166100015);
							writeD(playerReward.getLesserSupplementEternal());
						} if (playerReward.getIDArenaSoloReward02() != 0) {
							writeD(188052935);
							writeD(playerReward.getIDArenaSoloReward02());
							writeD(166100015);
							writeD(playerReward.getLesserSupplementEternal());
						} if (playerReward.getIDArenaSoloLucky() != 0) {
							writeD(188052937);
							writeD(playerReward.getIDArenaSoloLucky());
							writeD(166100015);
							writeD(playerReward.getLesserSupplementEternal());
						}
					}
					playerCount++;
				} if (playerCount < 6) {
					writeB(new byte[16 * (6 - playerCount)]);
				}
			break;
			case 300350000: //Arena Of Chaos.
			case 300360000: //Arena Of Discipline.
			case 300420000: //Chaos Training Grounds.
			case 300430000: //Discipline Training Grounds.
			case 300470000: //Arena Of Glory.
				PvPArenaReward arenaReward = (PvPArenaReward) instanceReward;
				PvPArenaPlayerReward rewardedPlayer = arenaReward.getPlayerReward(ownerObject);
				boolean isRewarded = arenaReward.isRewarded();
				for (Player player: players) {
					PvPArenaPlayerReward reward;
                    PvPArenaPlayerReward playerReward = reward = arenaReward.getPlayerReward(player.getObjectId());
					int points = playerReward.getPoints();
                    int rank = arenaReward.getRank(playerReward.getScorePoints());
					writeD(playerReward.getOwner());
					writeD(playerReward.getPvPKills());
					writeD(isRewarded ? points + playerReward.getTimeBonus() : points);
					writeC(player.getPlayerClass().getClassId());
					writeD(0x00);
					writeC(player.getPlayerClass().getClassId());
					writeC(0x00);
					writeC(rank);
					writeD(0x0F);
					if (instanceScoreType == InstanceScoreType.END_PROGRESS) {
						writeD(rank);
						writeD(0x00);
					} else {
						writeD(0x00);
						writeD(0x00);
					}
					writeQ(0x00);
					writeS(player.getName());
					int spaces = (player.getName().length() * 2) + 2;
					if (spaces < 52) {
						writeB(new byte[(52 - spaces)]);
					}
					playerCount++;
                } if (playerCount < 12) {
                    writeB(new byte[92 * (12 - playerCount)]);
                } if (instanceScoreType == InstanceScoreType.END_PROGRESS) {
                    writeD(rewardedPlayer.getScoreAP());
                    writeD(186000169);
                    writeD(rewardedPlayer.getScoreCrucible());
                    writeD(186000170);
                    writeD(rewardedPlayer.getScoreCourage());
					writeD(0);
                    writeD(0);
					writeD(0);
					writeD(0);
                    writeB(new byte[16]);
                } else {
                    writeB(new byte[36]);
                }
                writeD(8);
                writeD(0);
                writeD(arenaReward.getRound());
                writeD(arenaReward.getCapPoints());
                writeD(3);
                writeD(1);
            break;
		}
	}
	
	private void fillTableWithGroup(Race race) {
		int count = 0;
		DredgionReward dredgionReward = (DredgionReward) instanceReward;
		for (Player player : players) {
			if (!race.equals(player.getRace())) {
				continue;
			}
			InstancePlayerReward playerReward = dredgionReward.getPlayerReward(player.getObjectId());
			DredgionPlayerReward dpr = (DredgionPlayerReward) playerReward;
			writeD(playerReward.getOwner());
			writeD(player.getAbyssRank().getRank().getId());
			writeD(dpr.getPvPKills());
			writeD(dpr.getMonsterKills());
			writeD(dpr.getZoneCaptured());
			writeD(dpr.getPoints());
			if (instanceScoreType.isEndProgress()) {
				boolean winner = race.equals(dredgionReward.getWinningRace());
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()) + (int) (dpr.getPoints() * 1.6f));
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()));
			} else {
				writeB(new byte[8]);
			}
			writeC(player.getPlayerClass().getClassId());
			writeC(0);
			writeS(player.getName(), 54);
			count++;
		} if (count < 6) {
			writeB(new byte[88 * (6 - count)]);
		}
	}
	
	private void fillTiakTableWithGroup(Race race) {
		int count = 0;
		TiakReward tiakReward = (TiakReward) instanceReward;
		for (Player player : players) {
			if (!race.equals(player.getRace())) {
				continue;
			}
			InstancePlayerReward playerReward = tiakReward.getPlayerReward(player.getObjectId());
			TiakPlayerReward tpr = (TiakPlayerReward) playerReward;
			writeD(playerReward.getOwner());
			writeD(player.getAbyssRank().getRank().getId());
			writeD(tpr.getPvPKills());
			writeD(tpr.getMonsterKills());
			writeD(tpr.getZoneCaptured());
			writeD(tpr.getPoints());
			if (instanceScoreType.isEndProgress()) {
				boolean winner = race.equals(tiakReward.getWinningRace());
				writeD((winner ? tiakReward.getWinnerPoints() : tiakReward.getLooserPoints()) + (int) (tpr.getPoints() * 1.6f));
				writeD((winner ? tiakReward.getWinnerPoints() : tiakReward.getLooserPoints()));
			} else {
				writeB(new byte[8]);
			}
			writeC(player.getPlayerClass().getClassId());
			writeC(0);
			writeS(player.getName(), 54);
			count++;
		} if (count < 6) {
			writeB(new byte[88 * (6 - count)]);
		}
	}
}