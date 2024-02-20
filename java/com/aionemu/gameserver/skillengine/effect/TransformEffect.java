package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_POLYMORPH;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformEffect")
public abstract class TransformEffect extends EffectTemplate
{
	@XmlAttribute
	protected int model;
	
	@XmlAttribute
    protected TransformType type = TransformType.NONE;
	
	@XmlAttribute
	protected int panelid;
	
	@XmlAttribute
	protected int itemId;
	
	@XmlAttribute
    protected int skillId;
	
	@XmlAttribute
	protected AbnormalState state = AbnormalState.BUFF;
	
	@Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
        if (state != null) {
            effect.getEffected().getEffectController().setAbnormal(state.getId());
            effect.setAbnormal(state.getId());
        }
    }
	
	public void endEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		int newModel = 0;
		int oldPanelid = 0;
        int oldEquipment = 0;
        int oldSkillId = 0;
		TransformType transformType = TransformType.PC;
        if (this.state != null) {
            effected.getEffectController().unsetAbnormal(this.state.getId());
        } if (effected instanceof Player) {
			for (Effect tmp : effected.getEffectController().getAbnormalEffects()) {
                for (EffectTemplate template : tmp.getEffectTemplates()) {
                    if ((template instanceof TransformEffect)) {
                        if (((TransformEffect) template).getTransformId() != this.model) {
                            newModel = ((TransformEffect) template).getTransformId();
                            transformType = ((TransformEffect) template).getTransformType();
                            oldPanelid = ((TransformEffect) template).getPanelId();
                            oldEquipment = ((TransformEffect) template).getItemId();
                            oldSkillId = ((TransformEffect) template).getSkillId();
                            break;
                        }
                    }
                }
            }
			effected.getTransformModel().setModelId(newModel);
            effected.getTransformModel().setPanelId(oldPanelid);
            effected.getTransformModel().setItemId(oldEquipment);
            effected.getTransformModel().setTransformType(transformType);
            effected.getTransformModel().setSkillId(oldSkillId);
			effected.getTransformModel().setModelId(0);
			PacketSendUtility.broadcastPacketAndReceive(effected, new S_POLYMORPH(effected, 0, false));
			effected.getEffectController().clearEffect(effect);
            PacketSendUtility.broadcastPacketAndReceive(effected, new S_POLYMORPH(effected, oldPanelid, false));
			if (((effected instanceof Player)) &&
			    ((transformType == TransformType.PC) || (transformType == TransformType.NONE) ||
				 (transformType == TransformType.FORM1) || (transformType == TransformType.FORM2) ||
				 (transformType == TransformType.FORM3) || (transformType == TransformType.FORM4) ||
				 (transformType == TransformType.FORM5) || (transformType == TransformType.AVATAR))) {
                ((Player) effected).setTransformed(false);
            }
		} else if ((effected instanceof Summon)) {
            effected.getTransformModel().setModelId(0);
            PacketSendUtility.broadcastPacketAndReceive(effected, new S_POLYMORPH(effected, 0, false));
        } else if ((effected instanceof Npc)) {
            effected.getTransformModel().setModelId(effected.getObjectTemplate().getTemplateId());
            PacketSendUtility.broadcastPacketAndReceive(effected, new S_POLYMORPH(effected, 0, false));
        }
		super.endEffect(effect);
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getTransformModel().setModelId(model);
        effected.getTransformModel().setPanelId(this.panelid);
        effected.getTransformModel().setItemId(this.itemId);
		effected.getTransformModel().setSkillId(effect.getSkillId());
        effected.getTransformModel().setTransformType(effect.getTransformType());
        PacketSendUtility.broadcastPacketAndReceive(effected, new S_POLYMORPH(effected, true));
		if ((effected instanceof Player)) {
            ((Player) effected).setTransformed(true);
        } if (effected instanceof Player) {
            ((Player) effected).setTransformed(true);
            ((Player) effected).setTransformedModelId(model);
            ((Player) effected).setTransformedPanelId(panelid);
            ((Player) effected).setTransformedItemId(itemId);
        }
		super.startEffect(effect);
	}
	
	public TransformType getTransformType() {
		return type;
	}
	
	public int getTransformId() {
		return model;
	}
	
	public int getPanelId() {
		return panelid;
	}
	
	public int getItemId() {
        return itemId;
    }
	
	public int getSkillId() {
        return skillId;
    }
}