package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.siege.OutpostLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.mail.AbyssSiegeLevel;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.services.mail.SiegeResult;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.List;

public class OutpostSiege extends Siege<OutpostLocation>
{
	private final AbyssPointsListener addAPListener = new AbyssPointsListener(this);

	public OutpostSiege(OutpostLocation siegeLocation) {
		super(siegeLocation);
	}

	@Override
	protected void onSiegeStart() {
		getSiegeLocation().setVulnerable(true);
		broadcastUpdate(getSiegeLocation());
		GlobalCallbackHelper.addCallback(addAPListener);
        deSpawnNpcs(getSiegeLocationId());
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Elyos have captured every fortress in Gelkmaros.
				//The Asmodians have captured every fortress in Inggison.
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(getSiegeLocationId() == 2111 ? 1400317 : 1400318));
			}
		});
	}

	@Override
	protected void onSiegeFinish() {
		unregisterSiegeBossListeners();
		getSiegeLocation().setVulnerable(false);
		GlobalCallbackHelper.removeCallback(addAPListener);
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(getSiegeLocationId());
		if (isBossKilled()) {
			onCapture();
			broadcastUpdate(getSiegeLocation());
		} else {
			giveRewardToDefender(loc.getRace());
            broadcastUpdate(getSiegeLocation());
        }
		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
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

	public void onCapture() {
		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(winner.getSiegeRace());
		giveRewardToWinner(winner.getSiegeRace());
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
}
