
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_ITEM_COOLTIME;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_SKILL_COOLTIME;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.Map.Entry;

@AIName("recharger")
public class RechargerAI2 extends NpcAI2
{
	public static void removeCd(Player player) {
        List<Integer> delayIds = new ArrayList<Integer>();
		if (player.getSkillCoolDowns() != null) {
			long currentTime = System.currentTimeMillis();
			for (Entry<Integer, Long> en: player.getSkillCoolDowns().entrySet()) {
				delayIds.add(en.getKey());
			} for (Integer delayId: delayIds) {
				player.setSkillCoolDown(delayId, currentTime);
			}
			delayIds.clear();
			PacketSendUtility.sendPacket(player, new S_LOAD_SKILL_COOLTIME(player.getSkillCoolDowns()));
		}
		PacketSendUtility.sendWhiteMessage(player, "Your skills have been reset!!!");
    }
	
    @Override
    protected void handleDialogStart(final Player player) {
        RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
            @Override
            public void acceptRequest(Creature creature, Player player) {
				removeCd(player);
				player.getLifeStats().increaseHp(TYPE.HP, player.getLifeStats().getMaxHp() + 1);
				player.getLifeStats().increaseMp(TYPE.MP, player.getLifeStats().getMaxMp() + 1);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
				PacketSendUtility.sendWhiteMessage(player, "Your health has been recovered!!!");
            }
            @Override
            public void denyRequest(Creature creature, Player player) {
            }
        };
        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(player, new S_ASK(902247, 0, 0, "Do you want to reset your skills and recover your health ?"));
        }
    }
}