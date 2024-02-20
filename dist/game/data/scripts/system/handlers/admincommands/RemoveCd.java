package admincommands;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_ITEM_COOLTIME;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_SKILL_COOLTIME;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

import java.util.*;
import java.util.Map.Entry;

public class RemoveCd extends AdminCommand
{
	public RemoveCd() {
		super("removecd");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		VisibleObject target = admin.getTarget();
		if(target == null)
			target = admin;
		   
		
		if (target instanceof Player) {
			Player player = (Player) target;
			if (params.length == 0) {
				List<Integer> delayIds = new ArrayList<Integer>();
				if (player.getSkillCoolDowns() != null) {
					for (Entry<Integer, Long> en : player.getSkillCoolDowns().entrySet())
						delayIds.add(en.getKey());

					for (Integer delayId : delayIds)
						player.setSkillCoolDown(delayId, 0);

					delayIds.clear();
					PacketSendUtility.sendPacket(player, new S_LOAD_SKILL_COOLTIME(player.getSkillCoolDowns()));
				}

				if (player.getItemCoolDowns() != null) {
					for (Entry<Integer, ItemCooldown> en : player.getItemCoolDowns().entrySet())
						delayIds.add(en.getKey());

					for (Integer delayId : delayIds)
						player.addItemCoolDown(delayId, 0, 0);

					delayIds.clear();
					PacketSendUtility.sendPacket(player, new S_LOAD_ITEM_COOLTIME(player.getItemCoolDowns()));
				}

				if (player.equals(admin))
					PacketSendUtility.sendMessage(admin, "Your cooldowns were removed");
				else {
					PacketSendUtility.sendMessage(admin, "You have removed cooldowns of player: " + player.getName());
					PacketSendUtility.sendMessage(player, "Your cooldowns were removed by admin");
				}
			}
		}
		else
			PacketSendUtility.sendMessage(admin, "Only players are allowed as target");
	}
}