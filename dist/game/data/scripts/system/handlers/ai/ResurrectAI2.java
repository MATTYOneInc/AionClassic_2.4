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
package ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.PlayerBindPointDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("resurrect")
public class ResurrectAI2 extends NpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	private static Logger log = LoggerFactory.getLogger(ResurrectAI2.class);

	@Override
	public boolean canThink() {
		return canThink;
	}

	private void checkDistance(NpcAI2 ai, Creature creature) {
        if (creature instanceof Player && !creature.getLifeStats().isAlreadyDead()) {
			final Player player = (Player) creature;
			if (MathUtil.isIn3dRange(getOwner(), creature, 20)) {
				if (startedEvent.compareAndSet(false, true)) {
					///Use the Obelisk to register the current location as a resurrection bind point.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_NOTIFY_RESURRECT_POINT, 0);
				}
			}
        }
    }

	@Override
	protected void handleDialogStart(Player player) {
		BindPointTemplate bindPointTemplate = DataManager.BIND_POINT_DATA.getBindPointTemplate(getNpcId());
		Race race = player.getRace();
		if (SerialKillerService.getInstance().isRestrictDynamicBindstone(player)) {
			///You cannot bind from here.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
			return;
		} if (player.getSKInfo().getRank() > 0) {
            return;
        } if (bindPointTemplate == null) {
			log.info("There is no bind point template for npc: " + getNpcId());
			return;
		} if (player.getBindPoint() != null && player.getBindPoint().getMapId() == getPosition().getMapId() && MathUtil.getDistance(player.getBindPoint().getX(), player.getBindPoint().getY(), player.getBindPoint().getZ(), getPosition().getX(), getPosition().getY(), getPosition().getZ()) < 20) {
			///You have already bound at this location.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ALREADY_REGISTER_THIS_RESURRECT_POINT);
			//Poeta.
			final QuestState qs1100 = player.getQuestStateList().getQuestState(1100);
			if (qs1100 != null && qs1100.getStatus() == QuestStatus.START && qs1100.getQuestVarById(0) == 1) {
				ClassChangeService.onUpdateMission1100(player);
			}
			//Verteron.
			final QuestState qs1130 = player.getQuestStateList().getQuestState(1130);
			if (qs1130 != null && qs1130.getStatus() == QuestStatus.START && qs1130.getQuestVarById(0) == 1) {
				ClassChangeService.onUpdateMission1130(player);
			}
			//Ishalgen.
			final QuestState qs2100 = player.getQuestStateList().getQuestState(2100);
			if (qs2100 != null && qs2100.getStatus() == QuestStatus.START && qs2100.getQuestVarById(0) == 1) {
				ClassChangeService.onUpdateMission2100(player);
			}
			//Altgard.
			final QuestState qs2200 = player.getQuestStateList().getQuestState(2200);
			if (qs2200 != null && qs2200.getStatus() == QuestStatus.START && qs2200.getQuestVarById(0) == 1) {
				ClassChangeService.onUpdateMission2200(player);
			}
			//Telos.
			final QuestState qs10105 = player.getQuestStateList().getQuestState(10105);
			if (qs10105 != null && qs10105.getStatus() == QuestStatus.START && qs10105.getQuestVarById(0) == 1) {
				ClassChangeService.onUpdateMission10105(player);
			}
			return;
		}
		WorldType worldType = player.getWorldType();
		if (!CustomConfig.ENABLE_CROSS_FACTION_BINDING && !getTribe().equals(TribeClass.FIELD_OBJECT_ALL)) {
			if ((!getRace().equals(Race.NONE) && !getRace().equals(race)) || (race.equals(Race.ASMODIANS) && getTribe().equals(TribeClass.FIELD_OBJECT_LIGHT)) || (race.equals(Race.ELYOS) && getTribe().equals(TribeClass.FIELD_OBJECT_DARK))) {
				///You cannot register as you are not %0.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_BINDSTONE_CANNOT_FOR_INVALID_RIGHT(player.getCommonData().getOppositeRace().toString()));
				return;
			}
		} if (worldType == WorldType.PRISON) {
			///You cannot bind from here.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
			return;
		}
		bindHere(player, bindPointTemplate);
	}

	private void bindHere(Player player, final BindPointTemplate bindPointTemplate) {
		String price = Integer.toString(bindPointTemplate.getPrice());
		AI2Actions.addRequest(this, player, S_ASK.STR_ASK_REGISTER_RESURRECT_POINT, 0, new AI2Request() {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (responder.getWorldId() == requester.getWorldId()) {
					if (responder.getInventory().getKinah() < bindPointTemplate.getPrice()) {
						///You do not have enough Kinah to register this location as a bind point.
						PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE);
						return;
					} else if (MathUtil.getDistance(requester, responder) > 10) {
						///You cannot bind from here.
						PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
						return;
					}
					BindPointPosition old = responder.getBindPoint();
					BindPointPosition bpp = new BindPointPosition(requester.getWorldId(), responder.getX(), responder.getY(), responder.getZ(), responder.getHeading());
					bpp.setPersistentState(old == null ? PersistentState.NEW : PersistentState.UPDATE_REQUIRED);
					responder.setBindPoint(bpp);
					//Poeta.
					final QuestState qs1100 = player.getQuestStateList().getQuestState(1100);
					if (qs1100 != null && qs1100.getStatus() == QuestStatus.START && qs1100.getQuestVarById(0) == 1) {
						ClassChangeService.onUpdateMission1100(player);
					}
					//Verteron.
					final QuestState qs1130 = player.getQuestStateList().getQuestState(1130);
					if (qs1130 != null && qs1130.getStatus() == QuestStatus.START && qs1130.getQuestVarById(0) == 1) {
						ClassChangeService.onUpdateMission1130(player);
					}
					//Ishalgen.
					final QuestState qs2100 = player.getQuestStateList().getQuestState(2100);
					if (qs2100 != null && qs2100.getStatus() == QuestStatus.START && qs2100.getQuestVarById(0) == 1) {
						ClassChangeService.onUpdateMission2100(player);
					}
					//Altgard.
					final QuestState qs2200 = player.getQuestStateList().getQuestState(2200);
					if (qs2200 != null && qs2200.getStatus() == QuestStatus.START && qs2200.getQuestVarById(0) == 1) {
						ClassChangeService.onUpdateMission2200(player);
					}
					//Telos.
					final QuestState qs10105 = player.getQuestStateList().getQuestState(10105);
					if (qs10105 != null && qs10105.getStatus() == QuestStatus.START && qs10105.getQuestVarById(0) == 1) {
						ClassChangeService.onUpdateMission10105(player);
					} if (PlayerBindPointDAO.store(responder)) {
						responder.getInventory().decreaseKinah(bindPointTemplate.getPrice());
						TeleportService2.sendSetBindPoint(responder);
						PacketSendUtility.broadcastPacket(responder, new S_EFFECT(responder.getObjectId(), 2, responder.getCommonData().getLevel()), true);
						PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_DEATH_REGISTER_RESURRECT_POINT(""));
						old = null;
					} else {
					   responder.setBindPoint(old);
				    }
				}
			}
		}, price);
	}

	@Override
	public boolean isMoveSupported() {
		return false;
	}
}
