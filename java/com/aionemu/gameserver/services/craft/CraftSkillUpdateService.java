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
package com.aionemu.gameserver.services.craft;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RecipeList;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.templates.CraftLearnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CraftSkillUpdateService
{
	private static final Logger log = LoggerFactory.getLogger(CraftSkillUpdateService.class);

	protected static final Map<Integer, CraftLearnTemplate> npcBySkill = new HashMap<Integer, CraftLearnTemplate>();
	private static final Map<Integer, Integer> cost = new HashMap<Integer, Integer>();
	private static final List<Integer> craftingSkillIds = new ArrayList<Integer>();

	public static final CraftSkillUpdateService getInstance() {
		return SingletonHolder.instance;
	}

	private CraftSkillUpdateService() {
		//CRAFT ASMODIANS.
		npcBySkill.put(204096, new CraftLearnTemplate(30002, false, "Essencetapping"));
		npcBySkill.put(204257, new CraftLearnTemplate(30003, false, "Aethertapping"));
		npcBySkill.put(204100, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(204104, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(204106, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(204110, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(204102, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(204108, new CraftLearnTemplate(40008, true, "Handicrafting"));

		//CRAFT ELYOS.
		npcBySkill.put(203780, new CraftLearnTemplate(30002, false, "Essencetapping"));
		npcBySkill.put(203782, new CraftLearnTemplate(30003, false, "Aethertapping"));
		npcBySkill.put(203784, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(203788, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(203790, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(203793, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(203786, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(203792, new CraftLearnTemplate(40008, true, "Handicrafting"));

		//PRICE CRAFT KINAH.
		cost.put(0, 3500);
		cost.put(99, 17000);
		cost.put(199, 115000);
		cost.put(299, 460000);
		cost.put(399, 0);
		cost.put(449, 6004900);
		cost.put(499, 12000000);

		craftingSkillIds.add(40001);
		craftingSkillIds.add(40002);
		craftingSkillIds.add(40003);
		craftingSkillIds.add(40004);
		craftingSkillIds.add(40007);
		craftingSkillIds.add(40008);
		log.info("Craft System: Initialized.");
	}

	public void setMorphRecipe(Player player) {
		int object = player.getObjectId();
		Race race = player.getCommonData().getRace();
		if (player.getLevel() >= 10) {
			RecipeList recipelist = null;
			recipelist = PlayerRecipesDAO.load(object);
			if (race == Race.ELYOS) {
				if (!recipelist.isRecipePresent(155000005)) { //Morph Method: Iron Ore.
					PlayerRecipesDAO.addRecipe(object, 155000005);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155000005));
				} if (!recipelist.isRecipePresent(155000002)) { //Morph Method: Inina.
					PlayerRecipesDAO.addRecipe(object, 155000002);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155000002));
				} if (!recipelist.isRecipePresent(155000001)) { //Morph Method: Aria.
					PlayerRecipesDAO.addRecipe(object, 155000001);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155000001));
				}
			} else if (race == Race.ASMODIANS) {
				if (!recipelist.isRecipePresent(155005005)) { //Morph Method: Iron Ore.
					PlayerRecipesDAO.addRecipe(object, 155005005);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155005005));
				} if (!recipelist.isRecipePresent(155005002)) { //Morph Method: Conide.
					PlayerRecipesDAO.addRecipe(object, 155005002);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155005002));
				} if (!recipelist.isRecipePresent(155005001)) { //Morph Method: Azpha.
					PlayerRecipesDAO.addRecipe(object, 155005001);
					PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(155005001));
				}
			}
		}
	}

	public void learnSkill(Player player, Npc npc) {
		if (player.getLevel() < 10) {
			//You must level up to raise your skill level.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CRAFT_INFO_MAXPOINT_UP);
			return;
		}
		final CraftLearnTemplate template = npcBySkill.get(npc.getNpcId());
		if (template == null) {
			return;
		}
		final int skillId = template.getSkillId();
		if (skillId == 0) {
			return;
		}
		int skillLvl = 0;
		PlayerSkillList skillList = player.getSkillList();
		if (skillList.isSkillPresent(skillId)) {
			skillLvl = skillList.getSkillLevel(skillId);
		} if (!cost.containsKey(skillLvl)) {
			//You cannot be promoted as your skill level is too low.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390233));
			return;
		} if (isCraftingSkill(skillId) && (!canLearnMoreExpertCraftingSkill(player) && skillLvl == 399)) {
			PacketSendUtility.sendMessage(player, "You can only have " + CraftConfig.MAX_EXPERT_CRAFTING_SKILLS + " Expert crafting skills.");
			return;
		} if (isCraftingSkill(skillId) && (!canLearnMoreMasterCraftingSkill(player) && skillLvl == 499)) {
			PacketSendUtility.sendMessage(player, "You can only have " + CraftConfig.MAX_MASTER_CRAFTING_SKILLS + " Master crafting skill.");
			return;
		}
		//ESSENCETAPPING
		if (skillLvl == 399 && ((skillId == 30002 && //Essencetapping [Journeyman]
		    (!player.isCompleteQuest(19001) || !player.isCompleteQuest(29001))))) {
			//You must pass the Expert test in order to be promoted.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400284));
			return;
		} if (skillLvl == 499 && (skillId == 30002)) { //Essencetapping [Artisan]
			//You cannot be promoted any more.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1330069));
			return;
		}
		//AETHERTAPPING
		if (skillLvl == 399 && ((skillId == 30003 && //[Journeyman]
		    (!player.isCompleteQuest(19003) || !player.isCompleteQuest(29003))))) {
			//You must pass the Expert test in order to be promoted.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400284));
			return;
		} if (skillLvl == 499 && (skillId == 30003)) { //Aethertapping [Artisan]
			//You cannot be promoted any more.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1330069));
			return;
		}
		//CRAFTING
		if (skillLvl == 499 //[Artisan]
			&& ((skillId == 40001 && (!player.isCompleteQuest(19039) || !player.isCompleteQuest(29039)))
			|| (skillId == 40002 && (!player.isCompleteQuest(19009) || !player.isCompleteQuest(29009)))
			|| (skillId == 40003 && (!player.isCompleteQuest(19015) || !player.isCompleteQuest(29015)))
			|| (skillId == 40004 && (!player.isCompleteQuest(19021) || !player.isCompleteQuest(29021)))
			|| (skillId == 40007 && (!player.isCompleteQuest(19033) || !player.isCompleteQuest(29033)))
			|| (skillId == 40008 && (!player.isCompleteQuest(19027) || !player.isCompleteQuest(29027))))) {
			//You must pass the Master test in order to be promoted.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400286));
			return;
		} if (skillLvl == 549) { //[Master]
			//You cannot be promoted any more.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1330069));
			return;
		}
		final int price = cost.get(skillLvl);
		final long kinah = player.getInventory().getKinah();
		final int skillLevel = skillLvl;
		RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (price < kinah && responder.getInventory().tryDecreaseKinah(price)) {
					PlayerSkillList skillList = responder.getSkillList();
					skillList.addSkill(responder, skillId, skillLevel + 1);
					responder.getRecipeList().autoLearnRecipe(responder, skillId, skillLevel + 1);
					PacketSendUtility.sendPacket(responder, new S_RECIPE_LIST(responder.getRecipeList().getRecipeList()));
					//You have learned the %0 skill.
					PacketSendUtility.sendPacket(responder, new S_ADD_SKILL(skillList.getSkillEntry(skillId), 1330004, false));
					PacketSendUtility.broadcastPacket(responder, new S_EFFECT(responder.getObjectId(), 4, responder.getCommonData().getLevel()), true);
				} else {
					//You do not have enough Kinah.
					PacketSendUtility.sendPacket(responder, new S_MESSAGE_CODE(1300388));
				}
			}
			@Override
			public void denyRequest(Creature requester, Player responder) {
			}
		};
		boolean result = player.getResponseRequester().putRequest(S_ASK.STR_CRAFT_ADDSKILL_CONFIRM, responseHandler);
		if (result) {
			PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_CRAFT_ADDSKILL_CONFIRM, 0, 0, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId()), String.valueOf(price)));
		}
	}

	public static boolean isCraftingSkill(int skillId) {
		Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext()) {
			if (it.next() == skillId)
				return true;
		}
		return false;
	}

	static int getTotalExpertCraftingSkills(Player player) {
		int mastered = 0;
		Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext()) {
			int skillId = it.next();
			int skillLvl = 0;
			if (player.getSkillList().isSkillPresent(skillId)) {
				skillLvl = player.getSkillList().getSkillLevel(skillId);
				if (skillLvl > 399 && skillLvl <= 499)
					mastered++;
			}
		}
		return mastered;
	}

	static int getTotalMasterCraftingSkills(Player player) {
		int mastered = 0;
		Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext()) {
			int skillId = it.next();
			int skillLvl = 0;
			if (player.getSkillList().isSkillPresent(skillId)) {
				skillLvl = player.getSkillList().getSkillLevel(skillId);
				if (skillLvl > 499)
					mastered++;
			}
		}
		return mastered;
	}

	public static boolean canLearnMoreExpertCraftingSkill(Player player) {
		if (getTotalExpertCraftingSkills(player) + getTotalMasterCraftingSkills(player) < CraftConfig.MAX_EXPERT_CRAFTING_SKILLS)
			return true;
		else
			return false;
	}

	public static boolean canLearnMoreMasterCraftingSkill(Player player) {
		if (getTotalMasterCraftingSkills(player) < CraftConfig.MAX_MASTER_CRAFTING_SKILLS)
			return true;
		else
			return false;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final CraftSkillUpdateService instance = new CraftSkillUpdateService();
	}
}
