package com.aionemu.gameserver.model.items;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.*;
import com.aionemu.gameserver.skillengine.model.Skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GodStone extends ItemStone
{
    private static final Logger log = LoggerFactory.getLogger(GodStone.class);
	
    private final GodstoneInfo godstoneInfo;
    private ActionObserver actionListener;
    private final int probability;
    private final int probabilityLeft;
    private final ItemTemplate godItem;
	
    public GodStone(int itemObjId, int itemId, PersistentState persistentState) {
        super(itemObjId, itemId, 0, persistentState);
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        godItem = itemTemplate;
        godstoneInfo = itemTemplate.getGodstoneInfo();
        if (godstoneInfo != null) {
            probability = godstoneInfo.getProbability();
            probabilityLeft = godstoneInfo.getProbabilityleft();
        } else {
            probability = 0;
            probabilityLeft = 0;
        }
    }
	
    public void onEquip(final Player player) {
        if (godstoneInfo == null || godItem == null) {
            return;
        }
        final Item equippedItem = player.getEquipment().getEquippedItemByObjId(getItemObjId());
        long equipmentSlot = equippedItem.getEquipmentSlot();
        final int handProbability = equipmentSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? probability : probabilityLeft;
        actionListener = new ActionObserver(ObserverType.ATTACK) {
            @Override
            public void attack(Creature creature) {
                int breakChance = godstoneInfo.getBreakprob();
                int chance = Rnd.get(0, breakChance);
                if (handProbability > Rnd.get(0, 1000)) {
                    Skill skill = SkillEngine.getInstance().getSkill(player, godstoneInfo.getSkillid(), godstoneInfo.getSkilllvl(), player.getTarget(), godItem);
                    //%effect godstone has been activated.
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_PROC_EFFECT_OCCURRED(skill.getSkillTemplate().getNameId()));
                    skill.setFirstTargetRangeCheck(false);
                    if (skill.canUseSkill()) {
                        Effect effect = new Effect(player, creature, skill.getSkillTemplate(), 1, 0, godItem);
                        effect.initialize();
                        effect.applyEffect();
                        effect = null;
                    }
                }
            }
        };
        player.getObserveController().addObserver(actionListener);
    }
	
    public void onUnEquip(Player player) {
        if (actionListener != null) {
            player.getObserveController().removeObserver(actionListener);
        }
    }
	
    @Override
    public int getItemId() {
        return super.getItemId();
    }
}