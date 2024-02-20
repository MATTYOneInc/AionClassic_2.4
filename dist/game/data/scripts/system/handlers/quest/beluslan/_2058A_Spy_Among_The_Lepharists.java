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
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2058A_Spy_Among_The_Lepharists extends QuestHandler
{
	private final static int questId = 2058;
	private final static int[] npcs = {204774, 204809, 700359};
	private final static int[] DF3_Powerplant_Q2058 = {700349};
	
	public _2058A_Spy_Among_The_Lepharists() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(250, questId);
		qe.registerQuestItem(182204317, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF3_Powerplant_Q2058) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2053, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (qs.getStatus() == QuestStatus.START) {
				if (targetId == 204774) {
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						} case STEP_TO_1: {
							playQuestMovie(env, 249);
							changeQuestStep(env, 0, 1, false);
							return closeDialogWindow(env);
						}
					}
				} if (targetId == 204809) {
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						} case STEP_TO_2: {
							giveQuestItem(env, 182204317, 1);
							changeQuestStep(env, 1, 2, false);
							SkillEngine.getInstance().applyEffectDirectly(1863, player, player, 3600000); //Lepharist Disguise.
							return closeDialogWindow(env);
						}
					}
				} if (targetId == 700359) {
					switch (env.getDialog()) {
						case USE_OBJECT: {
							if (var == 2) {
								TeleportService2.teleportTo(env.getPlayer(), 220040000, 1757.0000f, 1392.0000f, 401.0000f, (byte) 94);
								return useQuestObject(env, 2, 3, false, 0, 250);
							}
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204774) {
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
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 700349, 3, 4);
	}
	
	@Override
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182204317) {
            if (var == 4 && player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2058"))) {
				player.getEffectController().removeEffect(1863); //Lepharist Disguise.
				return HandlerResult.fromBoolean(useQuestItem(env, item, 4, 4, true, 251));
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 2 && var < 4) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}