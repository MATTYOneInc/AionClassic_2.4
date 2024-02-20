package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudItem;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudLetter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_RESPONSE_NPSHOP_GOODS_LIST extends AionServerPacket {

    private Player player;

    public S_RESPONSE_NPSHOP_GOODS_LIST(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(0);
        writeC(1);
        writeH(this.player.getBlackcloudLetters().size()); //mail size
        for (BlackcloudLetter letter : this.player.getBlackcloudLetters().values()) {
            writeQ(letter.getObjectId());
            writeS(letter.getTitle());
            writeS(letter.getMessage());
            writeC(0);
            writeQ(- System.currentTimeMillis() /1000);//expire
            writeQ(letter.getTimeStamp().getTime() / 1000); //received date
            writeQ(0);//refund period
            writeC(letter.isUnread() ? 1 : 0); //unread

            writeH(letter.getAttachedItem().size()); //item size

            for (BlackcloudItem item : letter.getAttachedItem()) {
                writeD(item.getItemId());//itemId
                writeQ(item.getItemCount()); //item count
                writeD(0);
                //writeB("FF7F0000");//dont change
                writeD(32767);
                writeQ(2);
                writeD(80);
            }
        }
    }
}
