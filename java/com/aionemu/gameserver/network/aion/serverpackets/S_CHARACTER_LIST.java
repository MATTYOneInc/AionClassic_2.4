package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.player.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class S_CHARACTER_LIST extends PlayerInfo {
    private static Logger log = LoggerFactory.getLogger(S_CHARACTER_LIST.class);

    private final int playOk2;

    public S_CHARACTER_LIST(int playOk2) {
        this.playOk2 = playOk2;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(playOk2);
        Account account = con.getAccount();
        removeDeletedCharacters(account);
        writeC(account.size());

        for (PlayerAccountData playerData : account.getSortedAccountsList()) {
            PlayerCommonData pcd = playerData.getPlayerCommonData();
            CharacterBanInfo cbi = playerData.getCharBanInfo();
            Player player = PlayerService.getPlayer(pcd.getPlayerObjId(), account);
            writePlayerInfo(playerData);

            if(GSConfig.SERVER_COUNTRY_CODE == 0) {
                writeD(player.getPlayerSettings().getDisplay());//display helmet 0 show, 5 dont show
                writeD(1);
                writeD(1);
                writeD(MailDAO.unreadedMails(pcd.getPlayerObjId()) != 0 ? 1 : 1); // mail
                writeB(new byte[108]);
                //writeQ(BrokerService.getInstance().getCollectedMoney(pcd)); // collected money from broker
                writeD(2);
                writeD(0);

                //sanction here
                if (cbi != null && cbi.getEnd() > System.currentTimeMillis() / 1000) {
                    writeD((int) cbi.getStart()); // startPunishDate
                    writeD((int) cbi.getEnd()); // endPunishDate
                    writeS(cbi.getReason());
                } else {
                    writeD(0);
                    writeD(0);
                    writeH(0);
                }
            } else {
                writeD(player.getPlayerSettings().getDisplay());//display helmet 0 show, 5 dont show is ok
                writeD(MailDAO.mailCount(pcd.getPlayerObjId())); // All Mail Count
                writeD(MailDAO.unreadedMails(pcd.getPlayerObjId())); // Unread Mail Count
                writeD(0);
                writeD(0);
                writeD(0);
                writeQ(BrokerService.getInstance().getCollectedMoney(pcd)); // collected money from broker
                writeD(0);
                if (cbi != null && cbi.getEnd() > System.currentTimeMillis() / 1000) {
                    writeD((int) cbi.getStart()); // startPunishDate
                    writeD((int) cbi.getEnd()); // endPunishDate
                    writeS(cbi.getReason());
                } else {
                    writeD(0);
                    writeD(0);
                    writeH(0);
                }
            }
        }
    }

        public void removeDeletedCharacters (Account account){
            Iterator<PlayerAccountData> it = account.iterator();
            while (it.hasNext()) {
                PlayerAccountData pad = it.next();
                Race race = pad.getPlayerCommonData().getRace();
                long deletionTime = (long) pad.getDeletionTimeInSeconds() * (long) 1000;
                if (deletionTime != 0 && deletionTime <= System.currentTimeMillis()) {
                    it.remove();
                    account.decrementCountOf(race);
                    PlayerService.deletePlayerFromDB(pad.getPlayerCommonData().getPlayerObjId());
                }
            }
            if (account.isEmpty()) {
                removeAccountWH(account.getId());
                account.getAccountWarehouse().clear();
            }
        }

        private static void removeAccountWH ( int accountId){
            InventoryDAO.deleteAccountWH(accountId);
        }
    }
