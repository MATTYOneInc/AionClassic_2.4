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
package com.aionemu.gameserver.model.actions;

import com.aionemu.gameserver.model.gameobjects.player.InRoll;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamPath;

/****/
/** Author Rinzler (Encom)
/****/

public class PlayerActions extends CreatureActions
{
	public static boolean isInPlayerMode(Player player, PlayerMode mode) {
		switch (mode) {
			case IN_ROLL:
				return player.inRoll != null;
			case WINDSTREAM:
				return player.windstreamPath != null;
		}
		return false;
	}
	
	public static void setPlayerMode(Player player, PlayerMode mode, Object obj) {
		switch (mode) {
			case IN_ROLL:
				player.inRoll = (InRoll) obj;
			break;
			case WINDSTREAM:
				player.windstreamPath = (WindstreamPath) obj;
			break;	
		}
	}
	
	public static boolean unsetPlayerMode(Player player, PlayerMode mode) {
		switch (mode) {
			case IN_ROLL:
				if (player.inRoll == null) {
					return false;
				}
				player.inRoll = null;
				return true;
			case WINDSTREAM:
				if (player.windstreamPath == null) {
					return false;
				}
				player.windstreamPath = null;
				return true;
		}
		return false;
	}
}