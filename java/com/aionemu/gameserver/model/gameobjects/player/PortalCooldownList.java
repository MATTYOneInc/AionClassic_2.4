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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANCE_DUNGEON_COOLTIMES;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

public class PortalCooldownList {

    private Player owner;
    private FastMap<Integer, PortalCooldownItem> portalCooldowns;

    /**
     * @param owner
     */
    PortalCooldownList(Player owner) {
        this.owner = owner;
    }

    /**
     * @param worldId * @return
     */
    public boolean isPortalUseDisabled(int worldId) {
        if (portalCooldowns == null || !portalCooldowns.containsKey(worldId)) {
            return false;
        }
        PortalCooldownItem coolDown = portalCooldowns.get(worldId);
        if (coolDown == null) {
            return false;
        }
        if (DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(worldId) == 0 || coolDown.getEntryCount() < DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(worldId)) {
            return false;
        }
        if (coolDown.getCooldown() < System.currentTimeMillis()) {
            portalCooldowns.remove(worldId);
            return false;
        }
        return true;
    }

    /**
     * @param worldId
     * @return
     */
    public long getPortalCooldown(int worldId) {
        if (portalCooldowns == null || !portalCooldowns.containsKey(worldId)) {
            return 0;
        }
        return portalCooldowns.get(worldId).getCooldown();
    }

    public long getEntryCount(int worldId) {
        if (portalCooldowns == null || !portalCooldowns.containsKey(worldId)) {
            return 0;
        }
        return portalCooldowns.get(worldId).getEntryCount();
    }

    public PortalCooldownItem getPortalCooldownItem(int worldId) {
        if (portalCooldowns == null || !portalCooldowns.containsKey(worldId)) {
            return null;
        }
        return portalCooldowns.get(worldId);
    }

    public FastMap<Integer, PortalCooldownItem> getPortalCoolDowns() {
        return portalCooldowns;
    }

    /**
     * @param worldId
     * @param time
     */
    public void setPortalCoolDowns(FastMap<Integer, PortalCooldownItem> portalCoolDowns) {
        this.portalCooldowns = portalCoolDowns;
    }

    public void addPortalCooldown(int worldId, int entryCount, long useDelay) {
        if (portalCooldowns == null) {
            portalCooldowns = new FastMap<Integer, PortalCooldownItem>();
        }
        portalCooldowns.put(worldId, new PortalCooldownItem(worldId, entryCount, useDelay));
        if (owner.isInTeam()) {
            owner.getCurrentTeam().sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        } else {
            PacketSendUtility.sendPacket(owner, new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        }
    }

    /**
     * @param worldId
     */
    public void removePortalCoolDown(int worldId) {
        if (portalCooldowns != null) {
            portalCooldowns.remove(worldId);
        }
        if (owner.isInTeam()) {
            owner.getCurrentTeam().sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        } else {
            PacketSendUtility.sendPacket(owner, new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
            //You can enter %0 area now.
            PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_MSG_CAN_ENTER_INSTANCE(worldId));
        }
    }

    public void addEntry(int worldId) {
        if (portalCooldowns != null && portalCooldowns.containsKey(worldId)) {
            portalCooldowns.get(worldId).setEntryCount(portalCooldowns.get(worldId).getEntryCount() + 1);
        }
        if (owner.isInTeam()) {
            owner.getCurrentTeam().sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        } else {
            PacketSendUtility.sendPacket(owner, new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        }
    }

    public void reduceEntry(int worldId) {
        if (portalCooldowns != null && portalCooldowns.containsKey(worldId)) {
            portalCooldowns.get(worldId).setEntryCount(portalCooldowns.get(worldId).getEntryCount() - 1);
        }
        if (portalCooldowns.get(worldId).getEntryCount() == 0) {
            removePortalCoolDown(worldId);
            return;
        }
        if (owner.isInTeam()) {
            owner.getCurrentTeam().sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        } else {
            PacketSendUtility.sendPacket(owner, new S_INSTANCE_DUNGEON_COOLTIMES(owner, worldId));
        }
    }

    public boolean hasCooldowns() {
        return portalCooldowns != null && portalCooldowns.size() > 0;
    }

    public int size() {
        return portalCooldowns != null ? portalCooldowns.size() : 0;
    }

}