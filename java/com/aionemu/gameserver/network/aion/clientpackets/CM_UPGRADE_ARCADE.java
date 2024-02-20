package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.ArcadeUpgradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_UPGRADE_ARCADE extends AionClientPacket {

    private int action;
    @SuppressWarnings("unused")
    private int sessionId;

    public CM_UPGRADE_ARCADE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    private static final Logger log = LoggerFactory.getLogger(CM_UPGRADE_ARCADE.class);

    @Override
    protected void readImpl() {
        action = readC();
        sessionId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();

        if (player == null) {
            return;
        } switch(action) {
            case 0:
                ArcadeUpgradeService.getInstance().startArcadeUpgrade(player);
                break;
            case 1:
                ArcadeUpgradeService.getInstance().closeWindow(player);
                break;
            case 2:
                ArcadeUpgradeService.getInstance().tryArcadeUpgrade(player);
                break;

            case 3:
                ArcadeUpgradeService.getInstance().getReward(player);
                break;
                /*
            case 4:
                player.getUpgradeArcade().setReTry(true);
                ArcadeUpgradeService.getInstance().tryArcadeUpgrade(player);
                break;*/
            case 5:
                ArcadeUpgradeService.getInstance().showRewardList(player);
                break;
            default:
                log.info("CM_UPGRADE_ARCADE action : " + action);
                break;
        }
    }
}