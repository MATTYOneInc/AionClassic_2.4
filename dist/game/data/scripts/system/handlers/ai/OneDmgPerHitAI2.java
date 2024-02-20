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
package ai;

import com.aionemu.gameserver.ai2.AIName;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("onedmgperhit")
public class OneDmgPerHitAI2 extends NoActionAI2
{
	@Override
	public int modifyDamage(int damage) {
		return 1;
	}
	
	@Override
	public int modifyOwnerDamage(int damage) {
		return 1;
	}
}