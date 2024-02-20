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
package com.aionemu.gameserver.model.templates.panels;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SkillPanel")
public class SkillPanel
{
    @XmlAttribute(name="panel_id")
    protected byte id;
	
    @XmlAttribute(name="panel_skills")
    protected List<Integer> skills;
	
    public int getPanelId() {
        return id;
    }
	
    public List<Integer> getSkills() {
        return null;
    }
	
    public boolean canUseSkill(int skillId, int level) {
        for (Integer skill: skills) {
            if (skill >> 8 == skillId && (skill & 0xFF) == level) {
                return true;
            }
        }
        return false;
    }
}