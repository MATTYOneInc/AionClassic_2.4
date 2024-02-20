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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.serial_killer.RankRestriction;
import com.aionemu.gameserver.network.aion.serverpackets.S_SERIAL_KILLER_LIST;
import com.aionemu.gameserver.services.serialkillers.SerialKiller;
import com.aionemu.gameserver.services.serialkillers.SerialKillerDebuff;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

public class SerialKillerService
{
	private FastMap<Integer, SerialKiller> serialKillers = new FastMap<Integer, SerialKiller>();
	private FastMap<Integer, FastMap<Integer, Player>> worldKillers = new FastMap<Integer, FastMap<Integer, Player>>();
	private static final FastMap<Integer, WorldType> handledWorlds = new FastMap<Integer, WorldType>();
	private int refresh = CustomConfig.SERIAL_KILLER_REFRESH;
	private int levelDiff = CustomConfig.SERIAL_KILLER_LEVEL_DIFF;
	private SerialKillerDebuff debuff;
	
	public enum WorldType {
		ASMODIANS,
		ELYOS,
		USEALL;
	}
	
	public void initSerialKillers() {
		if (!CustomConfig.SERIAL_KILLER_ENABLED) {
			return;
		} for (String world : CustomConfig.SERIAL_KILLER_WORLDS.split(",")) {
			if ("".equals(world)) {
				break;
			}
			int worldId = Integer.parseInt(world);
			int worldType = Integer.parseInt(String.valueOf(world.charAt(1)));
			debuff = new SerialKillerDebuff();
			WorldType type = worldType > 0 ? worldType > 1 ? WorldType.ASMODIANS : WorldType.ELYOS : WorldType.USEALL;
			handledWorlds.put(worldId, type);
		}
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for (SerialKiller info : serialKillers.values()) {
					if (info.victims > 0 && !isEnemyWorld(info.getOwner())) {
						info.victims -= CustomConfig.SERIAL_KILLER_DECREASE;
						int newRank = getKillerRank(info.victims);
						if (info.getRank() != newRank) {
							info.setRank(newRank);
							PacketSendUtility.sendPacket(info.getOwner(), new S_SERIAL_KILLER_LIST(true, info.getRank()));
						} if (info.victims < 1) {
							info.victims = 0;
							serialKillers.remove(info.getOwner().getObjectId());
						}
					}
				}
			}
		}, refresh * 60000, refresh * 60000);
	}
	
	public FastMap<Integer, Player> getWorldKillers(int worldId) {
		if (worldKillers.containsKey(worldId)) {
			return worldKillers.get(worldId);
		} else {
			FastMap<Integer, Player> killers = new FastMap<Integer, Player>();
			worldKillers.putEntry(worldId, killers);
			return killers;
		}
	}
	
	public void onLogin(Player player) {
		if (!CustomConfig.SERIAL_KILLER_ENABLED) {
			return;
		} if (serialKillers.containsKey(player.getObjectId())) {
			player.setSKInfo(serialKillers.get(player.getObjectId()));
			player.getSKInfo().refreshOwner(player);
		}
	}
	
	public void onLogout(Player player) {
		if (!CustomConfig.SERIAL_KILLER_ENABLED) {
			return;
		}
		onLeaveMap(player);
	}
	
	public void onEnterMap(final Player player) {
		if (!CustomConfig.SERIAL_KILLER_ENABLED) {
			return;
		}
		int worldId = player.getWorldId();
		SerialKiller info = player.getSKInfo();
		info.setRank(getKillerRank(info.victims));
		PacketSendUtility.sendPacket(player, new S_SERIAL_KILLER_LIST(false, info.getRank()));
		if (!isHandledWorld(worldId)) {
			return;
		} if (isEnemyWorld(player)) {
			int objId = player.getObjectId();
			final FastMap<Integer, Player> world = getWorldKillers(worldId);
			if (!world.containsKey(objId)) {
				world.putEntry(objId, player);
			}
			debuff.applyEffect(player, info.getRank());
			World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(player.getInstanceId()).doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player victim) {
					if (!player.getRace().equals(victim.getRace())) {
						PacketSendUtility.sendPacket(victim, new S_SERIAL_KILLER_LIST(world.values()));
					}
				}
			});
		} else {
			PacketSendUtility.sendPacket(player, new S_SERIAL_KILLER_LIST(getWorldKillers(worldId).values()));
		}
	}
	
	public void onLeaveMap(Player player) {
		int worldId = player.getWorldId();
		if (!isHandledWorld(worldId)) {
			return;
		} if (isEnemyWorld(player)) {
			SerialKiller info = player.getSKInfo();
			FastList<Player> kill = new FastList<Player>();
			FastMap<Integer, Player> killers = getWorldKillers(worldId);
			kill.addAll(killers.values());
			killers.remove(player.getObjectId());
			if (info.getRank() > 0) {
				info.setRank(0);
				debuff.endEffect(player);
				for (Player victim : World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(player.getInstanceId()).getPlayersInside()) {
					if (!player.getRace().equals(victim.getRace())) {
						PacketSendUtility.sendPacket(victim, new S_SERIAL_KILLER_LIST(kill));
					}
				}
			}
		}
	}
	
	public void updateIcons(Player player) {
		if (!isEnemyWorld(player)) {
			PacketSendUtility.sendPacket(player, new S_SERIAL_KILLER_LIST(getWorldKillers(player.getWorldId()).values()));
		}
	}
	
	public void updateRank(final Player killer, Player victim) {
		if (isEnemyWorld(killer)) {
			SerialKiller info = killer.getSKInfo();
			if (killer.getLevel() >= victim.getLevel() + levelDiff) {
				int rank = getKillerRank(++info.victims);
				if (info.getRank() != rank) {
					info.setRank(rank);
					debuff.applyEffect(killer, rank);
					final FastMap<Integer, Player> killers = getWorldKillers(killer.getWorldId());
					PacketSendUtility.sendPacket(killer, new S_SERIAL_KILLER_LIST(true, info.getRank()));
					World.getInstance().getWorldMap(killer.getWorldId()).getWorldMapInstanceById(killer.getInstanceId()).doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player observed) {
							if (!killer.getRace().equals(observed.getRace())) {
								PacketSendUtility.sendPacket(observed, new S_SERIAL_KILLER_LIST(killers.values()));
							}
						}
					});
				} if (!serialKillers.containsKey(killer.getObjectId())) {
					serialKillers.put(killer.getObjectId(), info);
				}
			}
		}
	}
	
	private int getKillerRank(int kills) {
		return kills > CustomConfig.KILLER_2ND_RANK_KILLS ? 2 : kills > CustomConfig.KILLER_1ST_RANK_KILLS ? 1 : 0;
	}
	
	public void onKillSerialKiller(final Player killer, final Player victim) {
		if (isEnemyWorld(victim)) {
			final SerialKiller info = victim.getSKInfo();
			victim.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (killer.getRace().equals(player.getRace()) && MathUtil.isIn3dRange(victim, player, 50)) {
						SkillEngine.getInstance().applyEffectDirectly(buffId(killer, info), player, player, 0);
					}
				}
			});
		}
	}
	
	public boolean isHandledWorld(int worldId) {
		return handledWorlds.containsKey(worldId);
	}
	
	public boolean isRestrictPortal(Player killer) {
        SerialKiller info = killer.getSKInfo();
        if (info.getRank() > 0) {
            RankRestriction rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(info.getRank(), killer.getRace());
            if (rankRestriction.isRestrictDirectPortal()) {
                return true;
            }
        }
        return false;
    }
	
    public boolean isRestrictDynamicBindstone(Player killer) {
        SerialKiller info = killer.getSKInfo();
        if (info.getRank() > 0) {
            RankRestriction rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(info.getRank(), killer.getRace());
            if (rankRestriction.isRestrictDynamicBindstone()) {
                return true;
            }
        }
        return false;
    }
	
	public boolean isEnemyWorld(Player player) {
		if (handledWorlds.containsKey(player.getWorldId())) {
			WorldType homeType = player.getRace().equals(Race.ASMODIANS) ? WorldType.ASMODIANS : WorldType.ELYOS;
			return !handledWorlds.get(player.getWorldId()).equals(homeType);
		}
		return false;
	}
	
	private int buffId(Player player, SerialKiller info) {
		if (info.getRank() > 0) {
			return player.getRace() == Race.ELYOS ? 8610 : 8611;
		}
		return 0;
	}
	
	public static SerialKillerService getInstance() {
		return SerialKillerService.SingletonHolder.instance;
	}
	
	private static class SingletonHolder {
		protected static final SerialKillerService instance = new SerialKillerService();
	}
}