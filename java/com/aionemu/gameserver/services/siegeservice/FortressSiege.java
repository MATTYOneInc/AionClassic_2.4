package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.siege.*;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.mail.AbyssSiegeLevel;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.services.mail.SiegeResult;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FortressSiege extends Siege<FortressLocation>
{
	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final AbyssPointsListener addAPListener = new AbyssPointsListener(this);

	public FortressSiege(FortressLocation fortress) {
		super(fortress);
	}

	@Override
	public void onSiegeStart() {
		getSiegeLocation().setVulnerable(true);
		getSiegeLocation().setUnderShield(true);
		broadcastState(getSiegeLocation());
		getSiegeLocation().clearLocation();
		GlobalCallbackHelper.addCallback(addAPListener);
		deSpawnNpcs(getSiegeLocationId());
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(getSiegeLocationId());
		final DescriptionId nameId = new DescriptionId(loc.getTemplate().getNameId());
		if (getSiegeLocation().getOccupyCount() >= 2) {
            resetToBalaur();
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					///%0 is marked by the Balaur.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1401588, nameId));
					///%0 fell to the Balaur&apos;s attack.\nFortify and fight for your faction!
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1401589, nameId));
				}
			});
        }
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();
	}

	@Override
	public void onSiegeFinish() {
		unregisterSiegeBossListeners();
		getSiegeLocation().setVulnerable(false);
		getSiegeLocation().setUnderShield(false);
		GlobalCallbackHelper.removeCallback(addAPListener);
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(getSiegeLocationId());
		if (isBossKilled()) {
			onCapture();
			applyBuff();
			broadcastUpdate(getSiegeLocation());
			getSiegeLocation().setOccupyCount(getSiegeLocation().getOccupyCount() + 1);
		} else {
		    giveRewardToDefender(loc.getRace());
			broadcastState(getSiegeLocation());
		}
		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
		SiegeService.getInstance().updateOutpostStatusByFortress(getSiegeLocation());
		SiegeDAO.updateSiegeLocation(getSiegeLocation());
		getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.unsetInsideZoneType(ZoneType.SIEGE);
				player.getController().updateZone();
			    player.getController().updateNearbyQuests();
				if (isBossKilled() && (SiegeRace.getByRace(player.getRace()) == getSiegeLocation().getRace())) {
					QuestEngine.getInstance().onKill(new QuestEnv(getBoss(), player, 0, 0));
				}
			}
		});
	}

	private void resetToBalaur() {
        getSiegeLocation().setRace(SiegeRace.BALAUR);
        getSiegeLocation().setLegionId(0);
        getArtifact().setLegionId(0);
        getSiegeLocation().setOccupyCount(0);
        SiegeDAO.updateSiegeLocation(getSiegeLocation());
        broadcastUpdate(getSiegeLocation());
        onSiegeStart();
    }

	public void onCapture() {
		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());
		if (SiegeRace.BALAUR == winner.getSiegeRace()) {
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		} else {
			Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
            giveRewardToWinner(winner.getSiegeRace());
		}
	}

	public void applyBuff() {
		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());
		if (SiegeRace.BALAUR == winner.getSiegeRace()) {
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		} else {
			Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
		}
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Buff for Both Race.
				if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffId())) {
		    		player.getEffectController().removeEffect(getSiegeLocation().getBuffId());
				} else {
					SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffId(), player, player, 0);
				}
				//Buff for Asmodians or Elyos.
				if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffIdA())) {
		    		player.getEffectController().removeEffect(getSiegeLocation().getBuffIdA());
				} if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffIdE())) {
		    		player.getEffectController().removeEffect(getSiegeLocation().getBuffIdE());
				} if (player.getCommonData().getRace() == Race.ASMODIANS) {
				    SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffIdA(), player, player, 0);
				} if (player.getCommonData().getRace() == Race.ELYOS) {
				    SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffIdE(), player, player, 0);
				}
			}
		});
	}

	@Override
	public boolean isEndless() {
		return false;
	}

	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
		getSiegeCounter().addAbyssPoints(player, abysPoints);
	}

	protected void giveRewardToWinner(final SiegeRace winner) {
        final List<SiegeReward> playerRewards = getSiegeLocation().getReward();
        final SiegeLocation location = getSiegeLocation();
        getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace().getRaceId() == winner.getRaceId()) {
                    for (SiegeReward reward: playerRewards) {
                        Integer playerId = player.getObjectId();
                        PlayerCommonData pcd = PlayerDAO.loadPlayerCommonData(playerId);
						if (reward.getResultType() == SiegeResult.OCCUPY) {
							///[Reward EXP]
							int expCount = reward.getExpCount();
							if (expCount != 0) {
								player.getCommonData().addExp(expCount, RewardType.QUEST);
							}
							///[Reward AP]
							int apCount = reward.getApCount();
							if (apCount != 0) {
								AbyssPointsService.addAp(player, apCount);
							}
							///[Reward MAIL]
						    MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.OCCUPY, System.currentTimeMillis(), reward.getItemId(), reward.getItemCount(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.OCCUPY, System.currentTimeMillis(), reward.getItemId2(), reward.getItemCount2(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.OCCUPY, System.currentTimeMillis(), 0, 0, reward.getKinahCount());
                        }
                    }
                } else {
                    for (SiegeReward reward: playerRewards) {
                        Integer playerId = player.getObjectId();
                        PlayerCommonData pcd = PlayerDAO.loadPlayerCommonData(playerId);
                        if (reward.getResultType() == SiegeResult.FAIL) {
							///[Reward EXP]
							int expCount = reward.getExpCount();
							if (expCount != 0) {
								player.getCommonData().addExp(expCount, RewardType.QUEST);
							}
							///[Reward AP]
							int apCount = reward.getApCount();
							if (apCount != 0) {
								AbyssPointsService.addAp(player, apCount);
							}
							///[Reward MAIL]
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), reward.getItemId(), reward.getItemCount(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), reward.getItemId2(), reward.getItemCount2(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), 0, 0, reward.getKinahCount());
                        }
                    }
                }
            }
        });
    }

    protected void giveRewardToDefender(final SiegeRace winner) {
        final List<SiegeReward> playerRewards = getSiegeLocation().getReward();
        final SiegeLocation location = getSiegeLocation();
        getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace().getRaceId() == winner.getRaceId()) {
                    for (SiegeReward reward: playerRewards) {
                        Integer playerId = player.getObjectId();
                        PlayerCommonData pcd = PlayerDAO.loadPlayerCommonData(playerId);
						if (reward.getResultType() == SiegeResult.DEFENDER) {
							///[Reward EXP]
							int expCount = reward.getExpCount();
							if (expCount != 0) {
								player.getCommonData().addExp(expCount, RewardType.QUEST);
							}
							///[Reward AP]
							int apCount = reward.getApCount();
							if (apCount != 0) {
								AbyssPointsService.addAp(player, apCount);
							}
							///[Reward MAIL]
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.DEFENDER, System.currentTimeMillis(), reward.getItemId(), reward.getItemCount(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.DEFENDER, System.currentTimeMillis(), reward.getItemId2(), reward.getItemCount2(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.DEFENDER, System.currentTimeMillis(), 0, 0, reward.getKinahCount());
                        }
                    }
                } else {
                    for (SiegeReward reward: playerRewards) {
                        Integer playerId = player.getObjectId();
                        PlayerCommonData pcd = PlayerDAO.loadPlayerCommonData(playerId);
                        if (reward.getResultType() == SiegeResult.FAIL) {
							///[Reward EXP]
							int expCount = reward.getExpCount();
							if (expCount != 0) {
								player.getCommonData().addExp(expCount, RewardType.QUEST);
							}
							///[Reward AP]
							int apCount = reward.getApCount();
							if (apCount != 0) {
								AbyssPointsService.addAp(player, apCount);
							}
							///[Reward MAIL]
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), reward.getItemId(), reward.getItemCount(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), reward.getItemId2(), reward.getItemCount2(), 0);
                            MailFormatter.sendAbyssRewardMail(location, pcd, AbyssSiegeLevel.VETERAN_SOLDIER, SiegeResult.FAIL, System.currentTimeMillis(), 0, 0, reward.getKinahCount());
                        }
                    }
                }
            }
        });
    }

	protected VisibleObject spawn(int worldId, int npcId, float x, float y, float z, byte heading, int entityId, int instanceId) {
        SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
        template.setEntityId(entityId);
        return SpawnEngine.spawnObject(template, instanceId);
    }

	protected ArtifactLocation getArtifact() {
		return SiegeService.getInstance().getFortressArtifacts().get(getSiegeLocationId());
	}

	protected boolean hasArtifact() {
		return getArtifact() != null;
	}
}
