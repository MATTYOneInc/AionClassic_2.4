package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import org.slf4j.LoggerFactory;

enum AbyssSkills
{
	//Elyos.
	SUPREME_COMMANDER_E(Race.ELYOS, AbyssRankEnum.SUPREME_COMMANDER, new int[] {11889, 11898, 11900, 11903, 11904, 11905, 11906}),
	COMMANDER_E(Race.ELYOS, AbyssRankEnum.COMMANDER, new int[] {11888, 11898, 11900, 11903, 11904}),
	GREAT_GENERAL_E(Race.ELYOS, AbyssRankEnum.GREAT_GENERAL, new int[] {11887, 11897, 11899, 11903}),
	GENERAL_E(Race.ELYOS, AbyssRankEnum.GENERAL, new int[] {11886, 11896, 11899}),
	STAR5_OFFICER_E(Race.ELYOS, AbyssRankEnum.STAR5_OFFICER, new int[] {11885, 11895}),
	
	//Asmodians.
	SUPREME_COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.SUPREME_COMMANDER, new int[] {11894, 11898, 11902, 11903, 11904, 11905, 11906}),
	COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.COMMANDER, new int[] {11893, 11898, 11902, 11903, 11904}),
	GREAT_GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GREAT_GENERAL, new int[] {11892, 11897, 11901, 11903}),
	GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GENERAL, new int[] {11891, 11896, 11901}),
	STAR5_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR5_OFFICER, new int[] {11890, 11895});
	
	private int[] skills;
	private AbyssRankEnum rankenum;
	private Race race;
	
	private AbyssSkills(Race race, AbyssRankEnum rankEnum, int[] skills) {
		this.race = race;
		rankenum = rankEnum;
		this.skills = skills;
	}
	
	public Race getRace() {
		return race;
	}
	
	public int[] getSkills() {
		return skills;
	}
	
	public static int[] getSkills(Race race, AbyssRankEnum rank) {
		for (AbyssSkills aSkills : values()) {
			if ((aSkills.race == race) && (aSkills.rankenum == rank)) {
				return aSkills.skills;
			}
		}
		LoggerFactory.getLogger(AbyssSkills.class).warn("No abyss skills for: " + race + " " + rank);
		return new int[0];
	}
}