package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.AccountSielEnergyDAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountSielEnergy;
import com.aionemu.gameserver.model.account.SielEnergyType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_GAMEPASS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class SielEnergyService
{
    private Logger log = LoggerFactory.getLogger(SielEnergyService.class);
    Map<Integer, Player> playerTask = new FastMap<Integer, Player>();

    public void onLogin(Player player) {
        Account account = player.getPlayerAccount();


        if(account.getMembership() == 2) {
            PacketSendUtility.sendPacket(player, new S_GAMEPASS_INFO(3, player));
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_ALARM_PLAYTIME_CLASSIC_ACCOUNT_MEMBERSHIP((int) account.getMembershipExpire().getTime()));
        } else {
            if(player.getAccountSielEnergy() == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.HOUR, 24);
                Timestamp start = new Timestamp(System.currentTimeMillis());
                Timestamp end = new Timestamp(cal.getTimeInMillis());
                AccountSielEnergy sielEnergy = new AccountSielEnergy(SielEnergyType.TRIAL, start, end);
                player.setAccountSielEnergy(sielEnergy);
                PacketSendUtility.sendPacket(player, new S_GAMEPASS_INFO(1, player));
                PacketSendUtility.sendPacket(player, new S_GAMEPASS_INFO(1, player));
                //PacketSendUtility.sendPacket(player, new SM_SIEL_ENERGY(2, player));

            }
        }

        /*AccountSielEnergyDAO.load(player);
        if(player.getAccountSielEnergy() == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.HOUR, 2);
            Timestamp start = new Timestamp(System.currentTimeMillis());
            Timestamp end = new Timestamp(cal.getTimeInMillis());
            AccountSielEnergy sielEnergy = new AccountSielEnergy(SielEnergyType.TRIAL, start, end);
            player.setAccountSielEnergy(sielEnergy);
            AccountSielEnergyDAO.add(player);
        }
        PacketSendUtility.sendPacket(player, new SM_SIEL_ENERGY(1, player));
        PacketSendUtility.sendPacket(player, new SM_SIEL_ENERGY(1, player));
        if (player.getAccountSielEnergy().getType() == SielEnergyType.TRIAL && player.getAccountSielEnergy().isUnderSielEnergy()) {
            PacketSendUtility.sendPacket(player, new SM_SIEL_ENERGY(2, player));
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ALARM_PLAYTIME_CLASSIC_ACCOUNT_TRIAL((int) player.getAccountSielEnergy().getTime()));
            //player.getAccountSielEnergy().apply(player);
            player.getController().addTask(TaskId.SIEL_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new SielEnergyUpdateTask(player.getObjectId()), 1000, 1000));
        } else if (player.getAccountSielEnergy().getType() == SielEnergyType.MEMBERSHIP && player.getAccountSielEnergy().isUnderSielEnergy()) {
            //player.getAccountSielEnergy().apply(player);
            playerTask.put(player.getObjectId(), player);
        }*/
    }

    public void onRestart() {
		AccountSielEnergyDAO.remove();
    }

    public void EndEffect(Player player) {
        AccountSielEnergy sielEnergy = player.getAccountSielEnergy();
        sielEnergy.end(player);
    }

    public static SielEnergyService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final SielEnergyService instance = new SielEnergyService();
    }
}

class SielEnergyUpdateTask implements Runnable
{
	private static final Logger log = LoggerFactory.getLogger(SielEnergyUpdateTask.class);
	private final int playerId;

	SielEnergyUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null)
			try {
				if(player.getAccountSielEnergy() != null && !player.getAccountSielEnergy().isUnderSielEnergy()) {
					SielEnergyService.getInstance().EndEffect(player);
					PacketSendUtility.sendPacket(player, new S_GAMEPASS_INFO(1, player));
					PacketSendUtility.sendPacket(player, new S_GAMEPASS_INFO(1, player));
					player.getKnownList().doUpdate();
				}
			} catch (Exception ex) {
		}
	}
}
