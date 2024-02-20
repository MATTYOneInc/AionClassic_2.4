package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import org.slf4j.LoggerFactory;

public class CM_TELEPORT_SELECT extends AionClientPacket
{
	public int targetObjectId;
    public int locId;
	
	public CM_TELEPORT_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		this.targetObjectId = this.readD();
        this.locId = this.readD();
        this.readH();
	}
	
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
        } if (player.getLifeStats().isAlreadyDead()) {
            return;
        }
        VisibleObject obj = player.getKnownList().getObject(this.targetObjectId);
        if (obj != null && obj instanceof Npc) {
            Npc npc = (Npc)obj;
            int npcId = npc.getNpcId();
            if (!MathUtil.isInRange(npc, player, npc.getObjectTemplate().getTalkDistance() + 5.0f)) {
                return;
            }
            TeleporterTemplate teleport = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npcId);
            if (teleport != null) {
                TeleportService2.teleport(teleport, this.locId, player, npc, TeleportAnimation.JUMP_ANIMATION_2);
            } else {
                LoggerFactory.getLogger(CM_TELEPORT_SELECT.class).warn("teleportation id " + this.locId + " was not found on npc " + npcId);
            }
        } else {
            LoggerFactory.getLogger(CM_TELEPORT_SELECT.class).debug("player " + player.getName() + " requested npc " + this.targetObjectId + " for teleportation " + this.locId + ", but he doesnt have such npc in knownlist");
        }
    }
}