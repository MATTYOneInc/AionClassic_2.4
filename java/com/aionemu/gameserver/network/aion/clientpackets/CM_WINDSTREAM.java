package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamPath;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_POLYMORPH;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIND_PATH_RESULT;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_WINDSTREAM extends AionClientPacket
{
    int teleportId;
    int distance;
    int state;
	
    public CM_WINDSTREAM(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        teleportId = readD();
        distance = readD();
        state = readD();
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        switch (state) {
            case 0:
            case 7:
            case 8:
                if (state == 7) {
                    PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.WINDSTREAM_START_BOOST, 0, 0), true);
                } else if (state == 8) {
                    PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.WINDSTREAM_END_BOOST, 0, 0), true);
                }
                PacketSendUtility.sendPacket(player, new S_WIND_PATH_RESULT(state, 1));
            break;
            case 1:
                if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
                    return;
                }
				player.setPlayerMode(PlayerMode.WINDSTREAM, new WindstreamPath(teleportId, distance));
				player.unsetState(CreatureState.ACTIVE);
				player.unsetState(CreatureState.GLIDING);
				player.setState(CreatureState.FLYING);
				PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.WINDSTREAM, teleportId, distance), true);
				player.getLifeStats().triggerFpRestore();
            break;
            case 2:
            case 3:
            case 4:
                if (!player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
                    return;
                }
                player.unsetState(CreatureState.FLYING);
                player.setState(CreatureState.ACTIVE);
                if (state == 2) {
                    player.setState(CreatureState.GLIDING);
                    player.getLifeStats().triggerFpReduce();
                } if (state == 4 || player.isTransformed()) {
                    player.setState(CreatureState.GLIDING);
                    player.getLifeStats().triggerFpReduce();
                	PacketSendUtility.broadcastPacketAndReceive(player, new S_POLYMORPH(player, player.getTransformedModelId(), true, player.getTransformedItemId()));
                	PacketSendUtility.broadcastPacketAndReceive(player, new S_POLYMORPH(player, true));
                	player.getEffectController().updatePlayerEffectIcons();
                }
                PacketSendUtility.broadcastPacket(player, new S_ACTION(player, state == 2 ? EmotionType.WINDSTREAM_END : EmotionType.WINDSTREAM_EXIT, 0, 0), true);
                player.getGameStats().updateStatsAndSpeedVisually();
                PacketSendUtility.sendPacket(player, new S_WIND_PATH_RESULT(state, 1));
                player.unsetPlayerMode(PlayerMode.WINDSTREAM);
            break;
        }
    }
}