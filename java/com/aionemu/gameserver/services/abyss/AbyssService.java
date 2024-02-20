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
package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

public class AbyssService
{
	private static final int[] abyssMapList = {
	210020000, //Elten.
	210040000, //Heiron.
	210050000, //Inggison.
	210060000, //Theobomos.
	220020000, //Morheim.
	220040000, //Beluslan.
	220050000, //Brusthonin.
	220070000, //Gelkmaros.
	400010000, //Reshanta.
	600010000}; //Silentera Canyon.
	
	public static final boolean isOnPvpMap(Player player) {
		for (int i: abyssMapList) {
			if (i == player.getWorldId()) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}
	
	public static final void rankedKillAnnounce(final Player victim) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != victim && victim.getWorldId() == p.getWorldId()) {
					PacketSendUtility.sendPacket(p, S_MESSAGE_CODE.STR_ABYSS_ORDER_RANKER_DIE(victim, AbyssRankEnum.getRankDescriptionId(victim)));
				}
			}
		});
	}
	
	public static final void rankerSkillAnnounce(final Player player, final int nameId) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != player && player.getWorldType() == p.getWorldType() && !p.isInInstance()) {
					PacketSendUtility.sendPacket(p, S_MESSAGE_CODE.STR_SKILL_ABYSS_SKILL_IS_FIRED(player, new DescriptionId(nameId)));
				}
			}
		});
	}
}