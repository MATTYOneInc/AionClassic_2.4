package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.stats.CalculatedPlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "player_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData
{
	@XmlElement(name = "player_stats", required = true)
	private List<PlayerStatsType> templatesList = new ArrayList<PlayerStatsType>();
	
	private final TIntObjectHashMap<PlayerStatsTemplate> playerTemplates = new TIntObjectHashMap<PlayerStatsTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (PlayerStatsType pt: templatesList) {
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());
			PlayerStatsTemplate template = pt.getTemplate();
			template.setMaxMp(Math.round(template.getMaxMp() * 100f / template.getWill()));
			template.setMaxHp(Math.round(template.getMaxHp() * 100f / template.getHealth()));
			int agility = template.getAgility();
			agility = (agility-100);
			template.setEvasion(Math.round(template.getEvasion() - template.getEvasion() * agility * 0.003f));
			template.setBlock(Math.round(template.getBlock() - template.getBlock() * agility * 0.0025f));
			template.setParry(Math.round(template.getParry() - template.getParry() * agility * 0.0025f));
			template.setStrikeResist(template.getStrikeResist());
			template.setSpellResist(template.getSpellResist());
			playerTemplates.put(code, pt.getTemplate());
		}
		playerTemplates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		playerTemplates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		playerTemplates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		playerTemplates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		playerTemplates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		playerTemplates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		playerTemplates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		playerTemplates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		playerTemplates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		playerTemplates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		playerTemplates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		playerTemplates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SPIRIT_MASTER));
		playerTemplates.put(makeHash(PlayerClass.MONK, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MONK));
		playerTemplates.put(makeHash(PlayerClass.THUNDERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.THUNDERER));
		templatesList.clear();
		templatesList = null;
	}
	
	public PlayerStatsTemplate getTemplate(Player player) {
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if (template == null) {
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		}
		return template;
	}
	
	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level) {
		PlayerStatsTemplate template = playerTemplates.get(makeHash(playerClass, level));
		if (template == null) {
			template = getTemplate(playerClass, 0);
		}
		return template;
	}
	
	public int size() {
		return playerTemplates.size();
	}
	
	@XmlRootElement(name = "playerStatsTemplateType")
	private static class PlayerStatsType {
		@XmlAttribute(name = "class", required = true)
		private PlayerClass requiredPlayerClass;
		
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;
		
		@XmlElement(name = "stats_template")
		private PlayerStatsTemplate template;
		
		public PlayerClass getRequiredPlayerClass() {
			return requiredPlayerClass;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public PlayerStatsTemplate getTemplate() {
			return template;
		}
	}
	
	private static int makeHash(PlayerClass playerClass, int level) {
		return level << 8 | playerClass.ordinal();
	}
}