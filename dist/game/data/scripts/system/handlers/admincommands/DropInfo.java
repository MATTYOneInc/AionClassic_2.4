package admincommands;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.CommonDropItemTemplate;
import com.aionemu.gameserver.model.drop.CommonItemTemplate;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcCommonDrop;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Oliver
 */
public class DropInfo extends AdminCommand {

	public DropInfo() {
		super("dropinfo");
	}

	@Override
	public void execute(Player player, String... params) {
		NpcTemplate npcTemplate;
		if (params.length > 0) {
			int npcId = Integer.parseInt(params[0]);
			npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
			if (npcTemplate == null){
				PacketSendUtility.sendMessage(player, "Incorrect npcId: "+ npcId);
				return;
			}

		}
		else {
			VisibleObject visibleObject = player.getTarget();
			npcTemplate = DataManager.NPC_DATA.getNpcTemplate(visibleObject.getObjectTemplate().getTemplateId());
			if (visibleObject == null) {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}

		}
		
		int count = 0;
		PacketSendUtility.sendMessage(player, "[Drop Info for the specified NPC]\n");
		if(npcTemplate.getCommonsdrops() != null) {
			for (NpcCommonDrop npcCommonDrop: npcTemplate.getCommonsdrops()) {
				PacketSendUtility.sendMessage(player, "[item:" + npcCommonDrop.getItemId() + "]");
				count ++;
			}
		}
		if(npcTemplate.getDropGroup() != null){
			for (Integer id : npcTemplate.getDropGroup().getIds()) {
				CommonDropItemTemplate commonDropItemTemplate = DataManager.COMMONS_DROP_DATA.getTemplate(id);
				for (CommonItemTemplate commonItemTemplate : commonDropItemTemplate.getCommonItems()) {
					PacketSendUtility.sendMessage(player, "[item:" + commonItemTemplate.getItemId() + "]");
					count ++;
				}
			}
		}

		PacketSendUtility.sendMessage(player, count + " drops available for the selected NPC");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
