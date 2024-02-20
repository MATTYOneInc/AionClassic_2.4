package com.aionemu.gameserver.questEngine.handlers.models;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.MonsterHunt;
import javolution.util.FastMap;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonsterHuntData", propOrder = {"monster"})
@XmlSeeAlso({KillSpawnedData.class, MentorMonsterHuntData.class})
public class MonsterHuntData extends XMLQuest
{
	@XmlElement(name = "monster", required = true)
    protected List<Monster> monster;
	
    @XmlAttribute(name = "start_npc_ids", required = true)
    protected List<Integer> startNpcIds;
	
    @XmlAttribute(name = "end_npc_ids")
    protected List<Integer> endNpcIds;
	
    @XmlAttribute(name = "start_dialog_id")
    protected int startDialog;
	
    @XmlAttribute(name = "end_dialog_id")
    protected int endDialog;
	
    @XmlAttribute(name = "aggro_start_npcs")
    protected List<Integer> aggroNpcs;
	
	@Override
	public void register(QuestEngine questEngine) {
		FastMap<Monster, Set<Integer>> monsterNpcs = new FastMap<Monster, Set<Integer>>();
		for (Monster m : monster) {
			monsterNpcs.put(m, new HashSet<Integer>(m.getNpcIds()));
		}
		MonsterHunt template = new MonsterHunt(id, startNpcIds, endNpcIds, monsterNpcs, startDialog, endDialog, aggroNpcs);
        questEngine.addQuestHandler(template);
	}
}