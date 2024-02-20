/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.skill;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADD_SKILL;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class PlayerSkillList implements SkillList<Player>
{
	private static final Logger log = LoggerFactory.getLogger(PlayerSkillList.class);
	private final Map<Integer, PlayerSkillEntry> basicSkills;
	private final Map<Integer, PlayerSkillEntry> stigmaSkills;

	private final List<PlayerSkillEntry> deletedSkills;

	public PlayerSkillList() {
		this.basicSkills = new HashMap<Integer, PlayerSkillEntry>(0);
		this.stigmaSkills = new HashMap<Integer, PlayerSkillEntry>(0);
		this.deletedSkills = new ArrayList<PlayerSkillEntry>(0);
	}

	public PlayerSkillList(List<PlayerSkillEntry> skills) {
		this();
		for (PlayerSkillEntry entry : skills) {
			if (entry.isStigma())
				stigmaSkills.put(entry.getSkillId(), entry);
			else
				basicSkills.put(entry.getSkillId(), entry);
		}
	}

	/**
	 * Returns array with all skills
	 */
	public PlayerSkillEntry[] getAllSkills() {
		List<PlayerSkillEntry> allSkills = new ArrayList<PlayerSkillEntry>();
		allSkills.addAll(basicSkills.values());
		allSkills.addAll(stigmaSkills.values());
		return allSkills.toArray(new PlayerSkillEntry[allSkills.size()]);
	}
	
	public List<Integer> getAllSkills2() {
		HashSet<Integer> allSkills = new HashSet<Integer>();
		for (PlayerSkillEntry i : basicSkills.values()) {
			allSkills.add(i.getSkillId());
		}
		for (PlayerSkillEntry i : stigmaSkills.values()) {
			allSkills.add(i.getSkillId());
		}
		return Arrays.asList(allSkills.toArray(new Integer[0]));
	}

	public PlayerSkillEntry[] getBasicSkills() {
		return basicSkills.values().toArray(new PlayerSkillEntry[basicSkills.size()]);
	}

	public PlayerSkillEntry[] getStigmaSkills() {
		return stigmaSkills.values().toArray(new PlayerSkillEntry[stigmaSkills.size()]);
	}
	
	public PlayerSkillEntry[] getDeletedSkills() {
		return deletedSkills.toArray(new PlayerSkillEntry[deletedSkills.size()]);
	}

	public PlayerSkillEntry getSkillEntry(int skillId) {
		if (basicSkills.containsKey(skillId))
			return basicSkills.get(skillId);
		return stigmaSkills.get(skillId);
	}

	@Override
	public boolean addSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, false, PersistentState.NEW);
	}

	public boolean addStigmaSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, true, PersistentState.NOACTION);
	}

	/**
	 * Add temporary skill which will not be saved in db
	 * 
	 * @param player
	 * @param skillId
	 * @param skillLevel
	 * @return
	 */
	public boolean addAbyssSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, false, PersistentState.NOACTION);
	}

	public void addStigmaSkill(Player player, int skillId, int skillLevel, boolean equipedByNpc) {
		PlayerSkillEntry skill = new PlayerSkillEntry(skillId, true, skillLevel, PersistentState.NOACTION);
		this.stigmaSkills.put(skillId, skill);
		if (equipedByNpc) {
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(skill, 1300401, false));
		}
	}

	public void addStigmaSkill(Player player, int skillId, int skillLevel, boolean withMsg, boolean equipedByNpc) {
		PlayerSkillEntry skill = new PlayerSkillEntry(skillId, true, skillLevel, PersistentState.NOACTION);
		this.stigmaSkills.put(skillId, skill);
		if (equipedByNpc) {
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(skill, withMsg ? 1300401 : 0, false));
		}
	}

	private synchronized boolean addSkill(Player player, int skillId, int skillLevel, boolean isStigma, PersistentState state) {
		PlayerSkillEntry existingSkill = isStigma ? stigmaSkills.get(skillId) : basicSkills.get(skillId);
		boolean isNew = false;
		if (existingSkill != null) {
			existingSkill.setSkillLvl(skillLevel);
		} else {
			if (isStigma) {
				stigmaSkills.put(skillId, new PlayerSkillEntry(skillId, true, skillLevel, state));
			} else {
				basicSkills.put(skillId, new PlayerSkillEntry(skillId, false, skillLevel, state));
				isNew = true;
			}
		} if (player.isSpawned()) {
			sendMessage(player, skillId, isNew);
		}
		PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
		return true;
	}

	public boolean isCraftSkill(int skilId) {
		switch (skilId) {
			case 30001:
			case 30002:
			case 30003:
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
				return true;
			default:
				return false;
		}
	}

	/**
	 * @param player
	 * @param skillId
	 * @param xpReward
	 * @return
	 */
	public boolean addSkillXp(Player player, int skillId, int xpReward, int objSkillPoints) {
		PlayerSkillEntry skillEntry = getSkillEntry(skillId);
		int maxDiff = 40;
		int SkillLvlDiff = skillEntry.getSkillLevel() - objSkillPoints;
		if (maxDiff < SkillLvlDiff) {
			return false;
		} switch (skillEntry.getSkillId()) {
			case 30001:
				if (skillEntry.getSkillLevel() == 49) {
					return false;
				}
			case 30002:
			case 30003:
				if (skillEntry.getSkillLevel() == 499) {
					return false;
				}
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40007:
			case 40008:
				switch (skillEntry.getSkillLevel()) {
					case 99:
					case 199:
					case 299:
					case 399:
					case 449:
					case 499:
					case 549:
					    return false;
				}
				player.getRecipeList().autoLearnRecipe(player, skillId, skillEntry.getSkillLevel());
		}
		boolean updateSkill = skillEntry.addSkillXp(player, xpReward);
		if (updateSkill) {
			sendMessage(player, skillId, false);
		}
		return true;
	}

	@Override
	public boolean isSkillPresent(int skillId) {
		return basicSkills.containsKey(skillId) || stigmaSkills.containsKey(skillId);
	}

	@Override
	public int getSkillLevel(int skillId) {
		if (basicSkills.containsKey(skillId))
			return basicSkills.get(skillId).getSkillLevel();
		return stigmaSkills.get(skillId).getSkillLevel();
	}

	@Override
	public synchronized boolean removeSkill(int skillId) {
		PlayerSkillEntry entry = basicSkills.get(skillId);
		if (entry == null) {
			entry = stigmaSkills.get(skillId);
		}
		if (entry != null) {
			entry.setPersistentState(PersistentState.DELETED);
			deletedSkills.add(entry);
			basicSkills.remove(skillId);
			stigmaSkills.remove(skillId);
		}
		return entry != null;
	}

	@Override
	public int size() {
		return basicSkills.size() + stigmaSkills.size();
	}

	/**
	 * @param player
	 * @param skillId
	 */
	private void sendMessage(Player player, int skillId, boolean isNew) {
		switch (skillId) {
			case 30001:
			case 30002:
			case 30003:
				PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player.getSkillList().getSkillEntry(skillId), 1330005, false));
			break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
				PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player.getSkillList().getSkillEntry(skillId), 1330061, false));
			break;
		    default:
			if (player.getSkillList().getSkillEntry(skillId).getSkillLevel() > 1) {
				PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player.getSkillList().getSkillEntry(skillId), 0, isNew));
			} else {
				PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player.getSkillList().getSkillEntry(skillId), 1300050, isNew));
			}
		}
	}
}