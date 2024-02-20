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
package ai.siege;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.ArtifactStatus;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.templates.siegelocation.ArtifactActivation;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ARTIFACT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_GAUGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.properties.TargetSpeciesAttribute;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("artifact")
public class ArtifactAI2 extends NpcAI2
{
	private Map<Integer, ItemUseObserver> observers = new HashMap<Integer, ItemUseObserver>();
	
	@Override
	protected SiegeSpawnTemplate getSpawnTemplate() {
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}
	
	@Override
	protected void handleDialogStart(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		AI2Actions.addRequest(this, player, 160028, new AI2Request() {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				AI2Actions.addRequest(ArtifactAI2.this, player, 160016, new AI2Request() {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (MathUtil.isInRange(requester, responder, 10)) {
						    onActivate(responder);
						} else {
						    PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_CANNOT_USE_ARTIFACT_FAR_FROM_NPC);
							return;
						}
					}
				}, new DescriptionId(2 * 716570 + 1), SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId()).getTemplate().getActivation().getCount());
			}
		}, loc);
	}
	
	@Override
	protected void handleDialogFinish(Player player) {
	}
	
	public void onActivate(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		ArtifactActivation activation = loc.getTemplate().getActivation();
		int skillId = activation.getSkillId();
		final int itemId = activation.getItemId();
		final int count = activation.getCount();
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (skillTemplate == null) {
			LoggerFactory.getLogger(ArtifactAI2.class).error("No skill template for <Artifact Id>: " + skillId);
			return;
		} if (loc.getCoolDown() > 0 || !loc.getStatus().equals(ArtifactStatus.IDLE)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ARTIFACT_OUT_OF_ORDER);
			return;
		} if (loc.getLegionId() != 0) {
			if (!player.isLegionMember() || player.getLegion().getLegionId() != loc.getLegionId() || !player.getLegionMember().hasRights(LegionPermissionsMask.ARTIFACT)) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ARTIFACT_HAVE_NO_AUTHORITY);
				return;
			}
		} if (player.getInventory().getItemCountByItemId(itemId) < count) {
			return;
		} if (!loc.getStatus().equals(ArtifactStatus.IDLE)) {
			return;
		}
		final S_MESSAGE_CODE startMessage = S_MESSAGE_CODE.STR_ARTIFACT_CASTING(player.getRace().getRaceDescriptionId(), player.getName(), new DescriptionId(skillTemplate.getNameId()));
		loc.setStatus(ArtifactStatus.ACTIVATION);
		final S_ARTIFACT_INFO artifactInfo = new S_ARTIFACT_INFO(loc.getLocationId());
		player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, startMessage);
				PacketSendUtility.sendPacket(player, artifactInfo);
			}
		});
		PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), 10000, 1));
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
		ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
				PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), 10000, 0));
				final S_MESSAGE_CODE message = S_MESSAGE_CODE.STR_ARTIFACT_CANCELED(loc.getRace().getDescriptionId(), new DescriptionId(skillTemplate.getNameId()));
				loc.setStatus(ArtifactStatus.IDLE);
				final S_ARTIFACT_INFO artifactInfo = new S_ARTIFACT_INFO(loc.getLocationId());
				getOwner().getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}
				});
			}
		};
		observers.put(player.getObjectId(), observer);
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				ItemUseObserver observer = observers.remove(player.getObjectId());
				if (observer != null) {
					player.getObserveController().removeObserver(observer);
				}
				PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), 10000, 0));
				PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				if (!player.getInventory().decreaseByItemId(itemId, count)) {
					return;
				}
				final S_MESSAGE_CODE message = S_MESSAGE_CODE.STR_ARTIFACT_CORE_CASTING(loc.getRace().getDescriptionId(), new DescriptionId(skillTemplate.getNameId()));
				loc.setStatus(ArtifactStatus.CASTING);
				final S_ARTIFACT_INFO artifactInfo = new S_ARTIFACT_INFO(loc.getLocationId());
				player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}
				});
				loc.setLastActivation(System.currentTimeMillis());
				if (loc.getTemplate().getRepeatCount() == 1) {
					ThreadPoolManager.getInstance().schedule(new ArtifactUseSkill(loc, player, skillTemplate), 13000);
				} else {
					final ScheduledFuture<?> s = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ArtifactUseSkill(loc, player, skillTemplate), 13000, loc.getTemplate().getRepeatInterval() * 1000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							s.cancel(true);
							loc.setStatus(ArtifactStatus.IDLE);
						}
					}, 13000 + (loc.getTemplate().getRepeatInterval() * loc.getTemplate().getRepeatCount() * 1000));
				}
			}
		}, 10000));
	}
	
	class ArtifactUseSkill implements Runnable {
		private ArtifactLocation artifact;
		private Player player;
		private SkillTemplate skill;
		private int runCount = 1;
		private S_ARTIFACT_INFO pkt;
		private S_MESSAGE_CODE message;
		
		private ArtifactUseSkill(ArtifactLocation artifact, Player activator, SkillTemplate skill) {
			this.artifact = artifact;
			this.player = activator;
			this.skill = skill;
			this.pkt = new S_ARTIFACT_INFO(artifact.getLocationId());
			this.message = S_MESSAGE_CODE.STR_ARTIFACT_FIRE(activator.getRace().getRaceDescriptionId(), player.getName(), new DescriptionId(skill.getNameId()));
		}
		
		@Override
		public void run() {
			if (artifact.getTemplate().getRepeatCount() < runCount) {
				return;
			}
			final boolean start = (runCount == 1);
			final boolean end = (runCount == artifact.getTemplate().getRepeatCount());
			runCount++;
			player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (start) {
						PacketSendUtility.sendPacket(player, message);
					    artifact.setStatus(ArtifactStatus.ACTIVATED);
						PacketSendUtility.sendPacket(player, pkt);
					} if (end) {
						artifact.setStatus(ArtifactStatus.IDLE);
						PacketSendUtility.sendPacket(player, pkt);
					}
				}
			});
			boolean pc = skill.getProperties().getTargetSpecies() == TargetSpeciesAttribute.PC;
			for (Creature creature : artifact.getCreatures().values()) {
				if (creature.getActingCreature() instanceof Player || (creature instanceof SiegeNpc && !pc)) {
					switch (skill.getProperties().getTargetRelation()) {
						case FRIEND:
							if (player.isEnemy(creature)) {
								continue;
							}
						break;
						case ENEMY:
							if (!player.isEnemy(creature)) {
								continue;
							}
						break;
					}
					AI2Actions.applyEffect(ArtifactAI2.this, skill, creature);
				}
			}
		}
	}
}