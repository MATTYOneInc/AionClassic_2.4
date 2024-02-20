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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _1042Keeper_Of_The_Kaidan_Key extends QuestHandler
{
	private final static int questId = 1042;
	private final static int[] npcs = {203989, 203901, 730342};
	
	public _1042Keeper_Of_The_Kaidan_Key() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182201026, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } 
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1040, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203989) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 185);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730342) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 1) {
							giveQuestItem(env, 182201026, 1);
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							PacketSendUtility.sendWhiteMessage(player, "Read <Kalabar's Order> for the next quest step!!!");
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 203901) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 2, 2, true, 5, 1438);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 1) {
				removeQuestItem(env, 182201026, 1);
				///Kalabar controls the Abyss Gate!
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111001, player.getObjectId(), 2));
                return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false));
            }
        }
        return HandlerResult.FAILED;
    }
}