package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.NpcInfos;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ReportToMany extends QuestHandler
{
	private boolean mission;
	private final int itemId;
	private final int startItem;
	private final int startItem2;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final int startDialog;
	private final int endDialog;
	private final int maxVar;
	private final FastMap<Integer, NpcInfos> npcInfos;
	
	public ReportToMany(int questId, int itemId, int startItem, int startItem2, List<Integer> startNpcIds, List<Integer> endNpcIds, FastMap<Integer, NpcInfos> npcInfos, int startDialog, int endDialog, int maxVar, boolean mission) {
		super(questId);
		this.itemId = itemId;
		this.startItem = startItem;
		this.startItem2 = startItem2;
		if (startNpcIds != null) {
			startNpcs.addAll(startNpcIds);
			startNpcs.remove(0);
		} if (endNpcIds != null) {
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		this.npcInfos = npcInfos;
		this.startDialog = startDialog;
		this.endDialog = endDialog;
		this.maxVar = maxVar;
		this.mission = mission;
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv questEnv) {
        return defaultOnLvlUpEvent(questEnv);
    }
	
	@Override
    public void register() {
        if (mission) {
            qe.registerOnLevelUp(getQuestId());
        } if (startItem != 0) {
            qe.registerQuestItem(startItem, getQuestId());
        } else if (startItem2 != 0) {
            qe.registerQuestItem(startItem2, getQuestId());
        } else {
            Iterator<Integer> iterator = startNpcs.iterator();
            while (iterator.hasNext()) {
                int startNpc = iterator.next();
                qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
                qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
            }
        } for (int npcId: npcInfos.keySet()) {
            qe.registerQuestNpc(npcId).addOnTalkEvent(getQuestId());
        }
        Iterator<Integer> iterator = endNpcs.iterator();
        while (iterator.hasNext()) {
            int endNpc = iterator.next();
            qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
        }
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (startItem != 0) {
                if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    QuestService.startQuest(env);
                    return closeDialogWindow(env);
                } else if (env.getDialog() == QuestDialog.REFUSE_QUEST) {
                    return closeDialogWindow(env);
                }
            } else if (startItem2 != 0) {
                if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    QuestService.startQuest(env);
                    return closeDialogWindow(env);
                } else if (env.getDialog() == QuestDialog.REFUSE_QUEST) {
                    return closeDialogWindow(env);
                }
            } if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (startDialog != 0) {
							return sendQuestDialog(env, startDialog);
						} else {
							return sendQuestDialog(env, 1011);
						}
                    } case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						if (player.getInventory().isFullSpecialCube()) {
							///Your inventory is full. Try again after making space.
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_REWARD_INVENTORY_FULL);
							return false;
						} else if (itemId != 0) {
							if (giveQuestItem(env, itemId, 1)) {
								return sendQuestStartDialog(env);
							}
							return false;
						} else {
							return sendQuestStartDialog(env);
						}
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					} default: {
                        return sendQuestStartDialog(env);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            NpcInfos targetNpcInfo = npcInfos.get(targetId);
            if (var <= maxVar) {
                if (targetNpcInfo != null && var == targetNpcInfo.getVar()) {
                    int closeDialog;
                    if (targetNpcInfo.getCloseDialog() == 0) {
                        closeDialog = 10000 + targetNpcInfo.getVar();
                    } else {
                        closeDialog = targetNpcInfo.getCloseDialog();
                    } if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, targetNpcInfo.getQuestDialog());
                    } else if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, targetNpcInfo.getQuestDialog());
                    } else if (dialog.id() == targetNpcInfo.getQuestDialog() + 1 && targetNpcInfo.getMovie() != 0) {
                        sendQuestDialog(env, targetNpcInfo.getQuestDialog() + 1);
                        return playQuestMovie(env, targetNpcInfo.getMovie());
                    } else if (dialog.id() == closeDialog) {
                        if ((dialog != QuestDialog.CHECK_COLLECTED_ITEMS) || QuestService.collectItemCheck(env, true)) {
                            if (var == maxVar) {
                                qs.setStatus(QuestStatus.REWARD);
                                if (closeDialog == 1009 || closeDialog == 34) {
                                    return sendQuestDialog(env, 5);
                                }
                            } else {
                                qs.setQuestVarById(0, var + 1);
                            }
                            updateQuestStatus(env);
                        }
                        return sendQuestSelectionDialog(env);
                    }
                }
            } else if (var > maxVar) {
                if (endNpcs.contains(targetId)) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, endDialog);
                    } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                        if (startItem != 0) {
                            if (!removeQuestItem(env, startItem, 1)) {
                                return false;
                            }
                        } else if (startItem2 != 0) {
                            if (!removeQuestItem(env, startItem2, 1)) {
                                return false;
                            }
                        } else if (itemId != 0) {
							if (!removeQuestItem(env, itemId, 1)) {
                                return false;
                            }
						}
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestEndDialog(env);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD && endNpcs.contains(targetId)) {
            NpcInfos targetNpcInfo = npcInfos.get(targetId);
            if (env.getDialog() == QuestDialog.USE_OBJECT && targetNpcInfo != null && targetNpcInfo.getQuestDialog() != 0) {
                return sendQuestDialog(env, targetNpcInfo.getQuestDialog());
            }
            return sendQuestEndDialog(env);
        }
        return false;
    }
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        if (startItem != 0) {
            final Player player = env.getPlayer();
            final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
            }
        } else if (startItem2 != 0) {
            final Player player = env.getPlayer();
            final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                return HandlerResult.fromBoolean(sendQuestDialog(env, 4762));
            }
        }
        return HandlerResult.UNKNOWN;
    }
}