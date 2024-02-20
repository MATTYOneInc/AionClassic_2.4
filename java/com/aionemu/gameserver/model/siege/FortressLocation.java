package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVE_NEW;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import java.util.*;

public class FortressLocation extends SiegeLocation
{
    private boolean status;
	protected boolean isUnderShield;
	protected boolean isCanTeleport;
	protected boolean isUnderAssault;
	protected List<SiegeReward> siegeRewards;
	
	public FortressLocation() {
	}
	
	public FortressLocation(SiegeLocationTemplate template) {
		super(template);
		this.siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
	}
	
	public List<SiegeReward> getReward() {
		return this.siegeRewards;
	}
	
	public void setPreparation(boolean status) {
        this.status = status;
    }
	
	public boolean isEnemy(Creature creature) {
		return creature.getRace().getRaceId() != getRace().getRaceId();
	}
	
	@Override
	public boolean isUnderShield() {
		return this.isUnderShield;
	}
	
	@Override
	public void setUnderShield(boolean value) {
		this.isUnderShield = value;
	}
	
	@Override
	public boolean isCanTeleport(Player player) {
	    if (player == null) {
		    return isCanTeleport;
		}
		return isCanTeleport && player.getRace().getRaceId() == getRace().getRaceId();
	}
	
	@Override
	public void setCanTeleport(boolean status) {
		this.isCanTeleport = status;
	}
	
	public DescriptionId getNameAsDescriptionId() {
		return new DescriptionId(template.getNameId());
	}
	
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		super.onEnterZone(creature, zone);
		if (isVulnerable()) {
		    creature.setInsideZoneType(ZoneType.SIEGE);
		}
	}
	
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		super.onLeaveZone(creature, zone);
		if (this.isVulnerable()) {
			creature.unsetInsideZoneType(ZoneType.SIEGE);
		}
	}
	
	public WorldPosition getEntryPositionE() {
        WorldPosition posE = new WorldPosition(getWorldId());
        switch (getLocationId()) {
            //Abyss Core
			case 1011:
			    posE.setXYZH(1852.966064f, 1989.645752f, 2271.097168f, (byte) 0);
            break;
			//Reshanta
			case 1131:
			    posE.setXYZH(3459.779785f, 2875.269287f, 1523.073486f, (byte) 0);
            break;
			case 1132:
			    posE.setXYZH(2984.163330f, 3351.331299f, 1529.111572f, (byte) 0);
            break;
            case 1141:
			    posE.setXYZH(1570.848022f, 1146.073730f, 1502.494751f, (byte) 0);
            break;
			case 1211:
			    posE.setXYZH(2478.195801f, 735.536499f, 2869.608643f, (byte) 0);
            break;
			case 1221:
			    posE.setXYZH(2163.708496f, 1563.099854f, 2966.079834f, (byte) 0);
            break;
			case 1231:
			    posE.setXYZH(2719.578369f, 2276.097168f, 2984.000000f, (byte) 0);
            break;
			case 1241:
			    posE.setXYZH(1646.738770f, 2382.830566f, 2922.303955f, (byte) 0);
            break;
			case 1251:
                posE.setXYZH(1021.069580f, 3197.358398f, 2933.107422f, (byte) 0);
            break;
			//Inggison
			case 2011:
			    posE.setXYZH(1799.0000f, 1946.0000f, 298.0000f, (byte) 0);
            break;
			case 2021:
                posE.setXYZH(783.0000f, 1745.0000f, 321.0000f, (byte) 0);
            break;
			//Gelkmaros
			case 3011:
			    posE.setXYZH(1184.0000f, 1107.0000f, 281.0000f, (byte) 0);
            break;
			case 3021:
                posE.setXYZH(1882.0000f, 1303.0000f, 312.0000f, (byte) 0);
            break;
        }
        return posE;
    }
	
	public WorldPosition getEntryPositionA() {
        WorldPosition posA = new WorldPosition(getWorldId());
        switch (getLocationId()) {
            //Abyss Core
			case 1011:
			    posA.setXYZH(1852.966064f, 1989.645752f, 2271.097168f, (byte) 0);
            break;
			//Reshanta
			case 1131:
			    posA.setXYZH(3459.779785f, 2875.269287f, 1523.073486f, (byte) 0);
            break;
			case 1132:
			    posA.setXYZH(2984.163330f, 3351.331299f, 1529.111572f, (byte) 0);
            break;
            case 1141:
			    posA.setXYZH(1570.848022f, 1146.073730f, 1502.494751f, (byte) 0);
            break;
			case 1211:
			    posA.setXYZH(2478.195801f, 735.536499f, 2869.608643f, (byte) 0);
            break;
			case 1221:
			    posA.setXYZH(2163.708496f, 1563.099854f, 2966.079834f, (byte) 0);
            break;
			case 1231:
			    posA.setXYZH(2719.578369f, 2276.097168f, 2984.000000f, (byte) 0);
            break;
			case 1241:
			    posA.setXYZH(1646.738770f, 2382.830566f, 2922.303955f, (byte) 0);
            break;
			case 1251:
                posA.setXYZH(1021.069580f, 3197.358398f, 2933.107422f, (byte) 0);
            break;
			//Inggison
			case 2011:
			    posA.setXYZH(1799.0000f, 1946.0000f, 298.0000f, (byte) 0);
            break;
			case 2021:
                posA.setXYZH(783.0000f, 1745.0000f, 321.0000f, (byte) 0);
            break;
			//Gelkmaros
			case 3011:
			    posA.setXYZH(1184.0000f, 1107.0000f, 281.0000f, (byte) 0);
            break;
			case 3021:
                posA.setXYZH(1882.0000f, 1303.0000f, 312.0000f, (byte) 0);
            break;
        }
        return posA;
    }
	
	public void clearLocation() {
		for (Player player: getPlayers().values()) {
            if (player.getRace() == Race.ELYOS) {
				WorldPosition posE = getEntryPositionE();
				World.getInstance().updatePosition(player, posE.getX(), posE.getY(), posE.getZ() + 5, posE.getHeading());
				PacketSendUtility.broadcastPacketAndReceive(player, new S_MOVE_NEW(player));
			} else {
				WorldPosition posA = getEntryPositionA();
				World.getInstance().updatePosition(player, posA.getX(), posA.getY(), posA.getZ() + 5, posA.getHeading());
				PacketSendUtility.broadcastPacketAndReceive(player, new S_MOVE_NEW(player));
			}
        } for (Creature creature: getCreatures().values()) {
			if ((isEnemy(creature)) && ((creature instanceof Kisk))) {
				Kisk kisk = (Kisk)creature;
				kisk.getController().die();
			}
		}
    }
}