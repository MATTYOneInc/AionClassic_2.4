package com.aionemu.gameserver.model.templates;

public class CraftLearnTemplate
{
  private int skillId;
  private boolean isCraftSkill;

  public boolean isCraftSkill()
  {
    return isCraftSkill;
  }

  public CraftLearnTemplate(int skillId, boolean isCraftSkill, String skillName) {
    this.skillId = skillId;
    this.isCraftSkill = isCraftSkill;
  }

  public int getSkillId()
  {
    return skillId;
  }
}