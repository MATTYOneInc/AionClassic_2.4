/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetEmote;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET_MOVE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CM_PET_EMOTE extends AionClientPacket
{
	private PetEmote emote;
    private int emoteId;
    private float x1;
    private float y1;
    private float z1;
    private byte h;
    private float x2;
    private float y2;
    private float z2;
    private int emotionId;
    private int unk2;

    public CM_PET_EMOTE(int opcode, AionConnection.State state, AionConnection.State ... restStates) {
        super(opcode, state, restStates);
    }

    protected void readImpl() {
        this.emoteId = this.readC();
        this.emote = PetEmote.getEmoteById(this.emoteId);
        if (this.emote == PetEmote.MOVE_STOP) {
            this.x1 = this.readF();
            this.y1 = this.readF();
            this.z1 = this.readF();
            this.h = this.readSC();
        } else if (this.emote == PetEmote.MOVETO) {
            this.x1 = this.readF();
            this.y1 = this.readF();
            this.z1 = this.readF();
            this.h = this.readSC();
            this.x2 = this.readF();
            this.y2 = this.readF();
            this.z2 = this.readF();
        } else {
            this.emotionId = this.readC();
            this.unk2 = this.readC();
        }
    }

    protected void runImpl() {
        final Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        }
        Pet pet = player.getPet();
        if (pet == null) {
            return;
        } if (this.x1 < 0.0f || this.y1 < 0.0f || this.z1 < 0.0f) {
            return;
        } if (this.emote == PetEmote.ALARM) {
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote), true);
        } else if (this.emote == PetEmote.MOVE_STOP) {
            World.getInstance().updatePosition(pet, this.x1, this.y1, this.z1, this.h);
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote, this.x1, this.y1, this.z1, this.h), true);
        } else if (this.emote == PetEmote.MOVETO) {
            World.getInstance().updatePosition(pet, this.x1, this.y1, this.z1, this.h);
            pet.getMoveController().setNewDirection(this.x2, this.y2, this.z2, this.h);
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2, this.h), true);
        } else if (this.emote == PetEmote.FLY) {
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote, this.emotionId, this.unk2), true);
        } else if (this.emote == PetEmote.MOVE_STOP) {
            World.getInstance().updatePosition(pet, this.x1, this.y1, this.z1, this.h);
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote, this.x1, this.y1, this.z1, this.h), true);
        } else if (this.emotionId > 0) {
            PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET_MOVE(pet, this.emote, this.emotionId, this.unk2));
        } else {
            PacketSendUtility.broadcastPacket(player, (AionServerPacket)new S_FUNCTIONAL_PET_MOVE(pet, this.emote, 0, this.unk2), true);
        }
    }
}