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
package com.aionemu.gameserver.model.templates;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.instance.InstanceCoolTimeType;
import com.aionemu.gameserver.model.instance.InstanceType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceCooltime")
public class InstanceCooltime
{
	@XmlElement(name = "type")
    protected InstanceCoolTimeType coolTimeType;
	
    @XmlElement(name = "type_value")
    protected String typeValue;
	
    @XmlElement(name = "ent_cool_time")
    protected Integer entCoolTime;
	
    @XmlElement(name = "indun_type")
    protected InstanceType indunType;
	
    @XmlElement(name = "max_member_light")
    protected Integer maxMemberLight;
	
    @XmlElement(name = "max_member_dark")
    protected Integer maxMemberDark;
	
    @XmlElement(name = "enter_min_level_light")
    protected Integer enterMinLevelLight;
	
    @XmlElement(name = "enter_max_level_light")
    protected Integer enterMaxLevelLight;
	
    @XmlElement(name = "enter_min_level_dark")
    protected Integer enterMinLevelDark;
	
    @XmlElement(name = "enter_max_level_dark")
    protected Integer enterMaxLevelDark;
	
    @XmlElement(name = "alarm_unit_score")
    protected Integer alarmUnitScore;
	
    @XmlElement(name = "can_enter_mentor")
    protected boolean canEnterMentor;
	
    @XmlElement(name = "enter_guild")
    protected boolean enterGuild;
	
    @XmlElement(name = "max_count")
    protected Integer max_count;
	
	@XmlElement(name = "count_build_up")
    protected Integer countBuildUp;
	
	@XmlElement(name = "count_build_up_level")
    protected Integer countBuildUpLevel;
	
	@XmlElement(name="price")
	protected long price;

	@XmlElement(name="component")
	protected int component;

	@XmlElement(name="component_count")
	protected int componentCount;

	@XmlElement(name="synch_id")
	protected int synchId;
	
   /**
	*/
    @XmlAttribute(required = true)
    protected int id;
	
    @XmlAttribute(required = true)
    protected int worldId;
	
    @XmlAttribute(required = true)
    protected Race race;
	
	public int getId() {
		return id;
	}
	
	public int getWorldId() {
		return worldId;
	}
	
	public Race getRace() {
		return race;
	}
	
	public InstanceCoolTimeType getCoolTimeType() {
        return coolTimeType;
    }
	
    public String getTypeValue() {
        return typeValue;
    }
	
    public InstanceType getTypeInstance() {
        return indunType;
    }
	
	public Integer getEntCoolTime() {
		return entCoolTime;
	}
	
	public Integer getMaxMemberLight() {
		return maxMemberLight;
	}
	
	public Integer getMaxMemberDark() {
		return maxMemberDark;
	}
	
	public Integer getEnterMinLevelLight() {
		return enterMinLevelLight;
	}
	
	public Integer getEnterMaxLevelLight() {
		return enterMaxLevelLight;
	}
	
	public Integer getEnterMinLevelDark() {
		return enterMinLevelDark;
	}
	
	public Integer getEnterMaxLevelDark() {
		return enterMaxLevelDark;
	}
	
	public Integer getAlarmUnitScore() {
		return alarmUnitScore;
	}
	
	public boolean getCanEnterMentor() {
		return canEnterMentor;
	}
	
	public boolean getEnterGuild() {
		return enterGuild;
	}
	
	public Integer getMaxEntriesCount() {
		return max_count;
	}
	
	public Integer getCountBuildUp() {
		return countBuildUp;
	}
	
	public Integer getCountBuildUpLevel() {
		return countBuildUpLevel;
	}
	
	public int getComponent() {
		return component;
	}

	public int getSynchId() {
		return synchId;
	}

	public long getPrice() {
		return price;
	}

	public int getComponentCount() {
		return componentCount;
	}
}