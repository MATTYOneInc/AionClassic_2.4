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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_ADD_SKILL extends AionServerPacket
{
	private PlayerSkillEntry[] skillList;
	private int messageId;
	private int skillNameId;
	private String skillLvl;
	public static final int YOU_LEARNED_SKILL = 1300050;
	boolean isNew = false;
	private Player player;
	private int state;
	
	public S_ADD_SKILL(Player player) {
		this.player = player;
		this.skillList = player.getSkillList().getAllSkills();
		this.messageId = 0;
	}

	public S_ADD_SKILL(PlayerSkillEntry skillListEntry, int messageId, boolean isNew) {
		this.skillList = new PlayerSkillEntry[] { skillListEntry };
		this.messageId = messageId;
		this.skillNameId = DataManager.SKILL_DATA.getSkillTemplate(skillListEntry.getSkillId()).getNameId();
		this.skillLvl = String.valueOf(skillListEntry.getSkillLevel());
		this.isNew = isNew;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		final int size = skillList.length;
		writeH(size);
		if (size > 0) {
			for (PlayerSkillEntry entry : skillList) {
				writeH(entry.getSkillId());
				writeH(entry.getSkillLevel());
				writeC(0x00);
				int extraLevel = entry.getExtraLvl();
				writeC(extraLevel);
				if (isNew && extraLevel == 0 && !entry.isStigma()) {
					writeD((int) (System.currentTimeMillis() / 1000));
				} else {
					writeD(0);
				} if (entry.isStigma()) {
					writeC(1);
				} else {
					writeC(0);
				}
			}
		}
		writeD(messageId);
		if (messageId != 0) {
			writeH(0x24);
			writeD(skillNameId);
			writeH(0x00);
			writeS(skillLvl);
		}
	}
}