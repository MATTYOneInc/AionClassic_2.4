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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.S_STORE_PURCHASE_INFO;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogService
{
	private static final Logger log = LoggerFactory.getLogger(DialogService.class);
	
	public static void onCloseDialog(Npc npc, Player player) {
		switch (npc.getObjectTemplate().getTitleId()) {
			case 350409:
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
				Legion legion = player.getLegion();
				if (legion != null) {
					LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
					if (lwh.getWhUser() == player.getObjectId()) {
						lwh.setWhUser(0);
					}
				}
			break;
			case 350397:
			case 350398:
			case 350399:
			case 350400:
			case 350401:
			case 350402:
			case 350403:
			case 350404:
			case 350405:
			case 350406:
			case 350407:
			case 350408:
			case 350410:
			case 350411:
			case 350412:
			case 350413:
			case 350414:
			case 350415:
			case 350416:
			case 350417:
			case 350418:
			case 350419:
			case 350420:
			case 350421:
			case 350422:
			case 350423:
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	public static void onDialogSelect(int dialogId, final Player player, final Npc npc, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env)) {
			return;
		} if (player.isGM()) {
			PacketSendUtility.sendMessage(player, "<Quest Id>: " + questId);
			PacketSendUtility.sendMessage(player, "<Dialog Id>: " + dialogId);
		}
		int targetObjectId = npc.getObjectId();
		int npcNameId = npc.getObjectTemplate().getNameId();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		switch (dialogId) {
			case 2: {
				//Buy Item's.
				int level = player.getLevel();
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "<Trade List> is missing !!!");
					break;
				} switch (npc.getNpcId()) {
					///<Iron Combat Administration Officer Elyos>
					case 203179: //Anontrite.
					case 203330: //Laksis.
					    if (player.getInventory().getItemCountByItemId(186000001) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Iron Combat Administration Officer Asmodians>
					case 203659: //Lateni.
					case 203689: //Lohaban.
					    if (player.getInventory().getItemCountByItemId(186000006) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Bronze Combat Administration Officer Elyos>
					case 203931: //Ferenna.
					case 798111: //Atro.
					    if (player.getInventory().getItemCountByItemId(186000002) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Bronze Combat Administration Officer Asmodians>
					case 204360: //Nott.
					case 204368: //Cliessa.
					case 204425: //Bevna.
					case 204426: //Skataon.
					    if (player.getInventory().getItemCountByItemId(186000007) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Silver Administration Officer Elyos>
					case 203964: //Agrips.
					case 204011: //Sandinas.
					case 204515: //Belakade.
					case 204571: //Lomulias.
					case 204637: //Keas.
					case 204639: //Serdy.
					case 798112: //Metea.
					case 798113: //Anobi.
					    if (player.getInventory().getItemCountByItemId(186000003) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Silver Administration Officer Asmodians>
					case 204359: //Royaa.
					case 204427: //Sigen.
					case 204781: //Govanon.
					case 204789: //Lewin.
					    if (player.getInventory().getItemCountByItemId(186000008) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Gold Administration Officer Elyos>
					case 204548: //Euripia.
					case 204638: //Tolemos.
					    if (player.getInventory().getItemCountByItemId(186000004) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Gold Administration Officer Asmodians>
					case 204761: //Cinnia.
					case 204762: //Coventina.
					case 204766: //Maponus.
					case 204767: //Art.
					    if (player.getInventory().getItemCountByItemId(186000009) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Platinum Administration Officer Elyos>
					case 798172: //Eunomia.
					case 798173: //Euterpe.
					case 798174: //Guneus.
					case 798175: //Charites.
					case 798195: //Iphigenia.
					case 798196: //Illithyia.
					case 798197: //Neoptolemos.
					case 798198: //Calliope.
					    if (player.getInventory().getItemCountByItemId(186000005) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Platinum Administration Officer Asmodians>
					case 205160: //Nagel.
					case 205161: //Huvat.
					case 205162: //Bern.
					case 205163: //Rumolt.
					case 205183: //Signa.
					case 205184: //Pogel.
					case 205185: //Flazetta.
					case 205186: //Forseti.
					    if (player.getInventory().getItemCountByItemId(186000010) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Mithril Administration Officer Elyos>
					case 798914: //Eduardo.
					case 798915: //Giuseppe.
					case 798916: //Silva.
					case 798917: //Amauri.
					    if (player.getInventory().getItemCountByItemId(186000018) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					///<Mithril Administration Officer Asmodians>
					case 799213: //Nordin.
					case 799214: //Flores.
					case 799215: //Godin.
					case 799216: //Assulin.
					    if (player.getInventory().getItemCountByItemId(186000019) == 0) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
				}
				///Abyss Stigma/Stuff Seller's
				switch (npc.getNpcId()) {
					case 203708: //Iocaste.
					case 203710: //Dairos.
					    if (level < 25) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					case 798505: //Salvius.
					case 798509: //Papinius.
					case 798510: //Ecocia.
					case 799224: //Camila.
					    if (level < 45) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					case 798507: //Gracus.
					case 798508: //Tororite.
					    if (level < 50) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
					case 798506: //Elmaia.
					    if (level < 55) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    ///%0 does not sell items appropriate for you.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300336, new DescriptionId(npcNameId * 2 + 1)));
						    return;
					    }
                    break;
				}
				int tradeModifier = tradeListTemplate.getSellPriceRate();
				PacketSendUtility.sendPacket(player, new S_STORE_SALE_INFO(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier() * tradeModifier / 100));
                return;
			}
			case 3:
			case 10256: {
				//Sell Item's.
				int level = player.getLevel();
				switch (npc.getNpcId()) {
					case 798509: //Papinius.
					case 798510: //Ecocia.
					case 799224: //Camila.
					    if (level < 45) {
						    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
						    return;
					    }
                    break;
				}
				PacketSendUtility.sendPacket(player, new S_STORE_PURCHASE_INFO(targetObjectId, PricesService.getVendorSellModifier(player.getRace())));
				break;
			}
			case 4: {
				//Stigma Open.
				if (player.getRace() == Race.ELYOS && playerClass != PlayerClass.THUNDERER) {
					final QuestState qs1929 = player.getQuestStateList().getQuestState(1929); //A Sliver Of Darkness.
					if (qs1929 == null || qs1929.getStatus() != QuestStatus.COMPLETE) {
						///You cannot use it as the required quest has not been completed.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
					} else {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 1));
					}
				} else if (player.getRace() == Race.ASMODIANS && playerClass != PlayerClass.THUNDERER) {
					final QuestState qs2900 = player.getQuestStateList().getQuestState(2900); //No Escaping Destiny.
					if (qs2900 == null || qs2900.getStatus() != QuestStatus.COMPLETE) {
						///You cannot use it as the required quest has not been completed.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
					} else {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 1));
					}
				}
				break;
			}
			case 5: {
				//Create Legion.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 2));
				break;
			}
			case 6: {
				//Disband Legion.
				LegionService.getInstance().requestDisbandLegion(npc, player);
				break;
			}
			case 7: {
				//Recreate Legion.
				LegionService.getInstance().recreateLegion(npc, player);
				break;
			}
			case 21: {
				//Warehouse.
				if (!RestrictionsManager.canUseWarehouse(player)) {
					return;
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 26));
				WarehouseService.sendWarehouseInfo(player, true);
				break;
			}
			case 26: {
				//Quest.
				if (questId != 0) {
					final QuestState qs = player.getQuestStateList().getQuestState(questId);
					if (qs != null) {
						if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
							if (!"useitem".equals(npc.getAi2().getName())) {
								PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 10));
							} else {
								PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 0));
							}
						}
					}
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 10));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 0));
				}
				break;
			}
			case 28: {
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 13));
				break;
			}
			case 30: {
				//Soul Healing.
				final long expLost = player.getCommonData().getExpRecoverable();
				final double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
				final int price = (int) (expLost * factor);
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (player.getInventory().getKinah() >= price) {
							///You have gained %num0 XP.
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_GET_EXP2(expLost));
							///You received Soul Healing.
							PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_SUCCESS_RECOVER_EXPERIENCE, 2000);
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(price);
							PlayerEffectController effectController = player.getEffectController();
							effectController.removeEffect(8291);
							player.getCommonData().setDeathCount(0);
						} else {
							///You don't have enough Kinah. It costs %num0 Kinah.
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_KINA(price));
						}
					}
					@Override
					public void denyRequest(Creature requester, Player responder) {
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(S_ASK.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_ASK_RECOVER_EXPERIENCE, 0, 0, String.valueOf(price)));
					}
				} else {
					///You do not have any XP to recover.
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_DONOT_HAVE_RECOVER_EXPERIENCE, 0);
				} if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				break;
			} case 31: {
				//Arena City Teleporter.
				int level = player.getLevel();
				switch (npc.getNpcId()) {
					case 204089: //Garm.
						TeleportService2.teleportTo(player, 120010000, 984.0000f, 1543.0000f, 222.0000f, (byte) 0);
					break;
					case 203764: //Epeios.
						TeleportService2.teleportTo(player, 110010000, 1462.0000f, 1326.0000f, 564.0000f, (byte) 0);
					break;
					case 203981: //Meneus.
				        if (player.getRace() == Race.ELYOS) {
					        final QuestState qs1346 = player.getQuestStateList().getQuestState(1346); //Killing For Castor.
					        if (qs1346 == null || qs1346.getStatus() != QuestStatus.COMPLETE) {
								///You cannot use it as the required quest has not been completed.
						        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
						        PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
					        } else {
						        TeleportService2.teleportTo(player, 210020000, 439.0000f, 422.0000f, 274.0000f, (byte) 0);
					        }
						}
				    break;
			    }
			} case 32: {
				//Arena City Teleporter.
				switch (npc.getNpcId()) {
					case 204087: //Gunnar.
						TeleportService2.teleportTo(player, 120010000, 1005.0000f, 1528.0000f, 222.0000f, (byte) 0);
					break;
					case 203875: //Nepis.
						TeleportService2.teleportTo(player, 110010000, 1470.0000f, 1343.0000f, 563.0000f, (byte) 0);
					break;
					case 203982: //Ipetos.
						TeleportService2.teleportTo(player, 210020000, 446.0000f, 431.0000f, 274.0000f, (byte) 0);
					break;
				}
				break;
			}
			case 36: {
				//Godstone Socketing.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 21));
				break;
			}
			case 37: {
				//Remove Manastone.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 20));
				break;
			}
			case 38: {
				//Modify Appearance.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 19));
				break;
			}
			case 39: {
				//Flight & Teleport.
				switch (npc.getNpcId()) {
					case 203194: //Daines [Poeta]
						if (player.getRace() == Race.ELYOS) {
							final QuestState qs1006 = player.getQuestStateList().getQuestState(1006);
							if (qs1006 == null || qs1006.getStatus() != QuestStatus.COMPLETE) {
								///You cannot use it as the required quest has not been completed.
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
								PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
							} else {
								TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
							}
						}
					break;
					case 203679: //Osmar [Ishalgen]
						if (player.getRace() == Race.ASMODIANS) {
							final QuestState qs2008 = player.getQuestStateList().getQuestState(2008);
							if (qs2008 == null || qs2008.getStatus() != QuestStatus.COMPLETE) {
								///You cannot use it as the required quest has not been completed.
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
								PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 27));
							} else {
								TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
							}
						}
					break;
					case 203895: //Lamid [Sanctum]
						TeleportService2.teleportTo(player, 110010000, 2004.0000f, 1479.0000f, 591.0000f, (byte) 115);
					break;
					case 204268: //Gaminart [Pandaemonium]
						TeleportService2.teleportTo(player, 120010000, 1392.0000f, 1060.0000f, 206.0000f, (byte) 0);
					break;
					default: {
						TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
					}
				}
				break;
			}
			case 40:
			case 41: {
				//Learn Craft.
				//Improve Extraction.
				CraftSkillUpdateService.getInstance().learnSkill(player, npc);
				break;
			}
			case 42: {
				//Expand Cube.
				CubeExpandService.expandCube(player, npc);
				break;
			}
			case 43: {
				//Expand Warehouse.
				WarehouseService.expandWarehouse(player, npc);
				break;
			}
			case 48: {
				//Legion Warehouse.
				if (player.getLegion() == null) {
				    ///You must be a Legion member to use the Legion warehouse.
				    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_NO_GUILD_TO_DEPOSIT);
				    return;
				}
				LegionService.getInstance().openLegionWarehouse(player, npc);
				break;
			}
			case 51: {
				//Close Legion Warehouse.
				break;
			}
			case 53: {
			    //Work Order.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 28));
				break;
			}
			case 54: {
				//Coin's Reward.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 3));
				break;
			}
			case 56:
			case 57: {
				byte changesex = 0;
				byte check_ticket = 2;
				if (dialogId == 62) {
					//Gender Switch.
					changesex = 1;
					if (player.getInventory().getItemCountByItemId(169660000) > 0 || //Gender Switch Ticket
					    player.getInventory().getItemCountByItemId(169660001) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660002) > 0 || //Gender Switch Ticket (60 min)
						player.getInventory().getItemCountByItemId(169660003) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660004) > 0 || //[Event] Gender Switch Ticket
						player.getInventory().getItemCountByItemId(169660005)  > 0) { //Gender Switch Ticket
						check_ticket = 1;
					}
				} else {
					//Plastic Surgery.
					if (player.getInventory().getItemCountByItemId(169650000) > 0 || //Plastic Surgery Ticket
					    player.getInventory().getItemCountByItemId(169650001) > 0 || //[Event] Plastic Surgery Ticket
				        player.getInventory().getItemCountByItemId(169650002) > 0 || //[Special] Plastic Surgery Ticket
				        player.getInventory().getItemCountByItemId(169650003) > 0 || //[Special] Plastic Surgery Ticket
			            player.getInventory().getItemCountByItemId(169650004) > 0 || //[Daevanion Coupon] Plastic Surgery Ticket
				        player.getInventory().getItemCountByItemId(169650005) > 0 || //Plastic Surgery Ticket (60 mins)
						player.getInventory().getItemCountByItemId(169650006) > 0 || //[Event] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650007) > 0 || //[Event] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650008) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650009) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169650010) > 0 || //Plastic Surgery Ticket (60 mins)
						player.getInventory().getItemCountByItemId(169650011) > 0 || //[Stamp] Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(169691000) > 0 || //Plastic Surgery Ticket
						player.getInventory().getItemCountByItemId(186000449) > 0) { //Plastic Surgery Ticket
						check_ticket = 1;
					}
				}
				PacketSendUtility.sendPacket(player, new S_EDIT_CHARACTER(player, check_ticket, changesex));
				player.setEditMode(true);
				break;
			}
			case 61: {
				//Armsfusion.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 29));
				break;
			}
			case 62: {
				//Armsbreaking.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 30));
				break;
			}
			case 63: {
				//Join Faction.
				player.getNpcFactions().enterGuild(npc);
				break;
			}
			case 64: {
				//Leave Faction.
				player.getNpcFactions().leaveNpcFaction(npc);
				break;
			}
			case 65: {
				//Repurchase.
				PacketSendUtility.sendPacket(player, new S_USER_SELL_HISTORY_LIST(player, npc.getObjectId()));
				break;
			}
			case 66: {
				//Adopt Pet.
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(6));
				break;
			}
			case 67: {
				//Surrender Pet.
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(7));
				break;
			}
			case 68: {
				//Housing Build.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 32));
				break;
			}
			case 69: {
				//Housing Destruct.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 33));
				break;
			}
			case 70: {
				//Deep Conditioning Individual Item.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, 35));
				break;
			}
			case 71: {
				//Deep Conditioning All Item.
				ItemChargeService.startChargingEquippedItems(player, targetObjectId, 1);
				break;
			}
			case 73: {
				//Trade In.
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "<Trade In List> is missing !!!");
					break;
				}
				PacketSendUtility.sendPacket(player, new S_TRADE_IN(npc, tradeListTemplate, 100));
				break;
			}
			case 74: {
				//Purification Ritual.
				switch (npc.getNpcId()) {
					case 203752:
					case 204075:
						if (!player.getEffectController().hasAbnormalEffect(8610) || player.getEffectController().hasAbnormalEffect(8611)) {
							///Nothing to purify.
							PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_PURIFY_DONOT, 0);
						} if (player.getInventory().getKinah() >= 10000) {
						    if (player.getEffectController().hasAbnormalEffect(8610) || player.getEffectController().hasAbnormalEffect(8611)) {
								PlayerEffectController effectController = player.getEffectController();
								effectController.removeEffect(8610);
								effectController.removeEffect(8611);
								player.getInventory().decreaseKinah(10000);
								///Purified by the Empyrean Lord's authority.
								PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_PURIFY_SUCCESS, 2000);
							}
						} else {
							///You don't have enough Kinah. It costs %num0 Kinah.
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_KINA(10000));
						}
                    break;
				}
				break;
			}
			case 79: {
				//Give Up Craft Expert.
				RelinquishCraftStatus.getInstance();
				RelinquishCraftStatus.relinquishExpertStatus(player, npc);
				break;
			}
			case 80: {
				//Give Up Craft Master.
				RelinquishCraftStatus.getInstance();
				RelinquishCraftStatus.relinquishMasterStatus(player, npc);
				break;
			}
			case 10000:
			case 10001:
			case 10002:
			case 10003:
			case 10004:
			case 10005:
			case 10006:
			case 10007:
			case 10008:
			case 10009:
			case 10010:
			case 10011:
			case 10012:
			case 10013: {
				if (questId == 0) {
					TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npc.getNpcId());
					PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(npc.getNpcId(), dialogId, player.getRace());
					if (portalPath != null) {
						PortalService.port(portalPath, player, targetObjectId);
					} else if (template != null) {
						TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
						if (loc != null) {
							TeleportService2.teleport(template, loc.getLocId(), player, npc, npc.getAi2().getName().equals("general") ? TeleportAnimation.JUMP_ANIMATION : TeleportAnimation.BEAM_ANIMATION);
						}
					}
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, dialogId, questId));
				}
				break;
			} default: {
				if (questId > 0) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, dialogId, questId));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(targetObjectId, dialogId));
				}
				break;
			}
		}
	}
}