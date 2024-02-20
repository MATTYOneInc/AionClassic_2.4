package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArtifactSiege extends Siege<ArtifactLocation>
{
	private static final Logger log = LoggerFactory.getLogger(ArtifactSiege.class.getName());

	public ArtifactSiege(ArtifactLocation siegeLocation) {
		super(siegeLocation);
	}

	@Override
	protected void onSiegeStart() {
		initSiegeBoss();
	}

	@Override
	protected void onSiegeFinish() {
		unregisterSiegeBossListeners();
		deSpawnNpcs(getSiegeLocationId());
		if (isBossKilled()) {
			onCapture();
		} else {
            log.error("Artifact siege (artifactId:" + getSiegeLocationId() + ") ended without killing a boss.");
        }
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
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
		broadcastUpdate(getSiegeLocation());
		startSiege(getSiegeLocationId());
	}

	protected void onCapture() {
		SiegeRaceCounter wRaceCounter = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(wRaceCounter.getSiegeRace());
		Integer wLegionId = wRaceCounter.getWinnerLegionId();
		getSiegeLocation().setLegionId(wLegionId != null ? wLegionId : 0);
		final Race wRace = wRaceCounter.getSiegeRace() == SiegeRace.ELYOS ? Race.ELYOS : Race.ASMODIANS;
		Legion wLegion = wLegionId != null ? LegionService.getInstance().getLegion(wLegionId) : null;
		final DescriptionId nameId = new DescriptionId(getSiegeLocation().getTemplate().getNameId());
		//%0 has captured %1.
		final AionServerPacket wRacePacket = new S_MESSAGE_CODE(1390203, wRace.getRaceDescriptionId(), nameId);
		//%0 has lost %1.
		final AionServerPacket lRacePacket = new S_MESSAGE_CODE(1390204, wRace.getRaceDescriptionId(), nameId);
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, player.getRace().equals(wRace) ? wRacePacket : lRacePacket);
			}
		});
	}

	@Override
	public boolean isEndless() {
		return true;
	}

	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
	}
}
