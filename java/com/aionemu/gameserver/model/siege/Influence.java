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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ABYSS_NEXT_PVP_CHANGE_TIME;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.util.Iterator;

public class Influence
{
	private static final Influence instance = new Influence();
	
	//======[ABYSS]=============
	private float abyss_e = 0;
	private float abyss_a = 0;
	private float abyss_b = 0;
	//======[INGGISON]==========
	private float inggison_e = 0;
	private float inggison_a = 0;
	private float inggison_b = 0;
	//======[GELKMAROS]=========
	private float gelkmaros_e = 0;
	private float gelkmaros_a = 0;
	private float gelkmaros_b = 0;
	//======[GLOBAL]============
	private float global_e = 0;
	private float global_a = 0;
	private float global_b = 0;
	
	private Influence() {
		calculateInfluence();
	}
	
	public static Influence getInstance() {
		return instance;
	}
	
	public void recalculateInfluence() {
		calculateInfluence();
	}
	
	private void calculateInfluence() {
		float balaurea = 0.0019512194f;
		float abyss = 0.006097561f;
		//======[ABYSS]==========
		float e_abyss = 0f;
		float a_abyss = 0f;
		float b_abyss = 0f;
		float t_abyss = 0f;
		//=======[INGGISON]======
		float e_inggison = 0f;
		float a_inggison = 0f;
		float b_inggison = 0f;
		float t_inggison = 0f;
		//======[GELKMAROS]======
		float e_gelkmaros = 0f;
		float a_gelkmaros = 0f;
		float b_gelkmaros = 0f;
		float t_gelkmaros = 0f;
		for (SiegeLocation sLoc : SiegeService.getInstance().getSiegeLocations().values()) {
			switch (sLoc.getWorldId()) {
				//======[RESHANTA]==========
				case 400010000:
				    if (sLoc instanceof FortressLocation) {
						t_abyss += sLoc.getInfluenceValue();
						switch (sLoc.getRace()) {
							case ELYOS:
							   e_abyss += sLoc.getInfluenceValue();
							break;
							case ASMODIANS:
							   a_abyss += sLoc.getInfluenceValue();
							break;
							case BALAUR:
							   b_abyss += sLoc.getInfluenceValue();
							break;
						}
					}
				break;
				//=======[INGGISON]======
				case 210050000:
					if (sLoc instanceof FortressLocation) {
						t_inggison += sLoc.getInfluenceValue();
						switch (sLoc.getRace()) {
							case ELYOS:
								e_inggison += sLoc.getInfluenceValue();
							break;
							case ASMODIANS:
								a_inggison += sLoc.getInfluenceValue();
							break;
							case BALAUR:
								b_inggison += sLoc.getInfluenceValue();
							break;
						}
					}
				break;
				//======[GELKMAROS]======
				case 220070000:
					if (sLoc instanceof FortressLocation) {
						t_gelkmaros += sLoc.getInfluenceValue();
						switch (sLoc.getRace()) {
							case ELYOS:
								e_gelkmaros += sLoc.getInfluenceValue();
							break;
							case ASMODIANS:
								a_gelkmaros += sLoc.getInfluenceValue();
							break;
							case BALAUR:
								b_gelkmaros += sLoc.getInfluenceValue();
							break;
						}
					}
				break;
			}
		}
		//======[ABYSS]=========
		abyss_e = (e_abyss / t_abyss);
		abyss_a = (a_abyss / t_abyss);
		abyss_b = (b_abyss / t_abyss);
		//======[INGGISON]======
		inggison_e = (e_inggison / t_inggison);
		inggison_a = (a_inggison / t_inggison);
		inggison_b = (b_inggison / t_inggison);
		//======[GELKMAROS]=====
		gelkmaros_e = (e_gelkmaros / t_gelkmaros);
		gelkmaros_a = (a_gelkmaros / t_gelkmaros);
		gelkmaros_b = (b_gelkmaros / t_gelkmaros);
		
		//======[GLOBAL]========
		global_e = (inggison_e * balaurea + gelkmaros_e * balaurea + abyss_e * abyss) * 100f;
		global_a = (inggison_a * balaurea + gelkmaros_a * balaurea + abyss_a * abyss) * 100f;
		global_b = (inggison_b * balaurea + gelkmaros_b * balaurea + abyss_b * abyss) * 100f;
	}
	
	@SuppressWarnings("unused")
	private void broadcastInfluencePacket() {
		S_ABYSS_NEXT_PVP_CHANGE_TIME pkt = new S_ABYSS_NEXT_PVP_CHANGE_TIME();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			PacketSendUtility.sendPacket(player, pkt);
		}
	}
	
	//=======[RESHANTA]=======
	public float getAbyssElyosInfluence() {
		return abyss_e;
	}
	public float getAbyssAsmodiansInfluence() {
		return abyss_a;
	}
	public float getAbyssBalaursInfluence() {
		return abyss_b;
	}
	//=======[INGGISON]======
	public float getInggisonElyosInfluence() {
		return inggison_e;
	}
	public float getInggisonAsmodiansInfluence() {
		return inggison_a;
	}
	public float getInggisonBalaursInfluence() {
		return inggison_b;
	}
	//======[GELKMAROS]======
	public float getGelkmarosElyosInfluence() {
		return gelkmaros_e;
	}
	public float getGelkmarosAsmodiansInfluence() {
		return gelkmaros_a;
	}
	public float getGelkmarosBalaursInfluence() {
		return gelkmaros_b;
	}
	//=======[GLOBAL]=========
	public float getGlobalElyosInfluence() {
		return global_e;
	}
	public float getGlobalAsmodiansInfluence() {
		return global_a;
	}
	public float getGlobalBalaursInfluence() {
		return global_b;
	}
	
	public float getPvpRaceBonus(Race attRace) {
		float bonus = 1.0f;
		float elyos = getGlobalElyosInfluence();
		float asmo = getGlobalAsmodiansInfluence();
		switch (attRace) {
		    case ASMODIANS:
			    if (elyos >= 0.81f && asmo <= 0.1f) {
				    bonus = 1.2f;
			    } if (elyos >= 0.81f || elyos >= 0.71f && asmo <= 0.1f) {
				    bonus = 1.15f;
			    } if (elyos >= 0.71f) {
				    bonus = 1.1f;
			    }
		    break;
		    case ELYOS:
			    if (asmo >= 0.81f && elyos <= 0.1f) {
				    bonus = 1.2f;
				} if (asmo >= 0.81f || asmo >= 0.71f && elyos <= 0.1f) {
				    bonus = 1.15f;
				} if (asmo >= 0.71f) {
				    bonus = 1.1f;
				}
			break;
		}
		return bonus;
	}
}