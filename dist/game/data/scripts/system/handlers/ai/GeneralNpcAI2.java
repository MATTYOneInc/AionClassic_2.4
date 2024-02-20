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
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("general")
public class GeneralNpcAI2 extends NpcAI2
{
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}
	
	@Override
	protected void handleDied() {
		DiedEventHandler.onDie(this);
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		AttackEventHandler.onAttack(this, creature);
	}
	
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	@Override
	protected void handleDialogStart(Player player) {
		TalkEventHandler.onTalk(this, player);
		switch (getNpcId()) {
			///Sanctum.
			case 203895: //Lamid.
			///Pandaemonium.
			case 204268: //Gaminart.
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 10));
			break;
			///Fountain Reward.
			case 730142: //Teminon Coin Fountain.
			case 730143: //Primum Coin Fountain.
			case 730241: //Inggison Coin Fountain.
			case 730242: //Gelkmaros Coin Fountain.
				if (player.getInventory().getFirstItemByItemId(186000031) != null) { //Silver Medal.
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 10));
				} else {
					///You do not have enough Medals.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390257));
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
			///Enraged Fragment Menotios.
			case 218982:
			    if (player.getInventory().getFirstItemByItemId(185000117) != null) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1352));
				}
			break;
			///Sanctum.
			case 203899: //Opeia.
			///Pandaemonium.
			case 204276: //Manemori.
			///Haramel.
			case 730321: //Towerlift.
			///Kromede's Trials.
			case 700922: //Torture Chamber Door.
			case 700926: //Bedroom Door.
			case 700927: //Forbidden Book Repository Door.
			case 700946: //Tortured Captive.
			case 700947: //Tortured Prisoner.
			case 730336: //Garden Fountain.
			case 730337: //Fruit Basket.
			case 730338: //Porgus Barbecue.
			case 730339: //Prophet's Tower.
			///Beshmundir Temple.
			case 799517: //Plegeton Boatman.
			case 799518: //Plegeton Boatman.
			case 799519: //Plegeton Boatman.
			case 799520: //Plegeton Boatman.
			///Esoterrace.
			case 730381: //Murugan's Mailbox.
			///Empyrean Crucible.
			case 205331:
			case 205332:
			case 205333:
			case 205334:
			case 205335:
			case 205336:
			case 205337:
			case 205338:
			case 205339:
			case 205340:
			case 205341:
			case 205342:
			case 205343:
			case 205344:
			case 799567:
			case 799568:
			case 799569:
			///Crucible Challenge.
			case 217820:
			case 205666:
			case 205667:
			case 205668:
			case 205669:
			case 205670:
			case 205671:
			case 205672:
			case 205673:
			case 205674:
			case 205675:
			case 205676:
			case 205677:
			case 205678:
			case 205679:
			case 730459:
			///Phanoe Gate.
			case 730296:
			case 730297:
			///Dimaia Gate.
			case 730298:
			case 730299:
			///Spiritfall Gate.
			case 730300:
			case 730301:
			///Subterranea Gate.
			case 730302:
			case 730303:
			///Tempus.
			case 800535:
			case 800536:
			    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
			break;
			///Taloc's Hollow.
			case 730232: //Writhing Cocoon.
			case 730233: //Writhing Cocoon.
				if (player.getInventory().getFirstItemByItemId(185000088) != null) { //Shishir's Corrosive Fluid.
				    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1097));
				}
			break;
			///Empyrean Crucible.
			case 799573:
			case 205426:
			case 205427:
			case 205428:
			case 205429:
			case 205430:
			case 205431:
			///Crucible Challenge.
			case 205682:
			case 205683:
			case 205684:
			case 205663:
			case 205686:
			case 205687:
			case 205685:
			    if (player.getInventory().getFirstItemByItemId(186000124) != null) { //Worthiness Ticket.
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1097));
				}
			break;
			///Tempus.
			case 800581:
			case 800582:
			case 800583:
			    if (player.getInventory().getFirstItemByItemId(185000123) != null) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1352));
				}
			break;
			///Kromede's Trials.
			case 700924: //Secret Safe Door.
			    if (player.getInventory().getFirstItemByItemId(185000101) != null) { //Secret Safe Key.
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1352));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
			case 700961: //Grave's Robber Corpse.
			    if (player.getInventory().getFirstItemByItemId(164000141) != null) { //Silver Blade Rotan.
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1097));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
			case 730308: //Maga's Potion.
			    if (player.getInventory().getFirstItemByItemId(185000109) != null) { //Relic Key.
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 27));
				}
			break;
			case 730325: //Sleep Flower.
			    if (player.getInventory().getFirstItemByItemId(164000142) != null) { //Sapping Pollen.
				    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 27));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
			case 730340: //Old Relic Chest.
			    if (player.getInventory().getFirstItemByItemId(164000140) != null) { //Explosive Bead.
				    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 27));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
			case 730341: //Maga's Potion.
			    if (player.getInventory().getFirstItemByItemId(164000143) != null) { //Maga's Potion.
				    PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 27));
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getOwner().getObjectId(), 1011));
				}
			break;
		}
	}
	
	@Override
	protected void handleDialogFinish(Player creature) {
		TalkEventHandler.onFinishTalk(this, creature);
	}
	
	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
	}
	
	@Override
	protected void handleAttackComplete() {
		AttackEventHandler.onAttackComplete(this);
	}
	
	@Override
	protected void handleTargetReached() {
		TargetEventHandler.onTargetReached(this);
	}
	
	@Override
	protected void handleNotAtHome() {
		ReturningEventHandler.onNotAtHome(this);
	}
	
	@Override
	protected void handleBackHome() {
		ReturningEventHandler.onBackHome(this);
	}
	
	@Override
	protected void handleTargetTooFar() {
		TargetEventHandler.onTargetTooFar(this);
	}
	
	@Override
	protected void handleTargetGiveup() {
		TargetEventHandler.onTargetGiveup(this);
	}
	
	@Override
	protected void handleTargetChanged(Creature creature) {
		super.handleTargetChanged(creature);
		TargetEventHandler.onTargetChange(this, creature);
	}
	
	@Override
	protected void handleMoveValidate() {
		MoveEventHandler.onMoveValidate(this);
	}
	
	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		MoveEventHandler.onMoveArrived(this);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		CreatureEventHandler.onCreatureMoved(this, creature);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}
	
	@Override
	protected boolean canHandleEvent(AIEventType eventType) {
		boolean canHandle = super.canHandleEvent(eventType);
		switch (eventType) {
			case CREATURE_MOVED:
				return canHandle || DataManager.NPC_SHOUT_DATA.hasAnyShout(getOwner().getWorldId(), getOwner().getNpcId(), ShoutEventType.SEE);
			case CREATURE_NEEDS_SUPPORT:
				return canHandle && isNonFightingState() && DataManager.TRIBE_RELATIONS_DATA.hasSupportRelations(getOwner().getTribe());
		}
		return canHandle;
	}
	
	@Override
	public AttackIntention chooseAttackIntention() {
		VisibleObject currentTarget = getTarget();
		Creature mostHated = getAggroList().getMostHated();
		if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		} if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		} if (getOwner().getObjectTemplate().getAttackRange() == 0) {
			NpcSkillEntry skill = getOwner().getSkillList().getRandomSkill();
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		} else {
			NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
}