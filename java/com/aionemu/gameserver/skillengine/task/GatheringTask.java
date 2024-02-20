package com.aionemu.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.S_GATHER_OTHER;
import com.aionemu.gameserver.network.aion.serverpackets.S_GATHER;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class GatheringTask extends AbstractCraftTask
{
	private Material material;
	protected int currentSuccessValue;
    protected int currentFailureValue;
    private GatherableTemplate template;
	
    public GatheringTask(Player requestor, Gatherable gatherable, Material material, int skillLvlDiff) {
        super(requestor, gatherable, skillLvlDiff);
        this.template = gatherable.getObjectTemplate();
        this.material = material;
        this.maxSuccessValue = 100;
        this.maxFailureValue = 100;
    }
	
    @Override
    protected void onInteractionAbort() {
        PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, 0, 0, 5));
        PacketSendUtility.broadcastPacket(requestor, new S_GATHER_OTHER(requestor.getObjectId(), responder.getObjectId(), 2));
    }
	
    @Override
    protected void onInteractionFinish() {
        ((Gatherable) responder).getController().completeInteraction();
    }
	
    @Override
    protected void onInteractionStart() {
        PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, maxSuccessValue, maxFailureValue, 0));
        this.onInteraction();
        PacketSendUtility.broadcastPacket(requestor, new S_GATHER_OTHER(requestor.getObjectId(), responder.getObjectId(), 0), true);
        PacketSendUtility.broadcastPacket(requestor, new S_GATHER_OTHER(requestor.getObjectId(), responder.getObjectId(), 1), true);
    }
	
    @Override
    protected void analyzeInteraction() {
        int multi = Math.max(0, 33 - skillLvlDiff * 5);
		if (Rnd.get(100) > multi) {
            currentSuccessValue += Rnd.get(maxSuccessValue / (multi + 1) / 2, maxSuccessValue);
        } else {
            currentFailureValue += Rnd.get(maxFailureValue / (multi + 1) / 2, maxFailureValue);
        } if (currentSuccessValue >= maxFailureValue) {
            currentSuccessValue = maxFailureValue;
        } else if (currentFailureValue >= maxFailureValue) {
            currentFailureValue = maxFailureValue;
        }
    }
	
    @Override
    protected void sendInteractionUpdate() {
        PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, currentSuccessValue, currentFailureValue, this.critType.getPacketId()));
    }
	
    @Override
    protected boolean onInteraction() {
        if (currentSuccessValue == maxSuccessValue) {
            return onSuccessFinish();
        } if (currentFailureValue == maxFailureValue) {
            onFailureFinish();
            return true;
        }
        analyzeInteraction();
        sendInteractionUpdate();
        return false;
    }
	
    @Override
    protected void onFailureFinish() {
        PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, currentSuccessValue, currentFailureValue, 1));
        PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, currentSuccessValue, currentFailureValue, 7));
        PacketSendUtility.broadcastPacket(requestor, new S_GATHER_OTHER(requestor.getObjectId(), responder.getObjectId(), 3), true);
    }
	
    @Override
    protected boolean onSuccessFinish() {
		PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, currentSuccessValue, currentFailureValue, 2));
		PacketSendUtility.sendPacket(requestor, new S_GATHER(template, material, currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor, new S_GATHER_OTHER(requestor.getObjectId(), responder.getObjectId(), 2), true);
        PacketSendUtility.sendPacket(requestor, S_MESSAGE_CODE.STR_EXTRACT_GATHER_SUCCESS_1_BASIC(new DescriptionId(material.getNameid())));
        if (template.getEraseValue() > 0) {
            requestor.getInventory().decreaseByItemId(template.getRequiredItemId(), template.getEraseValue());
        }
		ItemService.addItem(requestor, material.getItemid(), requestor.getRates().getGatheringCountRate());
        if (requestor.isInInstance()) {
            requestor.getPosition().getWorldMapInstance().getInstanceHandler().onGather(requestor, (Gatherable) responder);
        } else {
            requestor.getPosition().getWorld().getWorldMap(requestor.getWorldId()).getWorldHandler().onGather(requestor, (Gatherable) responder);
        }
        ((Gatherable) responder).getController().rewardPlayer(requestor);
        return true;
    }
}