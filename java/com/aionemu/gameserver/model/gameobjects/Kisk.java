	package com.aionemu.gameserver.model.gameobjects;

    import com.aionemu.gameserver.controllers.NpcController;
    import com.aionemu.gameserver.model.Race;
    import com.aionemu.gameserver.model.gameobjects.player.Player;
    import com.aionemu.gameserver.model.team.legion.Legion;
    import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
    import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
    import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
    import com.aionemu.gameserver.network.aion.serverpackets.S_PLACEABLE_BINDSTONE_INFO;
    import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
    import com.aionemu.gameserver.utils.PacketSendUtility;
    import com.aionemu.gameserver.world.World;
    import com.aionemu.gameserver.world.knownlist.Visitor;
    import javolution.util.FastList;
    import javolution.util.FastSet;

    import java.util.List;
    import java.util.Set;

public class Kisk extends SummonedObject<Player>
{
	private final Legion ownerLegion;
	private final Race ownerRace;
	private KiskStatsTemplate kiskStatsTemplate;
	private int remainingResurrections;
	private long kiskSpawnTime;
	public int KISK_LIFETIME_IN_SEC;
	private final Set<Integer> kiskMemberIds;
	
	public Kisk(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate, Player owner) {
		super(objId, controller, spawnTemplate, npcTemplate, npcTemplate.getLevel());
		this.kiskStatsTemplate = npcTemplate.getKiskStatsTemplate();
		if (this.kiskStatsTemplate == null) {
			this.kiskStatsTemplate = new KiskStatsTemplate();
		}
		this.kiskMemberIds = new FastSet<Integer>(kiskStatsTemplate.getMaxMembers());
		this.remainingResurrections = this.kiskStatsTemplate.getMaxResurrects();
		this.kiskSpawnTime = System.currentTimeMillis() / 1000;
		this.ownerLegion = owner.getLegion();
		this.ownerRace = owner.getRace();
		this.KISK_LIFETIME_IN_SEC = npcTemplate.getKiskStatsTemplate().getLiveTime();
	}
	
	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this);
	}
	
	@Override
	public boolean isEnemyFrom(Npc npc) {
		return npc.isAttackableNpc() || npc.isAggressiveTo(this);
	}
	
	@Override
	public boolean isEnemyFrom(Player player) {
		return player.getRace() != this.ownerRace;
	}
	
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.NORMAL;
	}
	
	public int getUseMask() {
		return this.kiskStatsTemplate.getUseMask();
	}
	
	public List<Player> getCurrentMemberList() {
		List<Player> currentMemberList = new FastList<Player>();
		for (int memberId : this.kiskMemberIds) {
			Player member = World.getInstance().findPlayer(memberId);
			if (member != null) {
				currentMemberList.add(member);
			}
		}
		return currentMemberList;
	}
	
	public int getCurrentMemberCount() {
		return this.kiskMemberIds.size();
	}
	
	public Set<Integer> getCurrentMemberIds() {
		return this.kiskMemberIds;
	}
	
	public int getMaxMembers() {
		return this.kiskStatsTemplate.getMaxMembers();
	}
	
	public int getRemainingResurrects() {
		return this.remainingResurrections;
	}
	
	public int getMaxRessurects() {
		return this.kiskStatsTemplate.getMaxResurrects();
	}
	
	public int getRemainingLifetime() {
		long timeElapsed = (System.currentTimeMillis() / 1000) - kiskSpawnTime;
		int timeRemaining = (int) (KISK_LIFETIME_IN_SEC - timeElapsed);
		return (timeRemaining > 0 ? timeRemaining : 0);
	}
	
	public boolean canBind(Player player) {
		if (!player.getName().equals(getMasterName())) {
			switch (this.getUseMask()) {
				case 0:
				case 1:
					if (this.ownerRace != player.getRace())
					return false;
				break;
				case 2:
					if (ownerLegion == null || !ownerLegion.isMember(player.getObjectId()))
					return false;
				break;
				case 3:
					return false;
				case 4:
					if (!player.isInTeam() || !player.getCurrentGroup().hasMember(getCreatorId()))
					return false;
				break;
				case 5:
				case 6:
					if (!player.isInTeam() || (player.isInAlliance2() && !player.getPlayerAlliance2().hasMember(getCreatorId())) || (player.isInGroup2() && !player.getPlayerGroup2().hasMember(getCreatorId())))
					return false;
				break;
				default:
					return false;
			}
		} if (this.getCurrentMemberCount() >= getMaxMembers())
			return false;
		return true;
	}
	
	public synchronized void addPlayer(Player player) {
		if (kiskMemberIds.add(player.getObjectId())) {
		  this.broadcastKiskUpdate();
		} else {
			PacketSendUtility.sendPacket(player, new S_PLACEABLE_BINDSTONE_INFO(this));
		}
		player.setKisk(this);
	}
	
	public synchronized void removePlayer(Player player) {
		player.setKisk(null);
		if (kiskMemberIds.remove(player.getObjectId())) {
		   this.broadcastKiskUpdate();
		}
	}
	
	private void broadcastKiskUpdate() {
		for (Player member : this.getCurrentMemberList()) {
			if (!this.getKnownList().knowns(member)) {
				PacketSendUtility.sendPacket(member, new S_PLACEABLE_BINDSTONE_INFO(this));
			}
		}
		final Kisk kisk = this;
		getKnownList().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player object) {
				if (object.getRace() == ownerRace) {
					PacketSendUtility.sendPacket(object, new S_PLACEABLE_BINDSTONE_INFO(kisk));
				}
			}
		});
	}
	
	public void broadcastPacket(S_MESSAGE_CODE message) {
		for (Player member : this.getCurrentMemberList()) {
			if (member != null) {
				PacketSendUtility.sendPacket(member, message);
			}
		}
	}
	
	public void resurrectionUsed() {
		remainingResurrections -= 1;
		broadcastKiskUpdate();
		if (remainingResurrections <= 0) {
			this.getController().onDelete();
		}
	}
	
	public Race getOwnerRace() {
		return this.ownerRace;
	}
	
	public boolean isActive() {
		return !this.getLifeStats().isAlreadyDead() && this.getRemainingResurrects() > 0;
	}
}