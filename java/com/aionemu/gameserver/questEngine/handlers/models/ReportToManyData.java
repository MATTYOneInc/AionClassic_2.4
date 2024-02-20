package com.aionemu.gameserver.questEngine.handlers.models;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.ReportToMany;
import javolution.util.FastMap;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportToManyData")
public class ReportToManyData extends XMLQuest
{
	@XmlAttribute(name = "start_item_id")
	protected int startItemId;
	
	@XmlAttribute(name = "start_item_id2")
	protected int startItemId2;
	
	@XmlAttribute(name = "start_npc_ids")
	protected List<Integer> startNpcIds;
	
	@XmlAttribute(name = "end_npc_ids")
	protected List<Integer> endNpcIds;
	
	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;
	
	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;
	
	@XmlElement(name = "npc_infos", required = true)
	protected List<NpcInfos> npcInfos;
	
	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	
	@Override
    public void register(QuestEngine questEngine) {
        int maxVar = 0;
        FastMap<Integer, NpcInfos> NpcInfo = new FastMap<Integer, NpcInfos>();
        for (NpcInfos mi: npcInfos) {
            NpcInfo.put(mi.getNpcId(), mi);
            if (mi.getVar() > maxVar) {
                maxVar = mi.getVar();
            }
        }
        ReportToMany template = new ReportToMany(id, itemId, startItemId, startItemId2, startNpcIds, endNpcIds, NpcInfo, startDialog, endDialog, maxVar, mission);
        questEngine.addQuestHandler(template);
    }
}