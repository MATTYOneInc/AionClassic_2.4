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
package com.aionemu.gameserver.model.templates.recipe;


import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunaTemplate")
public class LunaTemplate
{
	protected List<LunaComponent> luna_component_panel_1;
	protected List<LunaComponent> luna_component_panel_2;
	protected List<LunaComponent> luna_component_panel_3;
	protected List<LunaComponent> luna_component_panel_4;
	protected List<LunaComponent> luna_component_panel_5;
	
	@XmlAttribute(name = "max_production_count")
	protected Integer maxProductionCount;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute
	protected int quantity;
	
	@XmlAttribute
	protected int group;
	
	@XmlAttribute(name = "success_rate")
	protected int success_rate;
	
	@XmlAttribute
	protected int productid;
	
	@XmlAttribute
	protected Race race;
	
	@XmlAttribute
	protected int itemid;
	
	@XmlAttribute
	protected int nameid;
	
	@XmlAttribute
	protected int id;
	
	public List<LunaComponent> getLunaComponent() {
		if (luna_component_panel_1 == null) {
			luna_component_panel_1 = new ArrayList<LunaComponent>();
		}
		return this.luna_component_panel_1;
	}
	public List<LunaComponent> getLunaComponent2() {
		if (luna_component_panel_2 == null) {
			luna_component_panel_2 = new ArrayList<LunaComponent>();
		}
		return this.luna_component_panel_2;
	}
	public List<LunaComponent> getLunaComponent3() {
		if (luna_component_panel_3 == null) {
			luna_component_panel_3 = new ArrayList<LunaComponent>();
		}
		return this.luna_component_panel_3;
	}
	public List<LunaComponent> getLunaComponent4() {
		if (luna_component_panel_4 == null) {
			luna_component_panel_4 = new ArrayList<LunaComponent>();
		}
		return this.luna_component_panel_4;
	}
	public List<LunaComponent> getLunaComponent5() {
		if (luna_component_panel_5 == null) {
			luna_component_panel_5 = new ArrayList<LunaComponent>();
		}
		return this.luna_component_panel_5;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public Integer getGroup() {
		return group;
	}
	
	public int getRate() {
		return success_rate;
	}
	
	public Integer getProductid() {
		return productid;
	}
	
	public Race getRace() {
		return race;
	}
	
	public Integer getItemid() {
		return itemid;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNameid() {
		return nameid;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Integer getMaxProductionCount() {
		return maxProductionCount;
	}
}