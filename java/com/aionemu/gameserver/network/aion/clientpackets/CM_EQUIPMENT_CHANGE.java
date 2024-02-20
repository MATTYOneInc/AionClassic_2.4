package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_EQUIPMENT_CHANGE extends AionClientPacket {

    private static final Logger log = LoggerFactory.getLogger(CM_EQUIPMENT_CHANGE.class);
    private int slot;
    private int displayType;
    private int mHand;
    private int sHand;
    private int helmet;
    private int torso;
    private int glove;
    private int boots;
    private int earringsLeft;
    private int earringsRight;
    private int ringLeft;
    private int ringRight;
    private int necklace;
    private int shoulder;
    private int pants;
    private int powershardLeft;
    private int powershardRight;
    private int wings;
    private int waist;
    private int mOffHand;
    private int sOffHand;
    private int plume;
    private int bracelet;
    private int unkD2;
    private int unkD3;
    private int unkD4;
    private int unkD5;

    public CM_EQUIPMENT_CHANGE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        slot = readD();
        displayType = readD();
        mHand = readD();
        sHand = readD();
        helmet = readD();
        torso = readD();
        glove = readD();
        boots = readD();
        earringsLeft = readD();
        earringsRight = readD();
        ringLeft = readD();
        ringRight = readD();
        necklace = readD();
        shoulder = readD();
        pants = readD();
        powershardLeft = readD();
        powershardRight = readD();
        wings = readD();
        waist = readD();
        mOffHand = readD();
        sOffHand = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        }
        player.getEquipmentSettingList().add(slot, displayType, mHand, sHand, helmet, torso, glove, boots, earringsLeft, earringsRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, waist, mOffHand, sOffHand);
    }
}