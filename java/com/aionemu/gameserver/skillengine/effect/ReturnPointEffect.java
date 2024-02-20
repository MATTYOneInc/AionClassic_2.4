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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.Effect;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnPointEffect")
public class ReturnPointEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffected().isInInstance()) {
			InstanceService.onLeaveInstance((Player) effect.getEffector());
		}
		ItemTemplate itemTemplate = effect.getItemTemplate();
		int worldId = itemTemplate.getReturnWorldId();
		String pointAlias = itemTemplate.getReturnAlias();
		TeleportService2.useTeleportScroll((Player) effect.getEffector(), pointAlias, worldId);
	}
	
	@Override
	public void calculate(Effect effect) {
		ItemTemplate itemTemplate = effect.getItemTemplate();
		if (itemTemplate != null) {
			effect.addSucessEffect(this);
		}
	}
}