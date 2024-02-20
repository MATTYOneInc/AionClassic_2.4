package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.FindGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

public class S_PARTY_MATCH extends AionServerPacket
{
	private int action;
	private int lastUpdate;
	private Collection<FindGroup> findGroups;
	private int groupSize;
	private int unk;
	private int instanceId;
	
	public S_PARTY_MATCH(int action, int lastUpdate, Collection<FindGroup> findGroups) {
        this.lastUpdate = lastUpdate;
        this.action = action;
        this.findGroups = findGroups;
        this.groupSize = findGroups.size();
    }
	
	public S_PARTY_MATCH(int action, int lastUpdate, int unk) {
        this.action = action;
        this.lastUpdate = lastUpdate;
        this.unk = unk;
    }
	
	public S_PARTY_MATCH(int action, int instanceId) {
        this.action = action;
		this.lastUpdate = lastUpdate;
        this.instanceId = instanceId;
    }
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		switch (action) {
            case 0:
            case 2:
                writeH(groupSize);
                writeH(groupSize);
                writeD(lastUpdate);
                for (FindGroup findGroup : findGroups) {
                    writeD(findGroup.getObjectId());
                    writeH(NetworkConfig.GAMESERVER_ID);
                    writeC(0);
                    writeC(16);
                    writeC(findGroup.getGroupType());
                    writeS(findGroup.getMessage());
                    writeS(findGroup.getName());
                    writeC(findGroup.getSize());
                    writeC(findGroup.getMinLevel());
                    writeC(findGroup.getMaxLevel());
                    writeD(findGroup.getLastUpdate());
                }
            break;
            case 1:
            case 3:
                writeC(0x01);
				writeD(lastUpdate);
                writeH(NetworkConfig.GAMESERVER_ID);
                writeC(0);
                writeC(16);
            break;
            case 4:
            case 6:
                writeC(action);
				writeH(groupSize);
                writeH(groupSize);
                writeD(lastUpdate);
                for (FindGroup findGroup : findGroups) {
                    writeD(findGroup.getObjectId());
                    writeC(findGroup.getGroupType());
                    writeS(findGroup.getMessage());
                    writeS(findGroup.getName());
                    writeC(findGroup.getClassId());
                    writeC(findGroup.getMinLevel());
                    writeD(findGroup.getLastUpdate());
                }
            break;
            case 5:
                writeC(0x05);
				writeD(lastUpdate);
            break;
            case 10:
                writeH(groupSize);
                writeH(groupSize);
                writeD(lastUpdate);
                for (FindGroup findGroup : findGroups) {
                    writeD(0);
                    writeD(findGroup.getInstanceId());
                    writeD(1);
                    writeC(findGroup.getSize());
                    writeC(findGroup.getMinMembers());
                    writeH(0);
                    writeD(findGroup.getObjectId());
                    writeD(1);
                    writeD(0);
                    writeC(findGroup.getMinLevel());
                    writeC(findGroup.getMaxLevel());
                    writeH(0);
                    writeD(findGroup.getLastUpdate());
                    writeD(0);
                    writeS(findGroup.getName());
                    writeS(findGroup.getMessage());
                }
            break;
            case 14:
                writeC(1);
                for (FindGroup findGroup : findGroups) {
                    writeD(0);
                    writeD(findGroup.getInstanceId());
                    writeD(1);
                    writeC(findGroup.getSize());
                    writeC(findGroup.getMinMembers());
                    writeH(0);
                    writeD(findGroup.getObjectId());
                    writeC(1);
                    writeC(0);
                    writeD(1);
                    writeH(0);
                    writeC(findGroup.getMinLevel());
                    writeC(findGroup.getMaxLevel());
                    writeH(0);
                    writeD(findGroup.getLastUpdate());
                    writeD(0);
                    writeS(findGroup.getName());
                    writeS(findGroup.getMessage());
                }
            break;
            case 16:
                writeH(groupSize);
                writeH(groupSize);
                writeD(lastUpdate);
                for (FindGroup findGroup : findGroups) {
                    writeD(0);
                    writeD(findGroup.getInstanceId());
                    writeD(findGroup.getObjectId());
                    writeD(findGroup.getMinLevel());
                    writeD(1);
                    writeH(1);
                    writeC(findGroup.getGroupType());
                    writeC(findGroup.getClassId());
                    writeS(findGroup.getName());
                }
            break;
            case 22:
                writeD(0);
                writeD(0);
            break;
            case 24:
                writeD(0);
                writeD(0);
                writeC(0);
                for (FindGroup findGroup : findGroups) {
                    writeD(0);
                    writeD(findGroup.getInstanceId());
                    writeD(findGroup.getObjectId());
                    writeD(findGroup.getMinLevel());
                    writeD(1);
                    writeH(1);
                    writeC(findGroup.getGroupType());
                    writeC(findGroup.getClassId());
                    writeS(findGroup.getName());
                }
            break;
            case 26:
                writeH(1);
                writeD(instanceId);
            break;
        }
	}
}