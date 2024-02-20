package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_SEARCH_USER_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CM_PLAYER_SEARCH extends AionClientPacket
{
	public static final int MAX_RESULTS = 104;
	private String name;
	private int region;
	private int classMask;
	private int minLevel;
	private int maxLevel;
	private int lfgOnly;
	
	public CM_PLAYER_SEARCH(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		name = readS(52);
		if (name != null) {
			name = Util.convertName(name);
		}
		region = readD();
		classMask = readD();
		minLevel = readC();
		maxLevel = readC();
		lfgOnly = readC();
		readC();
	}
	
	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer == null || !activePlayer.isSpawned()) {
            return;
        } if (activePlayer.isProtectionActive()) {
            activePlayer.getController().stopProtectionActiveTask();
        } if (activePlayer.isCasting()) {
            activePlayer.getController().cancelCurrentSkill();
        } if (activePlayer.getController().isInShutdownProgress()) {
            return;
        }
		Iterator<Player> it = World.getInstance().getPlayersIterator();
		List<Player> matches = new ArrayList<Player>(MAX_RESULTS);
		if (activePlayer.getLevel() < 9) {
			//Characters under level 9 cannot use the search function.
			PacketSendUtility.sendPacket(activePlayer, S_MESSAGE_CODE.STR_CANT_WHO_LEVEL("9"));
			return;
		} while (it.hasNext() && matches.size() < MAX_RESULTS) {
			Player player = it.next();
			if (!player.isSpawned()) {
				continue;
			} else if (player.getFriendList().getStatus() == Status.OFFLINE) {
				continue;
			} else if (player.isGM() && !CustomConfig.SEARCH_GM_LIST) {
				continue;
			} else if (lfgOnly == 1 && !player.isLookingForGroup()) {
				continue;
			} else if (!name.isEmpty() && !player.getName().toLowerCase().contains(name.toLowerCase())) {
				continue;
			} else if (minLevel != 0xFF && player.getLevel() < minLevel) {
				continue;
			} else if (maxLevel != 0xFF && player.getLevel() > maxLevel) {
				continue;
			} else if (classMask > 0 && (player.getPlayerClass().getMask() & classMask) == 0) {
				continue;
			} else if (region > 0 && player.getActiveRegion().getMapId() != region) {
				continue;
			} else if ((player.getRace() != activePlayer.getRace()) && (CustomConfig.FACTIONS_SEARCH_MODE == false)) {
				continue;
			} else {
				matches.add(player);
			}
		}
		sendPacket(new S_SEARCH_USER_RESULT(matches, region));
	}
}