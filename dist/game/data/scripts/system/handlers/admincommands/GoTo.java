package admincommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

public class GoTo extends AdminCommand
{
	public GoTo() {
		super("goto");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //goto <location>");
			return;
		}
		StringBuilder sbDestination = new StringBuilder();
		for(String p: params) {
			sbDestination.append(p + " ");
		}
		String destination = sbDestination.toString().trim();
		
		if (destination.equalsIgnoreCase("Sanctum"))
			goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		else if (destination.equalsIgnoreCase("Cloister"))
			goTo(player, WorldMapType.CLOISTER_OF_KAISINEL.getId(), 2155, 1567, 1205);
		else if (destination.equalsIgnoreCase("Poeta"))
			goTo(player, WorldMapType.POETA.getId(), 829, 1231, 118);
		else if (destination.equalsIgnoreCase("Melponeh"))
			goTo(player, WorldMapType.POETA.getId(), 426, 1740, 119);
		else if (destination.equalsIgnoreCase("Verteron"))
			goTo(player, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
		else if (destination.equalsIgnoreCase("Cantas"))
			goTo(player, WorldMapType.VERTERON.getId(), 2384, 788, 102);
		else if (destination.equalsIgnoreCase("Ardus"))
			goTo(player, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
		else if (destination.equalsIgnoreCase("Pilgrims"))
			goTo(player, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
		else if (destination.equalsIgnoreCase("Tolbas"))
			goTo(player, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
		else if (destination.equalsIgnoreCase("Eltnen"))
			goTo(player, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
		else if (destination.equalsIgnoreCase("Golden"))
			goTo(player, WorldMapType.ELTNEN.getId(), 688, 431, 332);
		else if (destination.equalsIgnoreCase("Eltnen Observatory"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
		else if (destination.equalsIgnoreCase("Novan"))
			goTo(player, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
		else if (destination.equalsIgnoreCase("Agairon"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
		else if (destination.equalsIgnoreCase("Kuriullu"))
			goTo(player, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
		else if (destination.equalsIgnoreCase("Theobomos"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1398, 1557, 31);
		else if (destination.equalsIgnoreCase("Jamanok"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 458, 1257, 127);
		else if (destination.equalsIgnoreCase("Meniherk"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1396, 1560, 31);
		else if (destination.equalsIgnoreCase("obsvillage"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2234, 2284, 50);
		else if (destination.equalsIgnoreCase("Josnack"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 901, 2774, 62);
		else if (destination.equalsIgnoreCase("Anangke"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2681, 847, 138);
		else if (destination.equalsIgnoreCase("Heiron"))
			goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		else if (destination.equalsIgnoreCase("Heiron Observatory"))
			goTo(player, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
		else if (destination.equalsIgnoreCase("Senemonea"))
			goTo(player, WorldMapType.HEIRON.getId(), 971, 686, 135);
		else if (destination.equalsIgnoreCase("Jeiaparan"))
			goTo(player, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
		else if (destination.equalsIgnoreCase("Changarnerk"))
			goTo(player, WorldMapType.HEIRON.getId(), 916, 2256, 157);
		else if (destination.equalsIgnoreCase("Kishar"))
			goTo(player, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
		else if (destination.equalsIgnoreCase("Arbolu"))
			goTo(player, WorldMapType.HEIRON.getId(), 170, 1662, 120);
		else if  (destination.equalsIgnoreCase("Pandaemonium"))
			goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
		else if (destination.equalsIgnoreCase("Convent"))
			goTo(player, WorldMapType.CONVENT_OF_MARCHUTAN.getId(), 1557, 1429, 266);
		else if (destination.equalsIgnoreCase("Ishalgen"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 579, 2445, 279);
		else if (destination.equalsIgnoreCase("Anturoon"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
		else if (destination.equalsIgnoreCase("Altgard"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
		else if (destination.equalsIgnoreCase("Basfelt"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
		else if (destination.equalsIgnoreCase("Trader"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
		else if (destination.equalsIgnoreCase("Impetusium"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
		else if (destination.equalsIgnoreCase("Altgard Observatory"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
		else if (destination.equalsIgnoreCase("Morheim"))
			goTo(player, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
		else if (destination.equalsIgnoreCase("Desert"))
			goTo(player, WorldMapType.MORHEIM.getId(), 634, 900, 360);
		else if (destination.equalsIgnoreCase("Slag"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
		else if (destination.equalsIgnoreCase("Kellan"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
		else if (destination.equalsIgnoreCase("Alsig"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
		else if (destination.equalsIgnoreCase("Morheim Observatory"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
		else if (destination.equalsIgnoreCase("Halabana"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
		else if (destination.equalsIgnoreCase("Brusthonin"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
		else if (destination.equalsIgnoreCase("Baltasar"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
		else if (destination.equalsIgnoreCase("Bollu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
		else if (destination.equalsIgnoreCase("Edge"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
		else if (destination.equalsIgnoreCase("Bubu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
		else if (destination.equalsIgnoreCase("Settlers"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
		else if (destination.equalsIgnoreCase("Beluslan"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		else if (destination.equalsIgnoreCase("Besfer"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
		else if (destination.equalsIgnoreCase("Kidorun"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
		else if (destination.equalsIgnoreCase("Red Mane"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
		else if (destination.equalsIgnoreCase("Kistenian"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
		else if (destination.equalsIgnoreCase("Hoarfrost"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);
		
		else if (destination.equalsIgnoreCase("haramel") || destination.equalsIgnoreCase("Haramel"))
			goTo(player, 300200000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("nochsana") || destination.equalsIgnoreCase("Nochsana Training Camp"))
			goTo(player, 300030000, 513, 668, 331);
		else if (destination.equalsIgnoreCase("arcanis") || destination.equalsIgnoreCase("Sky Temple Of Arcanis"))
			goTo(player, 320050000, 177, 229, 536);
		else if (destination.equalsIgnoreCase("firetemple") || destination.equalsIgnoreCase("Fire Temple"))
			goTo(player, 320100000, 148, 461, 141);
		else if (destination.equalsIgnoreCase("kromede") || destination.equalsIgnoreCase("Kromede Trial"))
			goTo(player, 300230000, 248, 244, 189);
		else if (destination.equalsIgnoreCase("rake") || destination.equalsIgnoreCase("Steel Rake"))
			goTo(player, 300100000, 237, 504, 948);
		else if (destination.equalsIgnoreCase("rake2") || destination.equalsIgnoreCase("Steel Rake [Quest]"))
			goTo(player, 300450000, 237, 504, 948);
		else if (destination.equalsIgnoreCase("indratu") || destination.equalsIgnoreCase("Indratu Fortress"))
			goTo(player, 310090000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("azoturan") || destination.equalsIgnoreCase("Azoturan Fortress"))
			goTo(player, 310100000, 300, 338, 1018);
		else if (destination.equalsIgnoreCase("lab1") || destination.equalsIgnoreCase("Aetherogenetics Lab"))
			goTo(player, 310050000, 360, 230, 147);
		else if (destination.equalsIgnoreCase("adma") || destination.equalsIgnoreCase("Adma Stronghold"))
			goTo(player, 320130000, 450, 200, 168);
		else if (destination.equalsIgnoreCase("alquimia") || destination.equalsIgnoreCase("Alquimia Research Center"))
			goTo(player, 320110000, 603, 527, 200);
		else if (destination.equalsIgnoreCase("draupnir") || destination.equalsIgnoreCase("Draupnir Cave"))
			goTo(player, 320080000, 491, 373, 622);
		else if (destination.equalsIgnoreCase("lab2") || destination.equalsIgnoreCase("Theobomos Lab"))
			goTo(player, 310110000, 477, 201, 170);
		else if (destination.equalsIgnoreCase("dp") || destination.equalsIgnoreCase("Dark Poeta"))
			goTo(player, 300040000, 1224, 419, 140);
		else if (destination.equalsIgnoreCase("left") || destination.equalsIgnoreCase("Left Wing Chamber"))
			goTo(player, 300080000, 672, 606, 321);
		else if (destination.equalsIgnoreCase("right") || destination.equalsIgnoreCase("Right Wing Chamber"))
			goTo(player, 300090000, 263, 386, 103);
		else if (destination.equalsIgnoreCase("asteria") || destination.equalsIgnoreCase("Asteria Chamber"))
			goTo(player, 300050000, 469, 568, 202);
		else if (destination.equalsIgnoreCase("roah") || destination.equalsIgnoreCase("Chamber Of Roah"))
			goTo(player, 300070000, 504, 396, 94);
		else if (destination.equalsIgnoreCase("baranath") || destination.equalsIgnoreCase("Baranath Dredgion"))
			goTo(player, 300110000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("chantra") || destination.equalsIgnoreCase("Chantra Dredgion"))
			goTo(player, 300210000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("taloc") || destination.equalsIgnoreCase("Taloc's Hollow"))
			goTo(player, 300190000, 200, 214, 1099);
		else if (destination.equalsIgnoreCase("udas") || destination.equalsIgnoreCase("Udas Temple"))
			goTo(player, 300150000, 637, 657, 134);
		else if (destination.equalsIgnoreCase("udas2") || destination.equalsIgnoreCase("Lower Udas Temple"))
			goTo(player, 300160000, 1146, 277, 116);
		else if (destination.equalsIgnoreCase("besh") || destination.equalsIgnoreCase("Beshmundir Temple"))
			goTo(player, 300170000, 1477, 237, 243);
		else if (destination.equalsIgnoreCase("padma") || destination.equalsIgnoreCase("Padmarashka Cave"))
			goTo(player, 320150000, 385, 506, 66);
		else if (destination.equalsIgnoreCase("karamatis1") || destination.equalsIgnoreCase("Karamatis A"))
			goTo(player, 310010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("karamatis2") || destination.equalsIgnoreCase("Karamatis B"))
			goTo(player, 310020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("karamatis3") || destination.equalsIgnoreCase("Karamatis C"))
			goTo(player, 310120000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("aerdina") || destination.equalsIgnoreCase("Aerdina"))
			goTo(player, 310030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("geranaia") || destination.equalsIgnoreCase("Geranaia"))
			goTo(player, 310040000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("sliver") || destination.equalsIgnoreCase("Sliver Of Darkness"))
			goTo(player, 310070000, 247, 249, 1392);
		else if (destination.equalsIgnoreCase("space") || destination.equalsIgnoreCase("Space Of Destiny"))
			goTo(player, 320070000, 246, 246, 125);
		else if (destination.equalsIgnoreCase("ataxiar1") || destination.equalsIgnoreCase("Ataxiar A"))
			goTo(player, 320010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("ataxiar2") || destination.equalsIgnoreCase("Ataxiar B"))
			goTo(player, 320020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("bregirun") || destination.equalsIgnoreCase("Bregirun"))
			goTo(player, 320030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("nidalber") || destination.equalsIgnoreCase("Nidalber"))
			goTo(player, 320040000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("sanctum arena") || destination.equalsIgnoreCase("Sanctum Undeground Arena"))
			goTo(player, 310080000, 275, 242, 159);
		else if (destination.equalsIgnoreCase("triniel arena") || destination.equalsIgnoreCase("Triniel Undeground Arena"))
			goTo(player, 320090000, 275, 239, 159);
		else if (destination.equalsIgnoreCase("prisone") || destination.equalsIgnoreCase("Prison Elyos"))
			goTo(player, 510010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("prisona") || destination.equalsIgnoreCase("Prison Asmos"))
			goTo(player, 520010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("IDAbPro"))
			goTo(player, 300010000, 270, 200, 206);
		else if (destination.equalsIgnoreCase("gm"))
			goTo(player, 120020000, 1442, 1133, 302);
		else if (destination.equalsIgnoreCase("academy"))
			goTo(player, 110070000, 459, 251, 128);
		else if (destination.equalsIgnoreCase("priory"))
			goTo(player, 120080000, 577, 250, 94);
		else if (destination.equalsIgnoreCase("splinter") || destination.equalsIgnoreCase("Abyssal Splinter"))
			goTo(player, 300220000, 704, 153, 453);
		else if (destination.equalsIgnoreCase("aturam") || destination.equalsIgnoreCase("Aturam Sky Fortress"))
			goTo(player, 300240000, 684, 460, 655);
		else if (destination.equalsIgnoreCase("esoterrace") || destination.equalsIgnoreCase("Esoterrace"))
			goTo(player, 300250000, 333, 437, 326);
		else if (destination.equalsIgnoreCase("forest") || destination.equalsIgnoreCase("Elementis Forest"))
            goTo(player, 300260000, 176, 612, 231);
		else if (destination.equalsIgnoreCase("manor") || destination.equalsIgnoreCase("Argent Manor"))
            goTo(player, 300270000, 1005, 1089, 70);
		else if (destination.equalsIgnoreCase("rentus") || destination.equalsIgnoreCase("Rentus Base"))
			goTo(player, 300280000, 611, 128, 45);
		else if (destination.equalsIgnoreCase("crucible") || destination.equalsIgnoreCase("Empyrean Crucible"))
			goTo(player, 300300000, 354, 350, 96);
		else if (destination.equalsIgnoreCase("raksang") || destination.equalsIgnoreCase("Raksang"))
            goTo(player, 300310000, 850, 948, 1206);
		else if (destination.equalsIgnoreCase("challenge") || destination.equalsIgnoreCase("Crucible Challenge"))
			goTo(player, 300320000, 357, 1662, 96);
		else if (destination.equalsIgnoreCase("chaos") || destination.equalsIgnoreCase("Arena Of Chaos"))
			goTo(player, 300350000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("discipline") || destination.equalsIgnoreCase("Arena Of Discipline"))
			goTo(player, 300360000, 707, 1779, 165);
		else if (destination.equalsIgnoreCase("chaos2") || destination.equalsIgnoreCase("Chaos Training Grounds"))
			goTo(player, 300420000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("discipline2") || destination.equalsIgnoreCase("Discipline Training Grounds"))
			goTo(player, 300430000, 707, 1779, 165);
		else if (destination.equalsIgnoreCase("tiak") || destination.equalsIgnoreCase("Tiak Research Base"))
			goTo(player, 300440000, 827, 700, 160);
		else if (destination.equalsIgnoreCase("tiak2") || destination.equalsIgnoreCase("Closed Tiak Research Center"))
			goTo(player, 300460000, 453, 576, 147);
		else if (destination.equalsIgnoreCase("glory") || destination.equalsIgnoreCase("Arena Of Glory"))
			goTo(player, 300470000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("draupnir2") || destination.equalsIgnoreCase("Twisted Draupnir Cave"))
			goTo(player, 300480000, 870, 528, 329);
		else if (destination.equalsIgnoreCase("besh2") || destination.equalsIgnoreCase("Unyielding Beshmundir Temple"))
			goTo(player, 300490000, 737, 510, 199);
		else if (destination.equalsIgnoreCase("tempus1") || destination.equalsIgnoreCase("Tempus"))
			goTo(player, 300500000, 799, 384, 161);
		else if (destination.equalsIgnoreCase("tempus2") || destination.equalsIgnoreCase("Tempus [Quest]"))
			goTo(player, 300510000, 799, 384, 161);
		else if (destination.equalsIgnoreCase("telos2") || destination.equalsIgnoreCase("Telos Of The Forgotten"))
			goTo(player, 300550000, 884, 1763, 360);
		else if (destination.equalsIgnoreCase("telos3") || destination.equalsIgnoreCase("Telos Of The Forgotten Hard"))
			goTo(player, 300560000, 1150, 1361, 279);
		//**Zones Both Race**//
		if (destination.equalsIgnoreCase("silentera")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 600010000, 504, 410, 327);
			} else {
				goTo(player, 600010000, 500, 1139, 332);
			}
		} if (destination.equalsIgnoreCase("telos")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 210070000, 1406, 187, 226);
			} else {
				goTo(player, 220080000, 1406, 187, 226);
			}
		}
		else if (destination.equalsIgnoreCase("inggison"))
			goTo(player, 210050000, 1335, 276, 590);
		else if (destination.equalsIgnoreCase("gelkmaros"))
			goTo(player, 220070000, 1763, 2911, 554);
		else if (destination.equalsIgnoreCase("teminon"))
			goTo(player, 400010000, 2923, 972, 1538);
		else if (destination.equalsIgnoreCase("primum"))
			goTo(player, 400010000, 1068, 2850, 1636);
		else if (destination.equalsIgnoreCase("sarpan"))
			goTo(player, 600020000, 1374, 1455, 600);
		
		//Siege 2.x
        else if (destination.equalsIgnoreCase("1011"))
            goTo(player, 400010000, 2137, 1930, 2322);
        else if (destination.equalsIgnoreCase("2011"))
            goTo(player, 210050000, 1747, 2174, 335);
        else if (destination.equalsIgnoreCase("2021"))
            goTo(player, 210050000, 859, 1918, 348);
		else if (destination.equalsIgnoreCase("3011"))
            goTo(player, 220070000, 1196, 869, 321);
        else if (destination.equalsIgnoreCase("3021"))
            goTo(player, 220070000, 1879, 1107, 337);
		else
			PacketSendUtility.sendMessage(player, "Could not find the specified destination !");
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType()) {
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		} else {
			TeleportService2.teleportTo(player, worldId, x, y, z);
		}
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null) {
				return registeredInstance.getInstanceId();
			}
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //goto <location>");
	}
}