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
package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

public class AbyssSkillService
{
	public static final void updateSkills(Player player) {
		AbyssRank abyssRank = player.getAbyssRank();
		if (abyssRank == null) {
			return;
		}
		AbyssRankEnum rankEnum = abyssRank.getRank();
		for (AbyssSkills abyssSkill: AbyssSkills.values()) {
			if (abyssSkill.getRace() == player.getRace()) {
				for (int skillId: abyssSkill.getSkills()) {
					player.getSkillList().removeSkill(skillId);
				}
			}
		} if (abyssRank.getRank().getId() >= AbyssRankEnum.STAR5_OFFICER.getId()) {
			for (int skillId: AbyssSkills.getSkills(player.getRace(), rankEnum)) {
				player.getSkillList().addAbyssSkill(player, skillId, 1);
			}
		}
	}
	
	public static final void onEnterWorld(Player player) {
		updateSkills(player);
	}
}