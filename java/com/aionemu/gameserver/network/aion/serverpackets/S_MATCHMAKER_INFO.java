package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.apache.commons.lang.StringUtils;

public class S_MATCHMAKER_INFO extends AionServerPacket
{
    private byte windowId;
    private int instanceMaskId;
    private int mapId;
    private int messageId;
    private int titleId;
    private int waitTime;
    private boolean close;
    String name = StringUtils.EMPTY;
    public static final byte wnd_EntryIcon = 6;

    public S_MATCHMAKER_INFO(int instanceMaskId) {
        AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
        this.instanceMaskId = instanceMaskId;
        this.messageId = agt.getNameId();
        this.titleId = agt.getTitleId();
        this.mapId = agt.getInstanceMapId();
    }

    public S_MATCHMAKER_INFO(int instanceMaskId, Number windowId) {
        this(instanceMaskId);
        this.windowId = windowId.byteValue();
    }

    public S_MATCHMAKER_INFO(int instanceMaskId, Number windowId, boolean close) {
        this(instanceMaskId);
        this.windowId = windowId.byteValue();
        this.close = close;
    }

    public S_MATCHMAKER_INFO(int instanceMaskId, Number windowId, int waitTime, String name) {
        this(instanceMaskId);
        this.windowId = windowId.byteValue();
        this.waitTime = waitTime;
        this.name = name;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.instanceMaskId);
        writeC(this.windowId);
        writeD(this.mapId);
        switch (this.windowId) {
            case 0: //Request Entry
                writeD(this.messageId);
                writeD(this.titleId);
                writeD(0);
                break;
            case 1: //Waiting Window
                writeD(0);
                writeD(0);
                writeD(this.waitTime);
                break;
            case 2: //Cancel Looking
                writeD(0);
                writeD(0);
                writeD(0);
                break;
            case 3: //Pass Window
                writeD(0);
                writeD(0);
                writeD(this.waitTime);
                break;
            case 4: //Enter Window
                writeD(0);
                writeD(0);
                writeD(0);
                break;
            case 5: //After You Click Enter
                writeD(0);
                writeD(0);
                writeD(0);
                break;
            case 6:
                writeD(this.messageId);
                writeD(this.titleId);
                writeD(this.close ? 0 : 1);
                break;
            case 7: //Failed Window
                writeD(this.messageId);
                writeD(this.titleId);
                writeD(0);
                break;
            case 8:
                writeD(0);
                writeD(0);
                writeD(this.waitTime);
                break;
        }
        writeC(0);
        writeS(this.name);
    }
}