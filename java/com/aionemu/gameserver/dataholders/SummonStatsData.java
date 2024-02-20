package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "summon_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class SummonStatsData
{
	@XmlElement(name = "summon_stats", required = true)
	private List<SummonStatsType> summonTemplatesList = new ArrayList<SummonStatsType>();
	
	private final TIntObjectHashMap<SummonStatsTemplate> summonTemplates = new TIntObjectHashMap<SummonStatsTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (SummonStatsType st: summonTemplatesList) {
			int summonDark = makeHash(st.getNpcIdDark(), st.getRequiredLevel());
			summonTemplates.put(summonDark, st.getTemplate());
			int summonLight = makeHash(st.getNpcIdLight(), st.getRequiredLevel());
			summonTemplates.put(summonLight, st.getTemplate());
		}
	}
	
	public SummonStatsTemplate getSummonTemplate(int npcId, int level) {
		SummonStatsTemplate template = summonTemplates.get(makeHash(npcId, level));
		return template;
	}
	
	public int size() {
		return summonTemplates.size();
	}
	
	@XmlRootElement(name = "summonStatsTemplateType")
	private static class SummonStatsType {
		@XmlAttribute(name = "npc_id_dark", required = true)
		private int npcIdDark;
		
		@XmlAttribute(name = "npc_id_light", required = true)
		private int npcIdLight;
		
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;
		
		@XmlElement(name = "stats_template")
		private SummonStatsTemplate template;
		
		public int getNpcIdDark() {
			return npcIdDark;
		}
		
		public int getNpcIdLight() {
			return npcIdLight;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public SummonStatsTemplate getTemplate() {
			return template;
		}
	}
	
	private static int makeHash(int npcId, int level) {
		return npcId << 10 | level;
	}
}