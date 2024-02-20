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
package ai.instance.crucibleChallenge;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("barrel")
public class BarrelAI2 extends NpcAI2
{
	@Override
	protected void handleDied() {
		super.handleDied();
		int npcId = 0;
		switch (getNpcId()) {
			case 217840: //Meat Barrel.
				npcId = 217841; //Wafer Thin Meet.
			break;
			case 218560: //Aether Barrel.
				npcId = 218561; //Aether Lump.
			break;
		}
		spawn(npcId, 1298.4448f, 1728.3262f, 316.8472f, (byte) 63);
		AI2Actions.deleteOwner(this);
	}
}