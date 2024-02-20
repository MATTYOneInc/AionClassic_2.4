package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;

public class FindGroup
{
    private AionObject object;
    private String message;
    private int groupType;
    private int minMembers;
    private int instanceId;
    private int lastUpdate = (int)(System.currentTimeMillis() / 1000);
	
    public FindGroup(AionObject object, String message, int groupType) {
        this.object = object;
        this.message = message;
        this.groupType = groupType;
    }
	
    public String getMessage() {
        return this.message;
    }
	
    public void setMessage(String message) {
        this.lastUpdate = (int)(System.currentTimeMillis() / 1000);
        this.message = message;
    }
	
    public int getGroupType() {
        return this.groupType;
    }
	
    public int getObjectId() {
        return this.object.getObjectId();
    }
	
    public int getInstanceId() {
        return this.instanceId;
    }
	
    public int getMinMembers() {
        return this.minMembers;
    }
	
    public int getClassId() {
        if (this.object instanceof Player) {
            return ((Player)this.object).getPlayerClass().getClassId();
        } if (this.object instanceof PlayerAlliance) {
            ((Player)((PlayerAlliance)this.object).getLeaderObject()).getCommonData().getPlayerClass();
        } else if (this.object instanceof PlayerGroup) {
            ((Player)((PlayerGroup)this.object).getLeaderObject()).getPlayerClass();
        }
        return 0;
    }
	
    public int getMinLevel() {
        if (this.object instanceof Player) {
            return ((Player)this.object).getLevel();
        } if (this.object instanceof PlayerAlliance) {
            int minLvl = 1;
            for (Player member : ((PlayerAlliance)this.object).getMembers()) {
                int memberLvl = member.getCommonData().getLevel();
                if (memberLvl >= minLvl) {
				    minLvl = memberLvl;
				}
            }
            return minLvl;
        } if (this.object instanceof PlayerGroup) {
            return ((PlayerGroup)this.object).getMinExpPlayerLevel();
        } if (this.object instanceof TemporaryPlayerTeam) {
            return ((TemporaryPlayerTeam)this.object).getMinExpPlayerLevel();
        }
        return 1;
    }
	
    public int getMaxLevel() {
        if (this.object instanceof Player) {
            return ((Player)this.object).getLevel();
        } if (this.object instanceof PlayerAlliance) {
            int maxLvl = 99;
            for (Player member : ((PlayerAlliance)this.object).getMembers()) {
                int memberLvl = member.getCommonData().getLevel();
                if (memberLvl <= maxLvl) {
                    maxLvl = memberLvl;
				}
            }
            return maxLvl;
        } if (this.object instanceof PlayerGroup) {
            return ((PlayerGroup)this.object).getMaxExpPlayerLevel();
        } if (this.object instanceof TemporaryPlayerTeam) {
            return ((TemporaryPlayerTeam)this.object).getMaxExpPlayerLevel();
        }
        return 1;
    }
	
    public int getUnk() {
        if (this.object instanceof Player) {
            return 65557;
        }
        return 0;
    }
	
    public int getLastUpdate() {
        return this.lastUpdate;
    }
	
    public String getName() {
        if (this.object instanceof Player) {
            return this.object.getName();
        } if (this.object instanceof PlayerAlliance) {
            return ((Player)((PlayerAlliance)this.object).getLeaderObject()).getCommonData().getName();
        } if (this.object instanceof PlayerGroup) {
            return ((Player)((PlayerGroup)this.object).getLeaderObject()).getName();
        }
        return "";
    }
	
    public int getSize() {
        if (this.object instanceof Player) {
            return 1;
        } if (this.object instanceof PlayerAlliance) {
            return ((PlayerAlliance)this.object).size();
        } if (this.object instanceof PlayerGroup) {
            return ((PlayerGroup)this.object).size();
        }
        return 1;
    }
}