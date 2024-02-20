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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADD_SKILL;
import com.aionemu.gameserver.network.aion.serverpackets.S_DELETE_SKILL;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillLearnService
{
	private static final Logger log = LoggerFactory.getLogger(SkillLearnService.class);
	
	public static void addNewSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();
		PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
		addSkills(player, level, playerClass, playerRace);
	}
	
	public static void addMissingSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();
		for (int i = 0; i <= level; i++) {
			addSkills(player, i, playerClass, playerRace);
		} if (!playerClass.isStartingClass()) {
			PlayerClass startinClass = PlayerClass.getStartingClassFor(playerClass);
			for (int i = 1; i < 10; i++) {
				addSkills(player, i, startinClass, playerRace);
			}
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
		}
	}
	
	public static void addMissingSkills4P(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();
		for (int i = 0; i <= level; i++) {
			addSkills(player, i, playerClass, playerRace);
		} if (!playerClass.isStartingClass()) {
			PlayerClass startinClass = PlayerClass.getStartingClassFor(playerClass);
			for (int i = 1; i < 10; i++) {
				addSkills(player, i, startinClass, playerRace);
			}
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
		}
	}
	
	public static void addSkills(Player player, int level, PlayerClass playerClass, Race playerRace) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(playerClass, level, playerRace);
		PlayerSkillList playerSkillList = player.getSkillList();
		for (SkillLearnTemplate template : skillTemplates) {
			if (!checkLearnIsPossible(player, playerSkillList, template)) {
				continue;
			} if (playerSkillList.isCraftSkill(template.getSkillId()) && player.getSkillList().isSkillPresent(template.getSkillId())) {
				continue;
			} else {
				playerSkillList.addSkill(player, template.getSkillId(), template.getSkillLevel());
			}
		}
	}
	
	private static boolean checkLearnIsPossible(Player player, PlayerSkillList playerSkillList, SkillLearnTemplate template) {
		if (playerSkillList.isSkillPresent(template.getSkillId())) {
			return true;
		} if (!template.isStigma() && !template.isSkillBook()) {
			return true;
		} if ((player.havePermission(MembershipConfig.STIGMA_AUTOLEARN) && template.isStigma())) {
			return true;
		} if (template.isAutoLearn()) {
			return true;
		}
		return false;
	}
	
	public static void learnSkillBook(Player player, int skillId) {
		SkillLearnTemplate[] skillTemplates = null;
		int maxLevel = 1;
		SkillTemplate passiveSkill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		for (int i = 1; i <= player.getLevel(); i++) {
			skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
			for (SkillLearnTemplate skill : skillTemplates) {
				if (skillId == skill.getSkillId()) {
					if (skill.getSkillLevel() > maxLevel) {
						maxLevel = skill.getSkillLevel();
					}
				}
			}
		}
		player.getSkillList().addSkill(player, skillId, maxLevel);
		if (passiveSkill.isPassive()) {
			player.getController().updatePassiveStats();
		}
	}
	
	public static void removeSkill(Player player, int skillId) {
		SkillTemplate passiveSkill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (player.getSkillList().isSkillPresent(skillId)) {
			Integer skillLevel = player.getSkillList().getSkillLevel(skillId);
			if (skillLevel == null) {
				skillLevel = 1;
			} if (player.getEffectController().hasAbnormalEffect(skillId) || passiveSkill.isPassive()) {
				player.getEffectController().removeEffect(skillId);
			}
			PacketSendUtility.sendPacket(player, new S_DELETE_SKILL(skillId, skillLevel, player.getSkillList().getSkillEntry(skillId).isStigma()));
			player.getSkillList().removeSkill(skillId);
		}
	}

	public static int getSkillLearnLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		int learnFinishes = 0;
		int maxLevel = 0;
		for (SkillLearnTemplate template : skillTemplates) {
			if (maxLevel < template.getSkillLevel()) {
				maxLevel = template.getSkillLevel();
			}
		} if (maxLevel == 0) {
			return wantedSkillLevel;
		}
		learnFinishes = playerLevel + maxLevel;
		if (learnFinishes > DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
			learnFinishes = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		}
		return Math.max(wantedSkillLevel, Math.min(playerLevel - (learnFinishes - maxLevel) + 1, maxLevel));
	}
	
	public static int getSkillMinLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		SkillLearnTemplate foundTemplate = null;
		for (SkillLearnTemplate template : skillTemplates) {
			if (template.getSkillLevel() <= wantedSkillLevel && template.getMinLevel() <= playerLevel) {
				foundTemplate = template;
			}
		} if (foundTemplate == null) {
			return playerLevel;
		}
		return foundTemplate.getMinLevel();
	}
}