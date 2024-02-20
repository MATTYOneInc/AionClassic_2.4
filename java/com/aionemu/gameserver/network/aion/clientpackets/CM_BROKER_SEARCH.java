package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CM_BROKER_SEARCH extends AionClientPacket
{
	Logger log = LoggerFactory.getLogger(CM_BROKER_SEARCH.class);
	
    @SuppressWarnings("unused")
    private int brokerId;
    private int sortType;
    private int page;
    private int mask;
    private int itemCount;
    private List<Integer> itemList;
    
    private int unk1;
    private int unk2;
    private int unk3;
    private int minLvl;
    private int maxLvl;
    private int minUnk;
    private int maxUnk;
    private int unk4;
	
    public CM_BROKER_SEARCH(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.brokerId = readD();
        this.sortType = readC();
        this.page = readH();
        this.mask = readH();
        this.itemCount = readH();
        this.itemList = new ArrayList<Integer>();
        for (int index = 0; index < this.itemCount; index++) {
            this.itemList.add(readD());
        }
    }
	
    @Override
    protected void runImpl() {
        final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
        BrokerService.getInstance().showRequestedItems(player, mask, sortType, page, itemList);
    }
}