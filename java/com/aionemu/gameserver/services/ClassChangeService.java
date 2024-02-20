/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_QUEST;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.sql.Timestamp;
import java.util.Calendar;

public class ClassChangeService
{
	public static void showClassChangeDialog(Player player) {
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			PlayerClass playerClass = player.getPlayerClass();
			Race playerRace = player.getRace();
			if (player.getLevel() >= 9 && playerClass.isStartingClass()) {
				if (playerRace == Race.ELYOS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 2375, 1006));
						break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 2716, 1006));
						break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 3057, 1006));
						break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 3398, 1006));
						break;
					}
				} else if (playerRace == Race.ASMODIANS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 3057, 2008));
						break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 3398, 2008));
						break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 3739, 2008));
						break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 4080, 2008));
						break;
					}
				}
			}
		}
	}
	
	public static void changeClassToSelection(final Player player, final int dialogId) {
		Race playerRace = player.getRace();
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			if (playerRace == Race.ELYOS) {
				switch (dialogId) {
					case 2376:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
					break;
					case 2461:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
					break;
					case 2717:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
					break;
					case 2802:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
					break;
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
					break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
					break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
					break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
					break;
				}
				completeQuest(player, 1006);
				completeQuest(player, 1007);
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 1929);
				}
			} else if (playerRace == Race.ASMODIANS) {
				switch (dialogId) {
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
					break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
					break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
					break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
					break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
					break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
					break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
					break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
					break;
				}
				completeQuest(player, 2008);
				completeQuest(player, 2009);
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 2900);
				}
			}
		}
	}
	
	public static void completeQuest(Player player, int questId) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		Calendar calendar = Calendar.getInstance();
		Timestamp timeStamp = new Timestamp(calendar.getTime().getTime());
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 1, null, 0, timeStamp));
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, QuestStatus.COMPLETE.value(), 0));
		} else {
			qs.setStatus(QuestStatus.COMPLETE);
			qs.setCompleteCount(qs.getCompleteCount() + 1);
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
	
	public static void setClass(Player player, PlayerClass playerClass) {
		if (validateSwitch(player, playerClass)) {
			player.getCommonData().setPlayerClass(playerClass);
			player.getController().upgradePlayer();
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(0, 0, 0));
		}
	}
	
	private static boolean validateSwitch(Player player, PlayerClass playerClass) {
		int level = player.getLevel();
		PlayerClass oldClass = player.getPlayerClass();
		if (level < 9 && oldClass != PlayerClass.MONK) {
			PacketSendUtility.sendMessage(player, "You can only switch class at level 9");
			return false;
		} if (level < 15 && oldClass == PlayerClass.MONK) {
			PacketSendUtility.sendMessage(player, "You can only switch class at level 15");
			return false;
		} if (!oldClass.isStartingClass()) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return false;
		} switch (oldClass) {
			case WARRIOR:
				if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
				break;
			case SCOUT:
				if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
				break;
			case MAGE:
				if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
				break;
			case PRIEST:
				if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
				break;
			case MONK:
				if (playerClass == PlayerClass.THUNDERER)
				break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return false;
		}
		return true;
	}
	
	//Elyos Quest/Mission.
	public static void onUpdateQuest1156(Player player) {
        if (player.getQuestStateList().hasQuest(1156)) {
			QuestState qs = player.getQuestStateList().getQuestState(1156);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new S_QUEST(1156, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest1158(Player player) {
        if (player.getQuestStateList().hasQuest(1158)) {
			QuestState qs = player.getQuestStateList().getQuestState(1158);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new S_QUEST(1158, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest1562(Player player) {
        if (player.getQuestStateList().hasQuest(1562)) {
			QuestState qs = player.getQuestStateList().getQuestState(1562);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new S_QUEST(1562, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest1647(Player player) {
        if (player.getQuestStateList().hasQuest(1647)) {
			QuestState qs = player.getQuestStateList().getQuestState(1647);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(0);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(1647, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest3036(Player player) {
        if (player.getQuestStateList().hasQuest(3036)) {
			QuestState qs = player.getQuestStateList().getQuestState(3036);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(0);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(3036, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest3082(Player player) {
        if (player.getQuestStateList().hasQuest(3082)) {
			QuestState qs = player.getQuestStateList().getQuestState(3082);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(3082, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest3711(Player player) {
        if (player.getQuestStateList().hasQuest(3711)) {
			QuestState qs = player.getQuestStateList().getQuestState(3711);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(3711, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest18212(Player player) {
        if (player.getQuestStateList().hasQuest(18212)) {
			QuestState qs = player.getQuestStateList().getQuestState(18212);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(18212, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest18602(Player player) {
        if (player.getQuestStateList().hasQuest(18602)) {
			QuestState qs = player.getQuestStateList().getQuestState(18602);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(18602, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest30211(Player player) {
        if (player.getQuestStateList().hasQuest(30211)) {
			QuestState qs = player.getQuestStateList().getQuestState(30211);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(30211, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest30255(Player player) {
        if (player.getQuestStateList().hasQuest(30255)) {
			QuestState qs = player.getQuestStateList().getQuestState(30255);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(30255, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1020(Player player) {
        if (player.getQuestStateList().hasQuest(1020)) {
			QuestState qs = player.getQuestStateList().getQuestState(1020);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(1020, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1041(Player player) {
        if (player.getQuestStateList().hasQuest(1041)) {
			QuestState qs = player.getQuestStateList().getQuestState(1041);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new S_QUEST(1041, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1057(Player player) {
        if (player.getQuestStateList().hasQuest(1057)) {
			QuestState qs = player.getQuestStateList().getQuestState(1057);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new S_QUEST(1057, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1074(Player player) {
        if (player.getQuestStateList().hasQuest(1074)) {
			QuestState qs = player.getQuestStateList().getQuestState(1074);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
				qs.setQuestVar(3);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(1074, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10005A(Player player) {
        if (player.getQuestStateList().hasQuest(10005)) {
			final QuestState qs = player.getQuestStateList().getQuestState(10005);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new S_QUEST(10005, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10005B(Player player) {
        if (player.getQuestStateList().hasQuest(10005)) {
			final QuestState qs = player.getQuestStateList().getQuestState(10005);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				PacketSendUtility.sendPacket(player, new S_QUEST(10005, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10006A(Player player) {
        if (player.getQuestStateList().hasQuest(10006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(10006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 8) {
				qs.setQuestVar(9);
				PacketSendUtility.sendPacket(player, new S_QUEST(10006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10006B(Player player) {
        if (player.getQuestStateList().hasQuest(10006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(10006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 10) {
				qs.setQuestVar(11);
				PacketSendUtility.sendPacket(player, new S_QUEST(10006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10006C(Player player) {
        if (player.getQuestStateList().hasQuest(10006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(10006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 12) {
				qs.setQuestVar(13);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(10006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10023(Player player) {
        if (player.getQuestStateList().hasQuest(10023)) {
			QuestState qs = player.getQuestStateList().getQuestState(10023);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 13) {
				qs.setQuestVar(14);
				PacketSendUtility.sendPacket(player, new S_QUEST(10023, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission10105(Player player) {
        if (player.getQuestStateList().hasQuest(10105)) {
			QuestState qs = player.getQuestStateList().getQuestState(10105);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(10105, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1100(Player player) {
        if (player.getQuestStateList().hasQuest(1100)) {
			QuestState qs = player.getQuestStateList().getQuestState(1100);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(1100, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission1130(Player player) {
        if (player.getQuestStateList().hasQuest(1130)) {
			QuestState qs = player.getQuestStateList().getQuestState(1130);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(1130, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	
	//Asmodians Quest/Mission.
	public static void onUpdateQuest2290(Player player) {
        if (player.getQuestStateList().hasQuest(2290)) {
			QuestState qs = player.getQuestStateList().getQuestState(2290);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(3);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(2290, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest2484(Player player) {
        if (player.getQuestStateList().hasQuest(2484)) {
			QuestState qs = player.getQuestStateList().getQuestState(2484);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new S_QUEST(2484, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest2633(Player player) {
        if (player.getQuestStateList().hasQuest(2633)) {
			QuestState qs = player.getQuestStateList().getQuestState(2633);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(2633, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest4711(Player player) {
        if (player.getQuestStateList().hasQuest(4711)) {
			QuestState qs = player.getQuestStateList().getQuestState(4711);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(4711, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest21114(Player player) {
        if (player.getQuestStateList().hasQuest(21114)) {
			QuestState qs = player.getQuestStateList().getQuestState(21114);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new S_QUEST(21114, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest28212(Player player) {
        if (player.getQuestStateList().hasQuest(28212)) {
			QuestState qs = player.getQuestStateList().getQuestState(28212);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(28212, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest28602(Player player) {
        if (player.getQuestStateList().hasQuest(28602)) {
			QuestState qs = player.getQuestStateList().getQuestState(28602);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(28602, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest30311(Player player) {
        if (player.getQuestStateList().hasQuest(30311)) {
			QuestState qs = player.getQuestStateList().getQuestState(30311);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(30311, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest30355(Player player) {
        if (player.getQuestStateList().hasQuest(30355)) {
			QuestState qs = player.getQuestStateList().getQuestState(30355);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(30355, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2022(Player player) {
        if (player.getQuestStateList().hasQuest(2022)) {
			QuestState qs = player.getQuestStateList().getQuestState(2022);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(2);
				PacketSendUtility.sendPacket(player, new S_QUEST(2022, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2035(Player player) {
        if (player.getQuestStateList().hasQuest(2035)) {
			QuestState qs = player.getQuestStateList().getQuestState(2035);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new S_QUEST(2035, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2074(Player player) {
        if (player.getQuestStateList().hasQuest(2074)) {
			QuestState qs = player.getQuestStateList().getQuestState(2074);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new S_QUEST(2074, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2076A(Player player) {
        if (player.getQuestStateList().hasQuest(2076)) {
			QuestState qs = player.getQuestStateList().getQuestState(2076);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				PacketSendUtility.sendPacket(player, new S_QUEST(2076, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2076B(Player player) {
        if (player.getQuestStateList().hasQuest(2076)) {
			QuestState qs = player.getQuestStateList().getQuestState(2076);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				PacketSendUtility.sendPacket(player, new S_QUEST(2076, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission20005A(Player player) {
        if (player.getQuestStateList().hasQuest(20005)) {
			final QuestState qs = player.getQuestStateList().getQuestState(20005);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				qs.setQuestVar(3);
				PacketSendUtility.sendPacket(player, new S_QUEST(20005, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission20005B(Player player) {
        if (player.getQuestStateList().hasQuest(20005)) {
			final QuestState qs = player.getQuestStateList().getQuestState(20005);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				qs.setQuestVar(8);
				PacketSendUtility.sendPacket(player, new S_QUEST(20005, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission20006A(Player player) {
        if (player.getQuestStateList().hasQuest(20006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(20006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 8) {
				qs.setQuestVar(9);
				PacketSendUtility.sendPacket(player, new S_QUEST(20006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission20006B(Player player) {
        if (player.getQuestStateList().hasQuest(20006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(20006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 10) {
				qs.setQuestVar(11);
				PacketSendUtility.sendPacket(player, new S_QUEST(20006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission20006C(Player player) {
        if (player.getQuestStateList().hasQuest(20006)) {
			final QuestState qs = player.getQuestStateList().getQuestState(20006);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 12) {
				qs.setQuestVar(13);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(20006, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateQuest41267(Player player) {
        if (player.getQuestStateList().hasQuest(41267)) {
			QuestState qs = player.getQuestStateList().getQuestState(41267);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				qs.setQuestVar(1);
				PacketSendUtility.sendPacket(player, new S_QUEST(41267, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2100(Player player) {
        if (player.getQuestStateList().hasQuest(2100)) {
			QuestState qs = player.getQuestStateList().getQuestState(2100);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(2100, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
	public static void onUpdateMission2200(Player player) {
        if (player.getQuestStateList().hasQuest(2200)) {
			QuestState qs = player.getQuestStateList().getQuestState(2200);
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new S_QUEST(2200, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
    }
}