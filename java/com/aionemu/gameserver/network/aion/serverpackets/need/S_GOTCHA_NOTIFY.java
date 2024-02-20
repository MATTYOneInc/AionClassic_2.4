package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTabItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.ArcadeUpgradeService;

import java.util.List;

public class S_GOTCHA_NOTIFY extends AionServerPacket {

    private int action;
    private int showicon = 1;
    private int frenzyPoints = 0;
    private boolean success = false;
    private int level;
    private ArcadeTabItem itemList;
    private int sessionId = 64519;
    private Player player;
    private int frenzyTime;
    private int frenzyCount;

    public S_GOTCHA_NOTIFY(boolean showicon) {
        this.action = 0;
        this.showicon = showicon ? 1 : 0;
    }

    public S_GOTCHA_NOTIFY(int frenzyPoints, int frenzyCount) {
        this.action = 1;
        this.frenzyPoints = frenzyPoints;
        this.frenzyCount = frenzyCount;
    }

    public S_GOTCHA_NOTIFY(int action) {
        this.action = action;
    }

    public S_GOTCHA_NOTIFY(int action, boolean success, int frenzy) {
        this.action = action;
        this.success = success;
        this.frenzyPoints = frenzy;
    }

    public S_GOTCHA_NOTIFY(Player player, int action, int level) {
        this.action = action;
        this.level = level;
        this.player = player;
    }

    public S_GOTCHA_NOTIFY(int action, ArcadeTabItem itemList) {
        this.action = action;
        this.itemList = itemList;
    }

    public S_GOTCHA_NOTIFY(int action, int frenzyTime, int frenzyCount) {
        this.action = action;
        this.frenzyTime = frenzyTime;
        this.frenzyCount = frenzyCount;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(action);
        switch(action) {
            case 0:
                writeD(this.showicon);
                break;
            case 1:
                writeD(sessionId);
                writeD(frenzyPoints);
                writeD(frenzyCount);
                writeD(1);
                writeD(4);
                writeD(6);
                writeD(8);
                writeD(8);
                writeH(272);
                writeS("success_weapon01");
                writeS("success_weapon01");
                writeS("success_weapon01");
                writeS("success_weapon02");
                writeS("success_weapon02");
                writeS("success_weapon03");
                writeS("success_weapon03");
                writeS("success_weapon04");
                break;
            case 2:
                writeD(sessionId);
                break;
            case 3:
                writeC(success ? 1 : 0);
                writeD(frenzyPoints > 100 ? 100 : frenzyPoints);
                break;
            case 4:
                writeD(level);
                break;
            case 5:
                writeD(level);
                writeC(level >= 6 && !player.getUpgradeArcade().isReTry() ? 1 : 0);
                writeD(level >= 6 && !player.getUpgradeArcade().isReTry() ? 2 : 0);
                writeD(0);
                player.getUpgradeArcade().setReTry(false);
                player.getUpgradeArcade().setFailed(false);
                break;
            case 6:
                writeD(itemList.getItemId());
                writeD(itemList.getNormalCount() > 0 ? this.itemList.getNormalCount() : this.itemList.getFrenzyCount());
                writeD(0);
                break;
            case 7:
                writeD(frenzyTime);
                writeD(frenzyCount);
                break;
            case 8:
                writeD(1);
                writeD(0);
                break;
            case 10:
                List<ArcadeTab> tabs = ArcadeUpgradeService.getInstance().getTabs();
                for(ArcadeTab tab : tabs) {
                    writeC(tab.getArcadeTabItems().size());
                } for (ArcadeTab arcadetab : tabs){
                for (ArcadeTabItem arcadetabitem : arcadetab.getArcadeTabItems()){
                    writeD(arcadetabitem.getItemId());
                    writeQ(arcadetabitem.getNormalCount());
                    writeQ(arcadetabitem.getFrenzyCount());
                }
            }
                break;
        }
    }
}