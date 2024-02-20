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
package com.aionemu.gameserver.model.templates.npc;

import com.aionemu.gameserver.ai2.AiNames;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "npc_template")
public class NpcTemplate extends VisibleObjectTemplate
{
	private int npcId;
	@XmlAttribute(name = "level", required = true)
	private int level;
	@XmlAttribute(name = "name_id", required = true)
	private int nameId;
	@XmlAttribute(name = "title_id")
	private int titleId;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "height")
	private float height = 1;
	@XmlAttribute(name = "npc_type", required = true)
	private NpcType npcType;
	@XmlElement(name = "stats")
	private NpcStatsTemplate statsTemplate;
	@XmlElement(name = "equipment")
	private NpcEquippedGear equipment;
	@XmlElement(name = "kisk_stats")
	private KiskStatsTemplate kiskStatsTemplate;
	@SuppressWarnings("unused")
	@XmlElement(name = "ammo_speed")
	private int ammoSpeed = 0;
	@XmlAttribute(name = "rank")
	private NpcRank rank;
	@XmlAttribute(name = "rating")
	private NpcRating rating;
	@XmlAttribute(name = "sensory_range")
	private int aggrorange;
	@XmlAttribute(name = "attack_range")
	private int attackRange;
	@XmlAttribute(name = "attack_rate")
	private int attackRate;
	@XmlAttribute(name = "attack_delay")
	private int attackDelay;
	@XmlAttribute(name = "hpgauge_level")
	private int hpGaugeLevel;
	@XmlAttribute(name = "tribe")
	private TribeClass tribe;
	@XmlAttribute(name = "ai")
	private String ai = AiNames.DUMMY_NPC.getName();
	@XmlAttribute
	private Race race = Race.NONE;
	@XmlAttribute
	private int state;
	@XmlAttribute
	private boolean floatcorpse;
	@XmlAttribute(name = "on_mist")
	private Boolean onMist;
	@XmlElement(name = "bound_radius")
	private BoundRadius boundRadius;
	@XmlAttribute(name = "type")
	private NpcTemplateType npcTemplateType;
	@XmlAttribute(name = "abyss_type")
	private AbyssNpcType abyssNpcType;
	@XmlElement(name = "talk_info")
	private TalkInfo talkInfo;
	@XmlElement(name = "cash")
	private NpcCash cash;
	@XmlElement(name = "common_drop")
	private List<NpcCommonDrop> commonsdrops;
	@XmlElement(name = "drop_group")
	private NpcDropGroup dropGroup;
	@XmlElement(name = "massive_looting")
	private MassiveLooting massiveLooting;
	@XmlAttribute
	private boolean savetradecount;
	@XmlAttribute(name = "max_enemy_count")
	private int maxEnemyCount;
	@XmlAttribute(name = "max_chase_time")
	private int maxChaseTime;
	@XmlAttribute(name = "can_see_invisible")
	private int canSeeInvisible;
	
	@Override
	public int getTemplateId() {
		return npcId;
	}
	
	@Override
	public int getNameId() {
		return nameId;
	}
	
	public int getTitleId() {
		return titleId;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public float getHeight() {
		return height;
	}
	
	public NpcType getNpcType() {
		return npcType;
	}
	
	public void setNpcType(NpcType newType) {
		npcType = newType;
	}
	
	public NpcEquippedGear getEquipment() {
		return equipment;
	}
	
	public int getLevel() {
		return level;
	}
	
	public NpcStatsTemplate getStatsTemplate() {
		return statsTemplate;
	}
	
	public void setStatsTemplate(NpcStatsTemplate statsTemplate) {
		this.statsTemplate = statsTemplate;
	}
	
	public KiskStatsTemplate getKiskStatsTemplate() {
		return kiskStatsTemplate;
	}
	
	public TribeClass getTribe() {
		return tribe;
	}
	
	public String getAi() {
		return (!"noaction".equals(ai) && level > 1 && getAbyssNpcType().equals(AbyssNpcType.TELEPORTER)) ? "siege_teleporter" : ai;
	}
	
	@Override
	public String toString() {
		return "Npc Template id: " + npcId + " name: " + name;
	}
	
	@SuppressWarnings("unused")
	@XmlID
	@XmlAttribute(name = "npc_id", required = true)
	private void setXmlUid(String uid) {
		npcId = Integer.parseInt(uid);
	}
	
	public final NpcRank getRank() {
		return rank;
	}
	
	public final NpcRating getRating() {
		return rating;
	}
	
	public int getAggroRange() {
		return aggrorange;
	}
	
	public int getMinimumShoutRange() {
		if (aggrorange < 5) {
			return 5;
		}
		return aggrorange;
	}
	
	public int getAttackRange() {
		return attackRange;
	}
	
	public int getAttackRate() {
		return attackRate;
	}
	
	public int getAttackDelay() {
		return attackDelay;
	}
	
	public int getHpGaugeLevel() {
		return hpGaugeLevel;
	}
	
	public Race getRace() {
		return race;
	}
	
	@Override
	public int getState() {
		return state;
	}
	
	@Override
	public BoundRadius getBoundRadius() {
		return boundRadius != null ? boundRadius : super.getBoundRadius();
	}
	
	public NpcTemplateType getNpcTemplateType() {
		return npcTemplateType != null ? npcTemplateType : NpcTemplateType.NONE;
	}
	
	public AbyssNpcType getAbyssNpcType() {
		return abyssNpcType != null ? abyssNpcType : AbyssNpcType.NONE;
	}
	
	public final int getTalkDistance() {
		if (talkInfo == null)
			return 5;
		return talkInfo.getDistance();
	}
	
	public int getTalkDelay() {
		if (talkInfo == null)
			return 0;
		return talkInfo.getDelay();
	}

	public boolean canInteract() {
		return talkInfo != null;
	}
	
	public boolean isDialogNpc() {
		if (talkInfo == null)
			return false;
		return talkInfo.isDialogNpc();
	}
	
	public boolean isFloatCorpse() {
		return floatcorpse;
	}
	
	public Boolean getMistSpawnCondition() {
		return onMist;
	}
	
	public MassiveLooting getMassiveLooting() {
		return massiveLooting;
	}
	
	public boolean isSaveTradeCount() {
		return savetradecount;
	}
	
	public void setAttackRange(int value) {
        this.attackRange = value;
    }
	
    public void setAggroRange(int value) {
        this.aggrorange = value;
    }
	
	public void setLevel(int value) {
		this.level = value;
	}
	
	public int getCanSeeInvisible() {
		return canSeeInvisible;
	}
	
	public int getMaxEnemyCount() {
		return maxEnemyCount;
	}
	
	public int getMaxChaseTime() {
		return maxChaseTime;
	}
	
	public NpcCash getCash() {
		return cash;
	}
	
	public List<NpcCommonDrop> getCommonsdrops() {
		return commonsdrops;
	}
	
	public NpcDropGroup getDropGroup() {
		return dropGroup;
	}
}