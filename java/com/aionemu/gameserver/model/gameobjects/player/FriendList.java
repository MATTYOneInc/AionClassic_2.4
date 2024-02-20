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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.network.aion.serverpackets.S_NOTIFY_BUDDY;
import com.aionemu.gameserver.network.aion.serverpackets.S_BUDDY_DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a player's Friend list
 * 
 * @author Ben
 */
public class FriendList implements Iterable<Friend> {

	private static final Logger log = LoggerFactory.getLogger(FriendList.class);
	private Status status = Status.OFFLINE;
	private volatile byte friendListSent = 0;

	private final Queue<Friend> friends;

	private Player player;

	/**
	 * Constructs an empty friend list for the given player
	 * 
	 * @param player
	 *          Player who has this friendlist
	 */
	public FriendList(Player player) {
		this(player, new ConcurrentLinkedQueue<Friend>());
	}

	/**
	 * Constructs a friend list for the given player, with the given friends
	 * 
	 * @param player
	 *          Player who has this friend list
	 * @param friends
	 *          Friends on the list
	 */
	public FriendList(Player owner, Collection<Friend> newFriends) {
		this.friends = new ConcurrentLinkedQueue<Friend>(newFriends);
		this.player = owner;
	}

	/**
	 * Gets the friend with this objId<br />
	 * Returns null if it is not our friend
	 * 
	 * @param objId
	 *          objId of friend
	 * @return Friend
	 */
	public Friend getFriend(int objId) {
		for (Friend friend : friends) {
			if (friend.getOid() == objId)
				return friend;
		}
		return null;
	}

	/**
	 * Returns number of friends in list
	 * 
	 * @return Num Friends in list
	 */
	public int getSize() {
		return friends.size();
	}

	/**
	 * Adds the given friend to the list<br />
	 * To add a friend in the database, see <tt>PlayerService</tt>
	 * 
	 * @param friend
	 */
	public void addFriend(Friend friend) {
		friends.add(friend);
	}

	/**
	 * Gets the Friend by this name
	 * 
	 * @param name
	 *          Name of friend
	 * @return Friend matching name
	 */
	public Friend getFriend(String name) {
		for (Friend friend : friends)
			if (friend.getName().equalsIgnoreCase(name))
				return friend;
		return null;
	}

	/**
	 * Deletes given friend from this friends list<br />
	 * <ul>
	 * <li>Note: This will only affect this player, not the friend.</li>
	 * <li>Note: Sends the packet to update the client automatically</li>
	 * <li>Note: You should use requestDel to delete from both lists</li>
	 * </ul>
	 * 
	 * @param friend
	 */
	public void delFriend(int friendOid) {
		Iterator<Friend> it = iterator();
		while (it.hasNext())
			if (it.next().getOid() == friendOid)
				it.remove();
	}

	public boolean isFull() {
		int MAX_FRIENDS = player.havePermission(MembershipConfig.ADVANCED_FRIENDLIST_ENABLE) ? MembershipConfig.ADVANCED_FRIENDLIST_SIZE
			: CustomConfig.FRIENDLIST_SIZE;
		return getSize() >= MAX_FRIENDS;
	}

	/**
	 * Gets players status
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status of the player<br />
	 * <ul>
	 * <li>Note: Does not update friends</li>
	 * </ul>
	 * 
	 * @param status
	 */
	public void setStatus(Status status, PlayerCommonData pcd) {
		Status previousStatus = this.status;
		this.status = status;

		for (Friend friend : friends) // For all my friends
		{
			if (friend.isOnline()) // If the player is online
			{
				Player friendPlayer = friend.getPlayer();
				if (friendPlayer == null)
					continue;

				if (friendPlayer.getClientConnection() == null){
					log.warn("[AT] friendlist connection is null");
					continue;
				}
				friendPlayer.getFriendList().getFriend(pcd.getPlayerObjId()).setPCD(pcd);
				friendPlayer.getClientConnection().sendPacket(new S_BUDDY_DATA(player.getObjectId()));

				if (previousStatus == Status.OFFLINE) {
					// Show LOGIN message
					friendPlayer.getClientConnection().sendPacket(new S_NOTIFY_BUDDY(S_NOTIFY_BUDDY.LOGIN, player.getName()));
				}
				else if (status == Status.OFFLINE) {
					// Show LOGOUT message
					friendPlayer.getClientConnection().sendPacket(new S_NOTIFY_BUDDY(S_NOTIFY_BUDDY.LOGOUT, player.getName()));
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Friend> iterator() {
		return friends.iterator();
	}

	public boolean getIsFriendListSent() {
		return friendListSent == 1;
	}

	public void setIsFriendListSent(boolean value) {
		this.friendListSent = (byte) (value ? 1 : 0);
	}

	public enum Status {
		/**
		 * User is offline or invisible
		 */
		OFFLINE((byte) 0),
		/**
		 * User is online
		 */
		ONLINE((byte) 1),
		/**
		 * User is away or busy
		 */
		AWAY((byte) 3);

		byte value;

		private Status(byte value) {
			this.value = value;
		}

		public byte getId() {
			return value;
		}

		/**
		 * Gets the Status from its int value<br />
		 * Returns null if out of range
		 * 
		 * @param value
		 *          range 0-3
		 * @return Status
		 */
		public static Status getByValue(byte value) {
			for (Status stat : values())
				if (stat.getId() == value)
					return stat;
			return null;
		}
	}
}
