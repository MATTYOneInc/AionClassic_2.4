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
package instance.quest;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320050000)
public class Sky_Temple_InteriorQ extends GeneralInstanceHandler
{
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///Water Room Guardian OR Corrupt Spring Spirit.
		switch (Rnd.get(1, 2)) {
		    case 1:
				final Npc elementalwater3nmd_31_an = (Npc) spawn(212834, 394.7483f, 54.3286f, 223.1608f, (byte) 0);
				elementalwater3nmd_31_an.getSpawn().setWalkerId("iddf2flying_mpath_elementalwater3nmd_31_1");
				WalkManager.startWalking((NpcAI2) elementalwater3nmd_31_an.getAi2());
			break;
			case 2:
				final Npc elementalwater3d_31_an = (Npc) spawn(212764, 394.7483f, 54.3286f, 223.1608f, (byte) 0);
				elementalwater3d_31_an.getSpawn().setWalkerId("iddf2flying_mpath_elementalwater3nmd_31_1");
				WalkManager.startWalking((NpcAI2) elementalwater3d_31_an.getAi2());
			break;
		}
		///Fire Key Guardian.
		switch (Rnd.get(1, 4)) {
		    case 1:
				spawn(212835, 404.7752f, 457.4501f, 375.0549f, (byte) 96);
			break;
			case 2:
				spawn(212835, 458.1592f, 436.5146f, 375.0554f, (byte) 72);
			break;
			case 3:
				spawn(212835, 444.9057f, 369.6886f, 375.0553f, (byte) 41);
			break;
			case 4:
				spawn(212835, 388.0430f, 370.5237f, 375.0553f, (byte) 18);
			break;
		}
		///Wind Room Guardian OR Corrupt Air Spirit.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(212836, 466.4982f, 398.2606f, 375.0549f, (byte) 56);
			break;
			case 2:
				spawn(212772, 466.4982f, 398.2606f, 375.0549f, (byte) 56);
			break;
		}
		///Fire Room Guardian OR Corrupt Flame Spirit.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(212837, 237.4710f, 233.8541f, 552.1723f, (byte) 21);
				spawn(212780, 162.1681f, 290.6224f, 552.1724f, (byte) 15);
			break;
			case 2:
				spawn(212780, 237.4710f, 233.8541f, 552.1723f, (byte) 21);
				spawn(212837, 162.1681f, 290.6224f, 552.1724f, (byte) 15);
			break;
		}
		///Hyuirinerk.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(798107, 206.2158f, 305.6858f, 550.4942f, (byte) 79);
			break;
			case 2:
				spawn(798108, 206.2158f, 305.6858f, 550.4942f, (byte) 79);
			break;
		}
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		final QuestState qs2035 = player.getQuestStateList().getQuestState(2035); //The Three Keys.
		if (qs2035 != null && qs2035.getStatus() == QuestStatus.START && qs2035.getQuestVarById(0) == 5) {
			ClassChangeService.onUpdateMission2035(player);
			instance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isOnline()) {
						PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 160));
					}
				}
			});
		}
	}
}