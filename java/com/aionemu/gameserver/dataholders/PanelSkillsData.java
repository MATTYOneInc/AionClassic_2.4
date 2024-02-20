package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.panels.SkillPanel;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "polymorph_panels")
public class PanelSkillsData
{
	@XmlElement(name = "panel")
	protected List<SkillPanel> templates;
	private TIntObjectHashMap<SkillPanel> skillPanels = new TIntObjectHashMap<SkillPanel>();

	void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		for (SkillPanel panel : templates) {
			skillPanels.put(panel.getPanelId(), panel);
		}
		templates.clear();
		templates = null;
	}
	
	public SkillPanel getSkillPanel(int id) {
		return (SkillPanel) skillPanels.get(id);
	}
	
	public int size() {
		return skillPanels.size();
	}
}