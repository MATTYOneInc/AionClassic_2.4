package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShapeChangeEffect")
public class ShapeChangeEffect extends TransformEffect
{
	@Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        if (effect.getEffected() instanceof Player) {
            final Player player = (Player) effect.getEffected();
            if (effect.getSkillId() == 1863 || effect.getSkillId() == 1864 ||
			    effect.getSkillId() == 1865 || effect.getSkillId() == 1868 ||
			    effect.getSkillId() == 2252 || effect.getSkillId() == 8197 ||
			    effect.getSkillId() == 8343 || effect.getSkillId() == 8352 ||
				effect.getSkillId() == 17241 || effect.getSkillId() == 18469 ||
				effect.getSkillId() == 18472 || effect.getSkillId() == 18480) {
				player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
					@Override
					public void visit(Npc npc) {
						PacketSendUtility.sendPacket(player, new S_CHANGE_FLAG(npc.getObjectId(), 0, NpcType.NON_ATTACKABLE.getId(), 0));
					}
				});
			}
        }
    }
	
	@Override
    public void startEffect(Effect effect) {
        if ((effect.getEffector() instanceof Player)) {
			if (effect.getEffector().getEffectController().isAbnormalSet(AbnormalState.HIDE)) {
				effect.getEffector().getEffectController().removeHideEffects();
			}
		} if (model > 0) {
            Creature effected = effect.getEffected();
            NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(model);
            if (template != null) {
                effected.getTransformModel().setTribe(template.getTribe(), false);
            }
        }
        super.startEffect(effect);
    }
	
	@Override
    public void endEffect(Effect effect) {
        effect.getEffected().getTransformModel().setActive(false);
        if (effect.getEffected() instanceof Player) {
            final Player player = (Player) effect.getEffected();
            player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
                @Override
                public void visit(Npc npc) {
					if (npc.getObjectTemplate().getNpcType() == NpcType.NON_ATTACKABLE) {
						player.getTransformModel().setTribe(null, false);
						PacketSendUtility.sendPacket(player, new S_CHANGE_FLAG(npc.getObjectId(), 0, NpcType.NON_ATTACKABLE.getId(), 0));
					} else {
						player.getTransformModel().setTribe(null, false);
						PacketSendUtility.sendPacket(player, new S_CHANGE_FLAG(npc.getObjectId(), 0, NpcType.ATTACKABLE.getId(), 0));
					}
                }
            });
        }
        super.endEffect(effect);
    }
}