package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.PlayerMailboxState;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import java.util.List;

public class S_NPC_HTML_MESSAGE extends AionServerPacket
{
	private int targetObjectId;
	private int dialogID;
	private int questId = 0;
	
	public S_NPC_HTML_MESSAGE(int targetObjectId, int dlgID) {
		this.targetObjectId = targetObjectId;
		this.dialogID = dlgID;
	}
	
	public S_NPC_HTML_MESSAGE(int targetObjectId, int dlgID, int questId) {
		this.targetObjectId = targetObjectId;
		this.dialogID = dlgID;
		this.questId = questId;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		writeD(targetObjectId);
		writeH(dialogID);
		writeD(questId);
		//writeH(0);
		writeD(0);
		if (this.dialogID == DialogPage.MAIL.id()) {
			AionObject object = World.getInstance().findVisibleObject(targetObjectId);
			if (object != null && object instanceof Npc) {
				Npc znpc = (Npc) object;
				if (znpc.getNpcId() == 798100 || znpc.getNpcId() == 798101) {
					player.getMailbox().mailBoxState = PlayerMailboxState.EXPRESS;
					writeH(2);
				} else {
					player.getMailbox().mailBoxState = PlayerMailboxState.REGULAR;
				}
			} else {
				writeH(0);
			}
		} else {
			writeH(0);
		}
	}
}