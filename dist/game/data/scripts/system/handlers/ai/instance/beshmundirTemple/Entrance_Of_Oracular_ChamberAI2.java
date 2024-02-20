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
package ai.instance.beshmundirTemple;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_GAUGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("entranceoforacularchamber")
public class Entrance_Of_Oracular_ChamberAI2 extends NpcAI2
{
    protected int startBarAnimation = 1;
	protected int cancelBarAnimation = 2;
	
	@Override
    protected void handleDialogStart(Player player) {
		if (player.getRace() == Race.ELYOS) {
			QuestState qsE = player.getQuestStateList().getQuestState(30208); //[Group] The Truth Hurts.
            if (qsE != null && qsE.getStatus() != QuestStatus.NONE) {
                handleUseItemStart(player);
            } else {
                PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 27));
				///You cannot use it as the required quest has not been completed.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
            }
        } else if (player.getRace() == Race.ASMODIANS) {
			QuestState qsA = player.getQuestStateList().getQuestState(30308); //[Group] Summon Respondent Utra.
            if (qsA != null && qsA.getStatus() != QuestStatus.NONE) {
                handleUseItemStart(player);
            } else {
                PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 27));
				///You cannot use it as the required quest has not been completed.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
            }
        }
    }
	
	protected void handleUseItemStart(final Player player) {
		final int delay = getTalkDelay();
		if (delay != 0) {
			final ItemUseObserver observer = new ItemUseObserver() {
				@Override
				public void abort() {
					player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
					PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), 0, cancelBarAnimation));
					player.getObserveController().removeObserver(this);
				}
			};
			player.getObserveController().attach(observer);
			PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), getTalkDelay(), startBarAnimation));
			PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
			player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), getObjectId(), getTalkDelay(), cancelBarAnimation));
					player.getObserveController().removeObserver(observer);
					handleUseItemFinish(player);
				}
			}, delay));
		} else {
			handleUseItemFinish(player);
		}
	}
	
    protected void handleUseItemFinish(Player player) {
        if (getOwner().isInInstance()) {
			AI2Actions.dieSilently(this, player);
		}
    }
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
	
	protected int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}
}