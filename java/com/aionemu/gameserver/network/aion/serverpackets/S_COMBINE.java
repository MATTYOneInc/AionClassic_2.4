package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_COMBINE extends AionServerPacket
{
	private int skillId;
	private int itemId;
	private int action;
	private int success;
	private int failure;
	private int nameId;
	private int executionDelay = 700;
    private int executionPeriod = 1200;
	
	public S_COMBINE(int skillId, ItemTemplate item, int success, int failure, int action) {
		this.action = action;
		this.skillId = skillId;
		this.itemId = item.getTemplateId();
		this.success = success;
		this.failure = failure;
		this.nameId = item.getNameId();
        //Aether Morphing.
        if (skillId == 40009) {
            this.executionPeriod = 1500;
        }
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(skillId);
		writeC(action);
		writeD(itemId);
		switch (action) {
			case 0:
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(1200);
				writeD(1330048);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
			break;
			case 1:
			case 2:
			case 5:
				writeD(success);
				writeD(failure);
				writeD(executionDelay);
				writeD(executionPeriod);
				writeD(0);
				writeH(0);
			break;
			case 3:
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(executionPeriod);
				writeD(1330048);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
			break;
			case 4:
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(0);
				writeD(1330051);
				writeH(0);
			break;
			case 6:
				writeD(success);
				writeD(failure);
				writeD(executionDelay);
                writeD(executionPeriod);
				writeD(1330050);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
			break;
			case 7:
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(executionPeriod);
				writeD(1330050);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
			break;
		}
	}
}