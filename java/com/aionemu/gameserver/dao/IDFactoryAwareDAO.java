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
package com.aionemu.gameserver.dao;

/**
 * This interface is generic one for all DAO classes that are generating their id's using
 * {@link com.aionemu.gameserver.utils.idfactory.IDFactory}
 *
 * @author SoulKeeper
 */
public abstract class IDFactoryAwareDAO
{
	/**
	 * Returns array of all id's that are used by this DAO
	 *
	 * @return array of used id's
	 */
	public static int[] getUsedIDs()
	{
		return new int[0];
	}
}
