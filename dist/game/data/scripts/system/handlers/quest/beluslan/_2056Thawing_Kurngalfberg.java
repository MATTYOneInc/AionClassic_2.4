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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2056Thawing_Kurngalfberg extends QuestHandler
{
	private final static int questId = 2056;
	private final static int[] npcs = {204753, 790016, 730036, 279000};
	
	public _2056Thawing_Kurngalfberg() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(243, questId);
		qe.registerOnMovieEndQuest(244, questId);
		qe.registerOnMovieEndQuest(245, questId);
		qe.registerQuestItem(182204313, questId);
		qe.registerQuestItem(182204314, questId);
		qe.registerQuestItem(182204315, questId);
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
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204753) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 1) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_ACTION_1012: {
						playQuestMovie(env, 242);
						return sendQuestDialog(env, 1012);
					} case SELECT_ACTION_2376: {
						if (QuestService.collectItemCheck(env, false)) {
							return sendQuestDialog(env, 2376);
						} else {
							return sendQuestDialog(env, 2461);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730036) {
				long anoramasEssence = player.getInventory().getItemCountByItemId(182204313);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1353: {
						if (anoramasEssence == 0) {
							giveQuestItem(env, 182204313, 1);
							return sendQuestDialog(env, 1353);
						} else if (anoramasEssence == 1) {
							return sendQuestDialog(env, 1438);
						}
					}
				}
			} if (targetId == 279000) {
				long triminosEssence = player.getInventory().getItemCountByItemId(182204314);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1694: {
						if (triminosEssence == 0) {
							giveQuestItem(env, 182204314, 1);
							return sendQuestDialog(env, 1694);
						} else if (triminosEssence == 1) {
							return sendQuestDialog(env, 1779);
						}
					}
				}
			} if (targetId == 790016) {
				long etunEssence = player.getInventory().getItemCountByItemId(182204315);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 2034);
						}
					} case SELECT_ACTION_2035: {
						if (etunEssence == 0) {
							giveQuestItem(env, 182204315, 1);
							return sendQuestDialog(env, 2035);
						} else if (etunEssence == 1) {
							return sendQuestDialog(env, 2120);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204753) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (movieId == 243) {
			qs.setQuestVar(3);
			updateQuestStatus(env);
            return true;
        } else if (movieId == 244) {
			qs.setQuestVar(4);
			updateQuestStatus(env);
            return true;
        } else if (movieId == 245) {
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
            return true;
        }
        return false;
    }
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		if (id != 182204313 && qs.getQuestVarById(0) == 2 || id != 182204314 && qs.getQuestVarById(0) == 3 || id != 182204315 && qs.getQuestVarById(0) == 4) {
			return HandlerResult.UNKNOWN;
		} else if (!player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2056"))) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 2000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (qs.getQuestVarById(0) == 2) {
					playQuestMovie(env, 243);
					removeQuestItem(env, 182204313, 1);
				} else if (qs.getQuestVarById(0) == 3) {
					playQuestMovie(env, 244);
					removeQuestItem(env, 182204314, 1);
				} else if (qs.getQuestVarById(0) == 4) {
					playQuestMovie(env, 245);
					removeQuestItem(env, 182204315, 1);
				}
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
			}
		}, 2000);
		return HandlerResult.SUCCESS;
	}
}