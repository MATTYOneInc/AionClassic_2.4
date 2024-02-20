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
package com.aionemu.gameserver.model.templates.quest;

/****/
/** Author Rinzler (Encom)
/****/

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum QuestTargetType
{
	NONE,
	AREA,
	FORCE,
	UNION
}