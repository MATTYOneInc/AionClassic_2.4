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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.XPLossEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

public class PlayerCommonData extends VisibleObjectTemplate
{
	static Logger log = LoggerFactory.getLogger(PlayerCommonData.class);
	private final int playerObjId;
	private Race race;
	private String name;
	private PlayerClass playerClass;
	private int level = 0;
	private long exp = 0;
	private long expRecoverable = 0;
	private Gender gender;
	private Timestamp lastOnline = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
	private Timestamp lastStamp = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
	private boolean online;
	private String note;
	private WorldPosition position;
	private int questExpands = 0;
	private int npcExpands = AdvCustomConfig.CUBE_SIZE;
	private int warehouseSize = 0;
	private int advencedStigmaSlotSize = 0;
	private int titleId = -1;
	private int dp = 0;
	private int mailboxLetters;
	private int soulSickness = 0;
	private boolean noExp = false;
	private long reposteCurrent;
	private long reposteMax = 67770848L;
	private long salvationPoint;
	private int mentorFlagTime;
	private int worldOwnerId;
	private BoundRadius boundRadius;
	private long lastTransferTime;
	
	public PlayerCommonData(int objId) {
		this.playerObjId = objId;
	}

	public int getPlayerObjId() {
		return playerObjId;
	}

	public long getExp() {
		return this.exp;
	}

	public int getQuestExpands() {
		return this.questExpands;
	}

	public void setQuestExpands(int questExpands) {
		this.questExpands = questExpands;
	}

	public void setNpcExpands(int npcExpands) {
		this.npcExpands = npcExpands;
	}

	public int getNpcExpands() {
		return npcExpands;
	}

	/**
	 * @return the advencedStigmaSlotSize
	 */
	public int getAdvencedStigmaSlotSize() {
		return advencedStigmaSlotSize;
	}

	/**
	 * @param advencedStigmaSlotSize the advencedStigmaSlotSize to set
	 */
	public void setAdvencedStigmaSlotSize(int advencedStigmaSlotSize) {
		this.advencedStigmaSlotSize = advencedStigmaSlotSize;
	}

	public long getExpShown() {
        return this.exp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
    }
	
    public long getExpNeed() {
		if (this.level == DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
			return 0;
		}
		return DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level + 1) - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
	}

	/**
	 * calculate the lost experience must be called before setexp
	 *
	 * @author Jangan
	 */
	public void calculateExpLoss() {
		long expLost = XPLossEnum.getExpLoss(this.level, this.getExpNeed());
		int unrecoverable = (int) (expLost * 0.33333333);
		int recoverable = (int) expLost - unrecoverable;
		long allExpLost = recoverable + this.expRecoverable;
		if (this.getExpShown() > unrecoverable) {
			this.exp = this.exp - unrecoverable;
		} else {
			this.exp = this.exp - this.getExpShown();
		} if (this.getExpShown() > recoverable) {
			this.expRecoverable = allExpLost;
			this.exp = this.exp - recoverable;
		} else {
			this.expRecoverable = this.expRecoverable + this.getExpShown();
			this.exp = this.exp - this.getExpShown();
		} if (this.getPlayer() != null) {
			PacketSendUtility.sendPacket(getPlayer(), new S_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy()));
		}
	}

	public void setRecoverableExp(long expRecoverable) {
        this.expRecoverable = expRecoverable;
    }

    public void resetRecoverableExp() {
        long el = this.expRecoverable;
        this.expRecoverable = 0;
        this.setExp(this.exp + el);
    }

    public long getExpRecoverable() {
        return this.expRecoverable;
    }
	
	public void addExp(long value, int npcNameId) {
		this.addExp(value, null, npcNameId, "", 0);
	}
	public void addExp(long value, RewardType rewardType) {
		this.addExp(value, rewardType, 0, "", 0);
	}
	public void addExp(long value, RewardType rewardType, int npcNameId) {
		this.addExp(value, rewardType, npcNameId, "", 0);
	}
	public void addExp(long value, RewardType rewardType, int npcNameId, int questId) {
		this.addExp(value, rewardType, npcNameId, "", questId);
	}
	public void addExp(long value, RewardType rewardType, String name) {
		this.addExp(value, rewardType, 0, name, 0);
	}
	
	public void addExp(long value, RewardType rewardType, int npcNameId, String name, int questId) {
		if (this.noExp) {
			return;
		}
		long reward = value;
		if ((getPlayer() != null) && (rewardType != null)) {
            reward = rewardType.calcReward(getPlayer(), value);
        }
		long repose = 0;
		if ((isReadyForReposteEnergy()) && (getCurrentReposteEnergy() > 0)) {
            if (rewardType != null && rewardType == RewardType.HUNTING || rewardType == RewardType.GROUP_HUNTING || rewardType == RewardType.QUEST) {
                repose = (long) ((reward / 100f) * 40);
				addReposteEnergy(-repose);
            }
        }
		long salvation = 0;
		if ((isReadyForSalvationPoints()) && (getCurrentSalvationPercent() > 0)) {
			if (rewardType != null && rewardType == RewardType.CRAFTING || rewardType == RewardType.GATHERING || rewardType == RewardType.QUEST) {
				salvation = (long) (reward / 100f * getCurrentSalvationPercent());
			}
		}
		reward = reward + repose;
		setExp(exp + reward);
		if ((getPlayer() != null) && (rewardType != null)) {
			switch (rewardType) {
				case HUNTING:
				case GROUP_HUNTING:
					if ((repose > 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose, salvation));
					} else if ((repose > 0) && (salvation == 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose));
					} else if ((repose == 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Salvation %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, salvation));
					} else {
						///You have gained %num1 XP from %0.
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_DESC(new DescriptionId(npcNameId * 2 + 1), reward));
					}
				break;
				case QUEST:
				case CRAFTING:
				case GATHERING:
				    if (npcNameId == 0) {
						///You have gained %num1 XP.
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP2(reward));
					} else if ((repose > 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose, salvation));
					} else if ((repose > 0) && (salvation == 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose));
					} else if ((repose == 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Salvation %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, salvation));
					} else {
						///You have gained %num1 XP from %0.
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_DESC(new DescriptionId(npcNameId * 2 + 1), reward));
					}
				break;
				case PVP_KILL:
					if ((repose > 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_MAKEUP_BONUS(name, reward, repose, salvation));
					} else if ((repose > 0) && (salvation == 0)) {
						///You have gained %num1 XP from %0 (Energy of Repose %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_VITAL_BONUS(name, reward, repose));
					} else if ((repose == 0) && (salvation > 0)) {
						///You have gained %num1 XP from %0 (Energy of Salvation %num2).
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP_MAKEUP_BONUS(name, reward, salvation));
					} else {
						///You have gained %num1 XP from %0.
						PacketSendUtility.sendPacket(getPlayer(), S_MESSAGE_CODE.STR_GET_EXP(name, reward));
					}
				break;
			}
		}
	}

	public boolean isReadyForReposteEnergy() {
        return this.level >= 10;
    }

	public boolean isReadyForSalvationPoints() {
		return this.level >= 15;
	}

	public void addReposteEnergy(long add) {
        if (!this.isReadyForReposteEnergy()) {
            return;
        }
        reposteCurrent += add;
        if (reposteCurrent < 0) {
            reposteCurrent = 0;
        } else if (reposteCurrent > getMaxReposteEnergy()) {
            reposteCurrent = getMaxReposteEnergy();
        }
    }

	public void updateMaxReposte() {
        if (!isReadyForReposteEnergy()) {
            reposteCurrent = 0;
            reposteMax = 0;
        } else {
            reposteMax = (long) (getExpNeed() * 0.25f); //Retail 99%
        }
    }

	public void setCurrentReposteEnergy(long value) {
        reposteCurrent = value;
    }

	public long getCurrentReposteEnergy() {
        return isReadyForReposteEnergy() ? this.reposteCurrent : 0;
    }

    public long getMaxReposteEnergy() {
        return isReadyForReposteEnergy() ? this.reposteMax : 0;
    }
	
	public void setExp(long exp) {
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);
		if (getPlayerClass() != null && getPlayerClass().isStartingClass() && this.playerClass != PlayerClass.MONK) {
			maxLevel = GSConfig.STARTING_LEVEL > GSConfig.STARTCLASS_MAXLEVEL ? GSConfig.STARTING_LEVEL : GSConfig.STARTCLASS_MAXLEVEL;
			if (this.getLevel() == 9 && this.getExp() >= 213454) {
				///You can advance to level 10 only after you have completed the class change quest.
				PacketSendUtility.sendPacket(this.getPlayer(), S_MESSAGE_CODE.STR_LEVEL_LIMIT_QUEST_NOT_FINISHED1);
			}
		} else if (getPlayerClass() != null && getPlayerClass().isStartingClass() && this.playerClass == PlayerClass.MONK) {
			maxLevel = GSConfig.STARTING_LEVEL > GSConfig.STARTCLASS_MONK_MAXLEVEL ? GSConfig.STARTING_LEVEL : GSConfig.STARTCLASS_MONK_MAXLEVEL;
			if (this.getLevel() == 15 && this.getExp() >= 1418170) {
				///You can advance to level 16 only after you have completed the class change quest.
				PacketSendUtility.sendPacket(this.getPlayer(), S_MESSAGE_CODE.STR_LEVEL_LIMIT_QUEST_NOT_FINISHED2);
			}
		} if (exp > maxExp) {
			exp = maxExp;
		}
		int oldLvl = this.level;
		this.exp = exp;
		boolean up = false;
		while ((this.level + 1) < maxLevel
		    && (up = exp >= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level + 1)) || (this.level - 1) >= 0
		    && exp < DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level)) {
			if (up) {
				this.level++;
			} else {
				this.level--;
			}
			upgradePlayerData();
		} if (this.getPlayer() != null) {
			if (oldLvl != level) {
				updateMaxReposte();
			}
			PacketSendUtility.sendPacket(this.getPlayer(), new S_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy()));
		}
	}

	private void upgradePlayerData() {
        Player player = getPlayer();
        if (player != null) {
            player.getController().upgradePlayer();
        }
    }

	public void setNoExp(boolean value) {
		this.noExp = value;
	}

	public boolean getNoExp() {
		return noExp;
	}

	/**
	 * @return Race as from template
	 */
	public final Race getRace() {
		return race;
	}

	public Race getOppositeRace() {
		return race == Race.ELYOS ? Race.ASMODIANS : Race.ELYOS;
	}

	/**
	 * @return the mentorFlagTime
	 */
	public int getMentorFlagTime() {
		return mentorFlagTime;
	}

	public boolean isHaveMentorFlag() {
		return mentorFlagTime > System.currentTimeMillis() / 1000;
	}

	/**
	 * @param mentorFlagTime the mentorFlagTime to set
	 */
	public void setMentorFlagTime(int mentorFlagTime) {
		this.mentorFlagTime = mentorFlagTime;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public WorldPosition getPosition() {
		return position;
	}

	public Timestamp getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Timestamp timestamp) {
		lastOnline = timestamp;
	}

	public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level <= DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
            this.setExp(DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level));
        }
    }

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	/**
	 * This method should be called exactly once after creating object of this class
	 *
	 * @param position
	 */
	public void setPosition(WorldPosition position) {
		if (this.position != null) {
			throw new IllegalStateException("position already set");
		}
		this.position = position;
	}

	/**
	 * Gets the cooresponding Player for this common data. Returns null if the player is not online
	 *
	 * @return Player or null
	 */
	public Player getPlayer() {
		if (online && getPosition() != null) {
			return World.getInstance().findPlayer(playerObjId);
		}
		return null;
	}

	public void addDp(int dp) {
		setDp(this.dp + dp);
	}

	/**
	 * //TODO move to lifestats -> db save?
	 *
	 * @param dp
	 */
	public void setDp(int dp) {
		if (getPlayer() != null) {
			if (playerClass.isStartingClass())
				return;

			int maxDp = getPlayer().getGameStats().getMaxDp().getCurrent();
			this.dp = dp > maxDp ? maxDp : dp;

			PacketSendUtility.broadcastPacket(getPlayer(), new S_DP_USER(playerObjId, this.dp), true);
			getPlayer().getGameStats().updateStatsAndSpeedVisually();
			PacketSendUtility.sendPacket(getPlayer(), new S_DP(this.dp));
		}
		else {
			log.debug("CHECKPOINT : getPlayer in PCD return null for setDP " + isOnline() + " " + getPosition());
		}
	}

	public int getDp() {
		return this.dp;
	}

	@Override
	public int getTemplateId() {
		return 100000 + race.getRaceId() * 2 + gender.getGenderId();
	}

	@Override
	public int getNameId() {
		return 0;
	}

	/**
	 * @param warehouseSize the warehouseSize to set
	 */
	public void setWarehouseSize(int warehouseSize) {
		this.warehouseSize = warehouseSize;
	}

	/**
	 * @return the warehouseSize
	 */
	public int getWarehouseSize() {
		return warehouseSize;
	}

	public void setMailboxLetters(int count) {
		this.mailboxLetters = count;
	}

	public int getMailboxLetters() {
		return mailboxLetters;
	}

	/**
	 * @param boundRadius
	 */
	public void setBoundingRadius(BoundRadius boundRadius) {
		this.boundRadius = boundRadius;
	}

	@Override
	public BoundRadius getBoundRadius() {
		return boundRadius;
	}

	public void setDeathCount(int count) {
		this.soulSickness = count;
	}

	public int getDeathCount() {
		return this.soulSickness;
	}
	
	//Energy of Repose will now start from level 15 instead of 20 and gives the following boost to XP: Levels 15–44: 40% bonusLevels 45–50: 30% bonus 
	public byte getCurrentSalvationPercent() {
		if (salvationPoint <= 0) {
			return 0;
		}
		long per = salvationPoint / 1000;
		if (level >= 15 && level <= 44) {
			if (per > 40) {
				return 40;
			}
		} else if (level >= 45 && level <= 55) {
			if (per > 30) {
				return 30;
			}
		}
		return (byte) per;
	}

	public void addSalvationPoints(long points) {
		salvationPoint += points;
	}

	public void resetSalvationPoints() {
		salvationPoint = 0;
	}

	public void setLastTransferTime(long value) {
		this.lastTransferTime = value;
	}

	public long getLastTransferTime() {
		return this.lastTransferTime;
	}

	public int getWorldOwnerId() {
		return worldOwnerId;
	}

	public void setWorldOwnerId(int worldOwnerId) {
		this.worldOwnerId = worldOwnerId;
	}


	private int wardrobeSlot;

	public int getWardrobeSlot() {
		return wardrobeSlot;
	}

	public void setWardrobeSlot(int wardrobeSlot) {
		this.wardrobeSlot = wardrobeSlot;
	}

	private int joinRequestLegionId = 0;
	private int guildCoins = 0;

	private LegionJoinRequestState joinRequestState = LegionJoinRequestState.NONE;

	public int getJoinRequestLegionId() {
		return joinRequestLegionId;
	}

	public void setJoinRequestLegionId(int joinRequestLegionId) {
		this.joinRequestLegionId = joinRequestLegionId;
	}

	public LegionJoinRequestState getJoinRequestState() {
		return joinRequestState;
	}

	public void setJoinRequestState(LegionJoinRequestState joinRequestState) {
		this.joinRequestState = joinRequestState;
	}

	public int getGuildCoins() {
		return guildCoins;
	}

	public void setGuildCoins(int guildCoins) {
		this.guildCoins = guildCoins;
	}

	private PlayerUpgradeArcade upgradeArcade;
	public PlayerUpgradeArcade getUpgradeArcade() {
		if (upgradeArcade == null) {
			this.upgradeArcade = new PlayerUpgradeArcade();
		}
		return upgradeArcade;
	}

	public void setUpgradeArcade(PlayerUpgradeArcade upgradeArcade) {
		this.upgradeArcade = upgradeArcade;
	}

}