/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

public class CM_PLAY_MOVIE_END extends AionClientPacket
{
	@SuppressWarnings("unused")
    private int type;
    @SuppressWarnings("unused")
    private int targetObjectId;
    @SuppressWarnings("unused")
    private int dialogId;
    private int movieId;
    @SuppressWarnings("unused")
    private int unk;

	public CM_PLAY_MOVIE_END(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
    protected void readImpl() {
        type = readC();
        targetObjectId = readD();
        dialogId = readD();
        movieId = readH();
        unk = readD();
    }
	
	@Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        QuestEngine.getInstance().onMovieEnd(new QuestEnv(null, player, 0, 0), movieId);
        if (player.getPosition().isInstanceMap()) {
            player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayMovieEnd(player, movieId);
        } else {
            player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onPlayMovieEnd(player, movieId);
        }
    }
}