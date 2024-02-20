package com.aionemu.gameserver.controllers;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.S_QUEST;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.skillengine.task.GatheringTask;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndSelector;
import com.aionemu.gameserver.utils.captcha.CAPTCHAUtil;
import com.aionemu.gameserver.world.World;

import java.util.List;

public class GatherableController extends VisibleObjectController<Gatherable>
{
	private int gatherCount;
	private int currentGatherer;
	private GatheringTask task;
	private GatherState state = GatherState.IDLE;
	private RndSelector<Material> mats;
	
	public enum GatherState {
		GATHERED,
		GATHERING,
		IDLE
	}
	
	public void onStartUse(final Player player) {
		final GatherableTemplate template = this.getOwner().getObjectTemplate();
		int gatherId = template.getTemplateId();
		if (template.getLevelLimit() > 0) {
			if (player.getLevel() < template.getLevelLimit()) {
				///You must be at least level %0 to perform extraction.
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400737, template.getLevelLimit()));
				return;
			}
		} if (player.getInventory().isFull()) {
			///You must have at least one free space in your cube to gather.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_GATHER_INVENTORY_IS_FULL);
			return;
		} if (MathUtil.getDistance(getOwner(), player) > 5) {
			return;
		} if (!checkGatherable(player, template)) {
			return;
		} if (!checkPlayerSkill(player, template)) {
			return;
		}
		byte result = checkPlayerRequiredExtractor(player, template);
		if (result == 0) {
			return;
		} if (SecurityConfig.CAPTCHA_ENABLE) {
			if (SecurityConfig.CAPTCHA_APPEAR.equals(template.getSourceType()) || SecurityConfig.CAPTCHA_APPEAR.equals("ALL")) {
				int rate = SecurityConfig.CAPTCHA_APPEAR_RATE;
				if (template.getCaptchaRate() > 0) {
					rate = (int) (template.getCaptchaRate() * 0.1f);
				} if (Rnd.get(0, 100) < rate) {
					player.setCaptchaWord(CAPTCHAUtil.getRandomWord());
					player.setCaptchaImage(CAPTCHAUtil.createCAPTCHA(player.getCaptchaWord()).array());
					PunishmentService.setIsNotGatherable(player, 0, true, SecurityConfig.CAPTCHA_EXTRACTION_BAN_TIME * 1000);
					//You were poisoned during extraction and cannot extract for (Time remaining: 10Min)
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CAPTCHA_RESTRICTED("10"));
					PacketSendUtility.sendPacket(player, new S_QUEST(0, 60));
				}
			}
		}
		List<Material> materials = null;
		switch (result) {
			case 1:
				materials = template.getExtraMaterials().getMaterial();
			break;
			case 2:
				materials = template.getMaterials().getMaterial();
			break;
		}
		mats = new RndSelector<Material>();
		for (Material mat: materials) {
			mats.add(mat, mat.getRate());
		}
		synchronized (state) {
			if (state != GatherState.GATHERING) {
				state = GatherState.GATHERING;
				currentGatherer = player.getObjectId();
				player.getObserveController().attach(new StartMovingListener() {
					@Override
					public void moved() {
						finishGathering(player);
					}
				});
				int skillLvlDiff = player.getSkillList().getSkillLevel(template.getHarvestSkill()) - template.getSkillLevel();
				task = new GatheringTask(player, getOwner(), getMaterial(), skillLvlDiff);
				task.start();
			}
		}
	}
	
	public Material getMaterial() {
		Material m = mats.select();
        int chance = Rnd.get(m.getRate());
        int current = 0;
        current += m.getRate();
        if (mats != null) {
            if (current >= chance) {
                return m;
            }
        }
        return null;
	}
	
	private boolean checkPlayerSkill(final Player player, final GatherableTemplate template) {
		int harvestSkillId = template.getHarvestSkill();
		if (!player.getSkillList().isSkillPresent(harvestSkillId)) {
			if (harvestSkillId == 30001) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_GATHER_INCORRECT_SKILL);
			} else {
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1330054, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(harvestSkillId).getNameId())));
			}
			return false;
		} if (player.getSkillList().getSkillLevel(harvestSkillId) < template.getSkillLevel()) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1330001, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(harvestSkillId).getNameId())));
			return false;
		}
		return true;
	}
	
	private byte checkPlayerRequiredExtractor(final Player player, final GatherableTemplate template) {
		if (template.getRequiredItemId() > 0) {
			if (template.getCheckType() == 1) {
				List<Item> items = player.getEquipment().getEquippedItemsByItemId(template.getRequiredItemId());
				boolean condOk = false;
				for (Item item: items) {
					if (item.isEquipped()) {
						condOk = true;
						break;
					}
				}
				return (byte) (condOk ? 1 : 2);
			} else if (template.getCheckType() == 2) {
				if (player.getInventory().getItemCountByItemId(template.getRequiredItemId()) < template.getEraseValue()) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400376, new DescriptionId(template.getRequiredItemNameId())));
					return 0;
				} else {
					return 1;
				}
			}
		}
		return 2;
	}
	
	private boolean checkGatherable(final Player player, final GatherableTemplate template) {
		if (player.isNotGatherable()) {
			//You are currently unable to extract. (Time remaining: 10Min)
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400273, (int) ((player.getGatherableTimer() - (System.currentTimeMillis() - player .getStopGatherable())) / 1000)));
			return false;
		}
		return true;
	}
	
	public void completeInteraction() {
		state = GatherState.IDLE;
		gatherCount++;
		if (gatherCount == getOwner().getObjectTemplate().getHarvestCount()) {
			onDespawn();
		}
	}
	
	public void rewardPlayer(Player player) {
		if (player != null) {
			int skillLvl = getOwner().getObjectTemplate().getSkillLevel();
			int xpReward = (int) ((0.0031 * (skillLvl + 5.3) * (skillLvl + 1592.8) + 60));
			if (player.getSkillList().addSkillXp(player, getOwner().getObjectTemplate().getHarvestSkill(), (int) RewardType.GATHERING.calcReward(player, xpReward), skillLvl)) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_EXTRACT_GATHERING_SUCCESS_GETEXP);
				player.getCommonData().addExp(xpReward, RewardType.GATHERING);
			    PacketSendUtility.sendPacket(player, new S_STATUS(player));
			} else {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(getOwner().getObjectTemplate().getHarvestSkill()).getNameId())));
			}
			BattlePassService.getInstance().onUpdateBattlePassMission(player, getOwner().getObjectTemplate().getTemplateId(), 1, BattlePassAction.GATHERING);
		}
	}
	
	public void finishGathering(Player player) {
		if (currentGatherer == player.getObjectId()) {
			if (state == GatherState.GATHERING) {
				task.abort();
			}
			currentGatherer = 0;
			state = GatherState.IDLE;
		}
	}
	
	@Override
	public void onDespawn() {
		Gatherable owner = getOwner();
		if (!getOwner().isInInstance()) {
			RespawnService.scheduleRespawnTask(owner);
		}
		World.getInstance().despawn(owner);
	}
	
	@Override
	public void onBeforeSpawn() {
		this.gatherCount = 0;
	}
	
	@Override
	public Gatherable getOwner() {
		return super.getOwner();
	}
}