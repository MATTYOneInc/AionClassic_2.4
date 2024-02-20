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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_TITLE extends AionServerPacket
{
	private TitleList titleList;
    private int action;
    private int titleId;
    private int playerObjId;

	public S_TITLE(Player player) {
        this.action = 0;
        this.titleList = player.getTitleList();
    }

    public S_TITLE(int titleId) {
        this.action = 1;
        this.titleId = titleId;
    }

    public S_TITLE(Player player, int titleId) {
        this.action = 3;
        this.playerObjId = player.getObjectId();
        this.titleId = titleId;
    }

    public S_TITLE(boolean flag) {
        this.action = 4;
        this.titleId = flag ? 1 : 0;
    }

    public S_TITLE(Player player, boolean flag) {
        this.action = 5;
        this.playerObjId = player.getObjectId();
        this.titleId = flag ? 1 : 0;
    }

	@Override
	protected void writeImpl(AionConnection con) {
		this.writeC(this.action);
        switch (this.action) {
            case 0: {
                this.writeC(0);
                this.writeH(this.titleList.size());
                for (Title title : this.titleList.getTitles()) {
                    this.writeD(title.getId());
                    this.writeD(title.getRemainingTime());
                }
                break;
            } case 1: {
                this.writeH(this.titleId);
                break;
            } case 3: {
                this.writeD(this.playerObjId);
                this.writeH(this.titleId);
                break;
            } case 4: {
                this.writeH(this.titleId);
                break;
            } case 5: {
                this.writeD(this.playerObjId);
                this.writeH(this.titleId);
                break;
            }
        }
	}
}