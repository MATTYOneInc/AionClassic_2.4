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
package world;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@WorldID(220010000)
public class Ishalgen extends GeneralWorldHandler
{
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/2004/
			case 700047: //Tombstone.
			    final QuestState qs2004 = player.getQuestStateList().getQuestState(2004); //A Charmed Cube.
				if (qs2004 != null && qs2004.getStatus() == QuestStatus.START && qs2004.getQuestVarById(0) == 1) {
					switch (Rnd.get(1, 2)) {
						case 1:
							spawn(220010000, 211755, player.getX() + 3, player.getY(), player.getZ(), (byte) 0);
						break;
						case 2:
						break;
					}
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		///QUEST-MISSION ZONES.
		///https://aioncodex.com/3x/quest/2007/
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DF1_SENSORY_AREA_Q2007_220010000")) {
		    final QuestState qs2007 = player.getQuestStateList().getQuestState(2007); //Where's Rae This Time?.
			if (qs2007 != null && qs2007.getStatus() == QuestStatus.REWARD && qs2007.getQuestVarById(0) == 8) {
				PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 58));
			}
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected List<Npc> getNpcs(int npcId) {
        List<Npc> npcs = new FastList<Npc>();
        for (Npc npc : this.map.getWorld().getNpcs()) {
            if (npc.getNpcId() == npcId) {
                npcs.add(npc);
            }
        }
        return npcs;
    }
}