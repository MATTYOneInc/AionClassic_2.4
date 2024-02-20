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
package instance;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import javolution.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300550000)
public class Telos_Of_The_Forgotten extends GeneralInstanceHandler
{
	private int IDLDF1_Ctrl;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			///https://aion.plaync.com/board/clsupdate/view?articleId=63c65734241b11035ecc63be&viewMode=compact&size=18
			case 800728: //idldf1_treasurebox_03.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
					    switch (Rnd.get(1, 2)) {
							case 1:
				                dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052525, 1, true));
				            break;
							case 2:
				                dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052526, 1, true));
				            break;
						}
					}
				}
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(2).setOpen(true);
		//사마엘 부대 주둔지는 드라코뉴트 대장을 처치하여 사마엘 부대 주둔지의 경계를 약화시켜야 진입할 수 있습니다.
		sendMsgByRace(1401965, Race.PC_ALL, 10000);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_drguard_thunderer_m_ae1 = (Npc) spawn(219348, 1897.0000f, 747.0000f, 227.0000f, (byte) 0);
				idldf1_drguard_thunderer_m_ae1.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan13");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_m_ae1.getAi2());
			break;
			case 2:
				final Npc idldf1_drguard_thunderer_f_ae1 = (Npc) spawn(219349, 1897.0000f, 747.0000f, 227.0000f, (byte) 0);
				idldf1_drguard_thunderer_f_ae1.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan13");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_f_ae1.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_fi_55_ae1 = (Npc) spawn(219350, 1897.0000f, 747.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae1.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan13");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae1.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_as_55_ae1 = (Npc) spawn(219351, 1897.0000f, 747.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_as_55_ae1.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan13");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae1.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 6)) {
			case 1:
				final Npc idldf1_drguard_thunderer_m_ae2 = (Npc) spawn(219348, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_drguard_thunderer_m_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_m_ae2.getAi2());
			break;
			case 2:
				final Npc idldf1_drguard_thunderer_f_ae2 = (Npc) spawn(219349, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_drguard_thunderer_f_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_f_ae2.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_fi_55_ae2 = (Npc) spawn(219350, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae2.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_as_55_ae2 = (Npc) spawn(219351, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae2.getAi2());
			break;
			case 5:
				final Npc idldf1_lizard_ra_55_ae2 = (Npc) spawn(219352, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae2.getAi2());
			break;
			case 6:
				final Npc idldf1_lizard_pr_55_ae2 = (Npc) spawn(219353, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae2.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan14");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae2.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 5)) {
			case 1:
				final Npc idldf1_drguard_thunderer_m_ae3 = (Npc) spawn(219348, 1794.0000f, 1009.0000f, 237.0000f, (byte) 0);
				idldf1_drguard_thunderer_m_ae3.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard12");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_m_ae3.getAi2());
			break;
			case 2:
				final Npc idldf1_drguard_thunderer_f_ae3 = (Npc) spawn(219349, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_drguard_thunderer_f_ae3.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard12");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_f_ae3.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_fi_55_ae3 = (Npc) spawn(219350, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae3.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard12");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae3.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_as_55_ae3 = (Npc) spawn(219351, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae3.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard12");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae3.getAi2());
			break;
			case 5:
				final Npc idldf1_lizard_ra_55_ae3 = (Npc) spawn(219352, 1899.0000f, 878.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae3.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard12");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae3.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 6)) {
			case 1:
				final Npc idldf1_drguard_thunderer_m_ae4 = (Npc) spawn(219348, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_drguard_thunderer_m_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_m_ae4.getAi2());
			break;
			case 2:
				final Npc idldf1_drguard_thunderer_f_ae4 = (Npc) spawn(219349, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_drguard_thunderer_f_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_drguard_thunderer_f_ae4.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_fi_55_ae4 = (Npc) spawn(219350, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae4.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_as_55_ae4 = (Npc) spawn(219351, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_lizard_as_55_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae4.getAi2());
			break;
			case 5:
				final Npc idldf1_lizard_ra_55_ae4 = (Npc) spawn(219352, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae4.getAi2());
			break;
			case 6:
				final Npc idldf1_lizard_pr_55_ae4 = (Npc) spawn(219353, 1879.0000f, 800.0000f, 228.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae4.getSpawn().setWalkerId("idldf1_npcpathidldf1_drakan11");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae4.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae5 = (Npc) spawn(219350, 1797.0000f, 726.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae5.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard22");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae5.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae5 = (Npc) spawn(219351, 1797.0000f, 726.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_as_55_ae5.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard22");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae5.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae5 = (Npc) spawn(219352, 1797.0000f, 726.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae5.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard22");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae5.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae5 = (Npc) spawn(219353, 1797.0000f, 726.0000f, 227.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae5.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard22");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae5.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae6 = (Npc) spawn(219350, 1781.0000f, 611.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae6.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard3");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae6.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae6 = (Npc) spawn(219351, 1781.0000f, 611.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae6.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard3");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae6.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae6 = (Npc) spawn(219352, 1781.0000f, 611.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae6.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard3");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae6.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae6 = (Npc) spawn(219353, 1781.0000f, 611.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae6.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard3");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae6.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae7 = (Npc) spawn(219350, 1751.0000f, 1114.0000f, 240.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae7.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard9");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae7.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae7 = (Npc) spawn(219351, 1751.0000f, 1114.0000f, 240.0000f, (byte) 0);
				idldf1_lizard_as_55_ae7.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard9");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae7.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae7 = (Npc) spawn(219352, 1751.0000f, 1114.0000f, 240.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae7.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard9");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae7.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae7 = (Npc) spawn(219353, 1751.0000f, 1114.0000f, 240.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae7.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard9");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae7.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae8 = (Npc) spawn(219350, 1836.0000f, 707.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae8.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard21");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae8.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae8 = (Npc) spawn(219351, 1836.0000f, 707.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae8.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard21");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae8.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae8 = (Npc) spawn(219352, 1836.0000f, 707.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae8.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard21");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae8.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae8 = (Npc) spawn(219353, 1836.0000f, 707.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae8.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard21");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae8.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae9 = (Npc) spawn(219350, 1517.0000f, 953.0000f, 270.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae9.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard4");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae9.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae9 = (Npc) spawn(219351, 1517.0000f, 953.0000f, 270.0000f, (byte) 0);
				idldf1_lizard_as_55_ae9.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard4");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae9.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae9 = (Npc) spawn(219352, 1517.0000f, 953.0000f, 270.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae9.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard4");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae9.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae9 = (Npc) spawn(219353, 1517.0000f, 953.0000f, 270.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae9.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard4");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae9.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae10 = (Npc) spawn(219350, 1812.0000f, 767.0000f, 231.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae10.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard7");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae10.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae10 = (Npc) spawn(219351, 1812.0000f, 767.0000f, 231.0000f, (byte) 0);
				idldf1_lizard_as_55_ae10.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard7");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae10.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae10 = (Npc) spawn(219352, 1812.0000f, 767.0000f, 231.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae10.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard7");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae10.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae10 = (Npc) spawn(219353, 1812.0000f, 767.0000f, 231.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae10.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard7");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae10.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae11 = (Npc) spawn(219350, 1868.0000f, 754.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae11.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard2");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae11.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae11 = (Npc) spawn(219351, 1868.0000f, 754.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae11.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard2");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae11.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae11 = (Npc) spawn(219352, 1868.0000f, 754.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae11.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard2");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae11.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae11 = (Npc) spawn(219353, 1868.0000f, 754.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae11.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard2");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae11.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae12 = (Npc) spawn(219350, 1846.0000f, 1045.0000f, 230.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae12.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard6");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae12.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae12 = (Npc) spawn(219351, 1846.0000f, 1045.0000f, 230.0000f, (byte) 0);
				idldf1_lizard_as_55_ae12.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard6");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae12.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae12 = (Npc) spawn(219352, 1846.0000f, 1045.0000f, 230.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae12.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard6");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae12.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae12 = (Npc) spawn(219353, 1846.0000f, 1045.0000f, 230.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae12.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard6");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae12.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae13 = (Npc) spawn(219350, 1564.0000f, 898.0000f, 276.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae13.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard8");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae13.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae13 = (Npc) spawn(219351, 1564.0000f, 898.0000f, 276.0000f, (byte) 0);
				idldf1_lizard_as_55_ae13.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard8");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae13.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae13 = (Npc) spawn(219352, 1564.0000f, 898.0000f, 276.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae13.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard8");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae13.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae13 = (Npc) spawn(219353, 1564.0000f, 898.0000f, 276.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae13.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard8");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae13.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae14 = (Npc) spawn(219350, 1905.0000f, 781.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae14.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard5");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae14.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae14 = (Npc) spawn(219351, 1905.0000f, 781.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_as_55_ae14.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard5");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae14.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae14 = (Npc) spawn(219352, 1905.0000f, 781.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae14.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard5");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae14.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae14 = (Npc) spawn(219353, 1905.0000f, 781.0000f, 226.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae14.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard5");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae14.getAi2());
			break;
		}
		//
		switch (Rnd.get(1, 4)) {
			case 1:
				final Npc idldf1_lizard_fi_55_ae15 = (Npc) spawn(219350, 1821.0000f, 1101.0000f, 237.0000f, (byte) 0);
				idldf1_lizard_fi_55_ae15.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard20");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_fi_55_ae15.getAi2());
			break;
			case 2:
				final Npc idldf1_lizard_as_55_ae15 = (Npc) spawn(219351, 1821.0000f, 1101.0000f, 237.0000f, (byte) 0);
				idldf1_lizard_as_55_ae15.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard20");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_as_55_ae15.getAi2());
			break;
			case 3:
				final Npc idldf1_lizard_ra_55_ae15 = (Npc) spawn(219352, 1821.0000f, 1101.0000f, 237.0000f, (byte) 0);
				idldf1_lizard_ra_55_ae15.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard20");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_ra_55_ae15.getAi2());
			break;
			case 4:
				final Npc idldf1_lizard_pr_55_ae15 = (Npc) spawn(219353, 1821.0000f, 1101.0000f, 237.0000f, (byte) 0);
				idldf1_lizard_pr_55_ae15.getSpawn().setWalkerId("idldf1_npcpathidldf1_lizard20");
				WalkManager.startWalking((NpcAI2) idldf1_lizard_pr_55_ae15.getAi2());
			break;
		}
    }
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 219369: //제55 드라코뉴트 전투대장.
			case 219370: //제55 드라코뉴트 암살대장.
			case 219371: //제55 드라코뉴트 수색대장.
			case 219372: //제55 드라코뉴트 치료대장.
				IDLDF1_Ctrl++;
				if (IDLDF1_Ctrl == 4) {
					doors.get(63).setOpen(true);
					doors.get(64).setOpen(true);
					doors.get(65).setOpen(true);
					//사마엘 부대 주둔지 경계가 약화되어 사마엘 부대 주둔지로 진입할 수 있습니다.
					sendMsgByRace(1401966, Race.PC_ALL, 0);
					//집행자를 인식한 일부 용족 군단이 물러납니다.
					sendMsgByRace(1401967, Race.PC_ALL, 3000);
				}
			break;
			case 219366: //드라나 증폭 장치.
			    despawnNpc(npc);
				rewardPlayerInside();
				doors.get(2).setOpen(true);
				despawnNpcs(instance.getNpcs(281118));
				despawnNpcs(instance.getNpcs(701417));
			break;
			case 219393: //분노한 사마엘.
			    despawnNpc(npc);
				rewardPlayerInside2();
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 800724:
				idldf1_exit(player, 444.93997f, 765.9995f, 313.6232f, (byte) 60);
			break;
		}
	}
	
	protected void idldf1_exit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600010000, 1, x, y, z, h);
	}
	
	private void rewardPlayerInside() {
		int playerInside = instance.getPlayersInside().size();
		if (playerInside == 1) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
		} else if (playerInside == 2) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
			spawn(800727, 1524.3632f, 1392.2115f, 237.7500f, (byte) 88);
		} else if (playerInside == 3) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
			spawn(800727, 1524.3632f, 1392.2115f, 237.7500f, (byte) 88);
			spawn(800727, 1536.4059f, 1377.1550f, 237.7500f, (byte) 52);
		} else if (playerInside == 4) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
			spawn(800727, 1524.3632f, 1392.2115f, 237.7500f, (byte) 88);
			spawn(800727, 1536.4059f, 1377.1550f, 237.7500f, (byte) 52);
			spawn(800727, 1524.3634f, 1371.1068f, 237.7500f, (byte) 30);
		} else if (playerInside == 5) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
			spawn(800727, 1524.3632f, 1392.2115f, 237.7500f, (byte) 88);
			spawn(800727, 1536.4059f, 1377.1550f, 237.7500f, (byte) 52);
			spawn(800727, 1524.3634f, 1371.1068f, 237.7500f, (byte) 30);
			spawn(800727, 1519.3142f, 1392.1588f, 237.7500f, (byte) 95);
		} else if (playerInside == 6) {
			spawn(800727, 1514.3159f, 1377.1482f, 237.7500f, (byte) 9);
			spawn(800727, 1524.3632f, 1392.2115f, 237.7500f, (byte) 88);
			spawn(800727, 1536.4059f, 1377.1550f, 237.7500f, (byte) 52);
			spawn(800727, 1524.3634f, 1371.1068f, 237.7500f, (byte) 30);
			spawn(800727, 1519.3142f, 1392.1588f, 237.7500f, (byte) 95);
			spawn(800727, 1519.3610f, 1371.1725f, 237.7500f, (byte) 20);
			spawn(800727, 1536.3275f, 1384.2145f, 237.7500f, (byte) 72);
			spawn(800727, 1514.3256f, 1384.1793f, 237.7592f, (byte) 116);
			spawn(800727, 1529.3813f, 1392.2281f, 237.7500f, (byte) 83);
			spawn(800727, 1529.4375f, 1371.2086f, 237.7500f, (byte) 38);
		}
	}
	
	private void rewardPlayerInside2() {
		int playerInside = instance.getPlayersInside().size();
		if (playerInside == 1) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
		} else if (playerInside == 2) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
			spawn(800728, 1124.9657f, 1329.2491f, 286.6024f, (byte) 112);
		} else if (playerInside == 3) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
			spawn(800728, 1124.9657f, 1329.2491f, 286.6024f, (byte) 112);
			spawn(800728, 1123.1178f, 1325.2712f, 287.6315f, (byte) 112);
		} else if (playerInside == 4) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
			spawn(800728, 1124.9657f, 1329.2491f, 286.6024f, (byte) 112);
			spawn(800728, 1123.1178f, 1325.2712f, 287.6315f, (byte) 112);
			spawn(800728, 1141.0021f, 1316.1954f, 287.2072f, (byte) 52);
		} else if (playerInside == 5) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
			spawn(800728, 1124.9657f, 1329.2491f, 286.6024f, (byte) 112);
			spawn(800728, 1123.1178f, 1325.2712f, 287.6315f, (byte) 112);
			spawn(800728, 1141.0021f, 1316.1954f, 287.2072f, (byte) 52);
			spawn(800728, 1142.9610f, 1320.3103f, 286.1217f, (byte) 52);
		} else if (playerInside == 6) {
			spawn(800728, 1127.1482f, 1333.2820f, 285.4019f, (byte) 111);
			spawn(800728, 1124.9657f, 1329.2491f, 286.6024f, (byte) 112);
			spawn(800728, 1123.1178f, 1325.2712f, 287.6315f, (byte) 112);
			spawn(800728, 1141.0021f, 1316.1954f, 287.2072f, (byte) 52);
			spawn(800728, 1142.9610f, 1320.3103f, 286.1217f, (byte) 52);
			spawn(800728, 1144.9352f, 1324.2771f, 285.1980f, (byte) 52);
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}