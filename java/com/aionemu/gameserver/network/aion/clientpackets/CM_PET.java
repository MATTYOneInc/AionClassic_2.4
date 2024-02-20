package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.NameConfig;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.toypet.PetAdoptionService;
import com.aionemu.gameserver.services.toypet.PetMoodService;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_PET extends AionClientPacket
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_PET.class);

	private int actionId;
	private PetAction action;
	private int petId;
	private String petName;
	private int decorationId;
	private int eggObjId;
	private int objectId;
	private int count;
	private int subType;
	private int emotionId;
	private int actionType;
	private int dopingItemId;
	private int dopingAction;
	private int dopingSlot1;
	private int dopingSlot2;
	private int activateLoot;
	private int unk2;
	private int unk3;
	@SuppressWarnings("unused")
	private int unk5;
	@SuppressWarnings("unused")
	private int unk6;
	
	public CM_PET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readH();
		action = PetAction.getActionById(actionId);
		switch (action) {
		case ADOPT:
			eggObjId = readD();
			petId = readD();
			unk2 = readC();
			unk3 = readD();
			decorationId = readD();
			unk5 = readD();
			unk6 = readD();
			petName = readS();
			break;
		case SURRENDER:
		case SPAWN:
		case DISMISS:
			petId = readD();
			break;
		case FOOD:
			actionType = readD();
			if (actionType == 3) {
				activateLoot = readD();
			} else if (actionType == 2) {
				dopingAction = readD();
				if (dopingAction == 0) {
					dopingItemId = readD();
					dopingSlot1 = readD();
				} else if (dopingAction == 1) {
					dopingSlot1 = readD();
					dopingItemId = readD();
				} else if (dopingAction == 2) {
					dopingSlot1 = readD();
					dopingSlot2 = readD();
				} else if (dopingAction == 3) {
					dopingItemId = readD();
					dopingSlot1 = readD();
				}
			} else {
				objectId = readD();
				count = readD();
				unk2 = readD();
			}
			break;
		case RENAME:
			petId = readD();
			petName = readS();
			break;
		case MOOD:
			subType = readD();
			emotionId = readD();
			break;
		default:
			break;
		}
	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
		Pet pet = player.getPet();
		switch (action) {
			case ADOPT:
				if (NameRestrictionService.isForbiddenWord(petName)) {
					///That name is invalid. Please try another..
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_PET_NOT_AVALIABE_NAME, 0);
				} else {
					PetAdoptionService.adoptPet(player, eggObjId, petId, petName, decorationId);
				}
			break;
			case SURRENDER:
				PetAdoptionService.surrenderPet(player, petId);
				///Items stored in the surrendered pet's bag have been returned to your cube.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_TOYPET_RETURN_MASTER_ITEM, 0);
			break;
			case SPAWN:
				if (player.getPet() != null) {
					///You already have a spirit following you.
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_SKILL_SUMMON_ALREADY_HAVE_A_FOLLOWER, 0);
				} else if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM)) || player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
					///You cannot summon a pet while you are %0.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_TOYPET_CANT_SUMMON_STATE(new DescriptionId(2800109)));
				} else {
					PetSpawnService.summonPet(player, petId, true);
				}
			break;
			case DISMISS:
				if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM)) || player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
					///You cannot unsummon a a pet in %0.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_TOYPET_CANT_UNSUMMON_STATE(new DescriptionId(2800109)));
				} else {
					PetSpawnService.dismissPet(player, true);
				}
			break;
			case FOOD:
				if (actionType == 2) {
					if (dopingAction == 2) {
						PetService.getInstance().relocateDoping(player, dopingSlot1, dopingSlot2);
					} else {
						PetService.getInstance().useDoping(player, dopingAction, dopingItemId, dopingSlot1);
					}
				} else if (actionType == 3) {
					boolean isInFortZone = false;
					for (ZoneInstance zone: player.getPosition().getMapRegion().getZones(player)) {
						if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT)) {
							isInFortZone = true;
							break;
						}
					} if (isInFortZone) {
						///You cannot use the selected function in the current restriction phase.
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_ACCUSE_TARGET_IS_NOT_VALID, 0);
						return;
					} else {
						PetService.getInstance().activateLoot(player, activateLoot != 0);
					}
				} else if (pet != null && !pet.getCommonData().isFeedingTime()) {
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(8, actionId, objectId, count, player.getPet()));
				} else if (pet != null && objectId == 0 && pet.getCommonData().isFeedingTime()) {
					pet.getCommonData().setCancelFeed(true);
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(4, actionId, 0, 0, player.getPet()));
					PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				} else {
					PetService.getInstance().removeObject(objectId, count, actionId, player);
				}
			break;
			case RENAME:
				if (NameConfig.PET_NAME_CHANGE_ENABLE) {
					if (NameRestrictionService.isForbiddenWord(petName)) {
						///That name is invalid. Please try another..
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_PET_NOT_AVALIABE_NAME, 0);
					} else {
						PetService.getInstance().renamePet(player, petName);
					}
				}
			break;
			case MOOD:
				if (pet != null && ((subType == 0 && pet.getCommonData().getMoodRemainingTime() == 0) || (subType == 3 && pet.getCommonData().getGiftRemainingTime() == 0) || emotionId != 0)) {
					PetMoodService.checkMood(pet, subType, emotionId);
				}
			default:
			break;
		}
	}
}